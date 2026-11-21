package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.bean.JSaParCell;
import org.jsapar.bean.JSaParContainsCells;
import org.jsapar.bean.JSaParDetail;
import org.jsapar.bean.JSaParLine;
import org.jsapar.compose.ComposeException;
import org.jsapar.compose.bean.BeanComposer;
import org.jsapar.compose.bean.BeanFactoryByMap;
import org.jsapar.compose.bean.BeanFactoryDefault;
import org.jsapar.error.BeanException;
import org.jsapar.error.JSaParException;
import org.jsapar.model.CellType;
import org.jsapar.model.IntegerCell;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.schema.CsvSchema;
import org.jsapar.schema.CsvSchemaCell;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapping of lines to and from records.
 */
class RecordBeanTest {

    public enum Level {LOW, HIGH}

    @JSaParLine(lineType = "Customer")
    public record Customer(@JSaParCell(name = "name") String name,
                           @JSaParCell(name = "born") LocalDate born,
                           @JSaParCell(name = "limit") BigDecimal limit,
                           @JSaParCell(name = "visits") int visits,
                           @JSaParCell(name = "level") Level level) {
    }

    private static CsvSchema customerSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Customer")
                        .withCells(CsvSchemaCell.builder("name").build(),
                                CsvSchemaCell.builder("born").withType(CellType.LOCAL_DATE).build(),
                                CsvSchemaCell.builder("limit").withType(CellType.DECIMAL).build(),
                                CsvSchemaCell.builder("visits").withType(CellType.INTEGER).build(),
                                CsvSchemaCell.builder("level").build())
                        .build())
                .build();
    }

    private static <T> List<T> parse(Text2BeanConverter<T> converter, String input, List<JSaParException> errors) throws IOException {
        converter.setErrorConsumer(errors::add);
        List<T> beans = new ArrayList<>();
        try (Reader reader = new StringReader(input)) {
            converter.convertForEach(reader, (T bean) -> beans.add(bean));
        }
        return beans;
    }

    @Test
    void annotatedRecord_parse() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<Customer> customers = parse(new Text2BeanConverter<>(customerSchema(), Customer.class),
                "Ann;1970-01-02;1000.25;3;HIGH\nBob;;;;\n", errors);
        assertEquals(List.of(), errors);
        assertEquals(List.of(
                new Customer("Ann", LocalDate.of(1970, 1, 2), new BigDecimal("1000.25"), 3, Level.HIGH),
                new Customer("Bob", null, null, 0, null)), customers);
    }

    @Test
    void annotatedRecord_compose() throws IOException {
        BeanCollection2TextConverter<Customer> converter = new BeanCollection2TextConverter<>(customerSchema(),
                BeanMap.ofClass(Customer.class));
        StringWriter writer = new StringWriter();
        converter.convert(List.of(
                new Customer("Ann", LocalDate.of(1970, 1, 2), new BigDecimal("1000.25"), 3, Level.HIGH),
                new Customer("Bob", null, null, 0, null)), writer);
        assertEquals("Ann;1970-01-02;1000.25;3;HIGH" + System.lineSeparator() + "Bob;;;0;", writer.toString());
    }

    @Test
    void annotatedRecord_cellError_otherValuesAssigned() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<Customer> customers = parse(new Text2BeanConverter<>(customerSchema(), Customer.class),
                "Ann;1970-01-02;1000.25;3;MEDIUM\n", errors);
        assertEquals(1, errors.size());
        assertInstanceOf(ComposeException.class, errors.getFirst());
        assertEquals(List.of(new Customer("Ann", LocalDate.of(1970, 1, 2), new BigDecimal("1000.25"), 3, null)), customers);
    }

    /** Validates in the compact constructor. */
    @JSaParLine(lineType = "Customer")
    public record ValidatedCustomer(@JSaParCell(name = "name") String name) {
        public ValidatedCustomer {
            Objects.requireNonNull(name, "name");
        }
    }

    @Test
    void constructorThrows_beanSkipped() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<ValidatedCustomer> customers = parse(new Text2BeanConverter<>(customerSchema(), ValidatedCustomer.class),
                "Ann\n;1970-01-02\nBob\n", errors);
        assertEquals(List.of(new ValidatedCustomer("Ann"), new ValidatedCustomer("Bob")), customers);
        assertEquals(1, errors.size());
        assertInstanceOf(ComposeException.class, errors.getFirst());
        assertEquals(2, ((ComposeException) errors.getFirst()).getLine().getLineNumber());
    }

    /** Not public. */
    record Point(int x, int y) {
    }

    @Test
    void nonPublicRecord_bothDirections() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Point")
                        .withCells(CsvSchemaCell.builder("x").withType(CellType.INTEGER).build(),
                                CsvSchemaCell.builder("y").withType(CellType.INTEGER).build())
                        .build())
                .build();
        BeanMap beanMap = BeanMap.builder()
                .withLine("Point", Point.class, b -> b.withCell("x").withCell("y"))
                .build();
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Point(1, 2)), parse(new Text2BeanConverter<>(schema, beanMap), "1;2", errors));
        assertEquals(List.of(), errors);

        StringWriter writer = new StringWriter();
        new BeanCollection2TextConverter<Point>(schema, beanMap).convert(List.of(new Point(3, 4)), writer);
        assertEquals("3;4", writer.toString());
    }

    // ---------- Nested objects ----------

    public record Address(@JSaParCell(name = "street") String street,
                          @JSaParCell(name = "city") String city) {
    }

    @JSaParLine(lineType = "Person")
    public record Person(@JSaParCell(name = "name") String name,
                         @JSaParContainsCells Address address) {
    }

    /** A mutable bean in a record. */
    public static class Phone {
        @JSaParCell(name = "phone")
        private String number;

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
    }

    @JSaParLine(lineType = "Person")
    public record PersonWithPhone(@JSaParCell(name = "name") String name,
                                  @JSaParContainsCells Phone phone) {
    }

    /** A record in a mutable bean. */
    @JSaParLine(lineType = "Person")
    public static class MutablePerson {
        @JSaParCell(name = "name")
        private String name;
        @JSaParContainsCells
        private Address address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    private static CsvSchema personSchema() {
        return CsvSchema.builder()
                .withLine(CsvSchemaLine.builder("Person")
                        .withCells("name", "street", "city", "phone")
                        .build())
                .build();
    }

    @Test
    void nestedRecordInRecord_bothDirections() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<Person> persons = parse(new Text2BeanConverter<>(personSchema(), Person.class),
                "Ann;Main street 1;Springfield;\nBob;;;\n", errors);
        assertEquals(List.of(), errors);
        assertEquals(List.of(new Person("Ann", new Address("Main street 1", "Springfield")), new Person("Bob", null)),
                persons);

        StringWriter writer = new StringWriter();
        new BeanCollection2TextConverter<Person>(personSchema(), BeanMap.ofClass(Person.class))
                .convert(List.of(new Person("Ann", new Address("Main street 1", "Springfield"))), writer);
        assertEquals("Ann;Main street 1;Springfield;", writer.toString());
    }

    @Test
    void nestedBeanInRecord() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<PersonWithPhone> persons = parse(new Text2BeanConverter<>(personSchema(), PersonWithPhone.class),
                "Ann;;;555-1234\n", errors);
        assertEquals(List.of(), errors);
        assertEquals("Ann", persons.getFirst().name());
        assertEquals("555-1234", persons.getFirst().phone().getNumber());
    }

    @Test
    void nestedRecordInBean() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<MutablePerson> persons = parse(new Text2BeanConverter<>(personSchema(), MutablePerson.class),
                "Ann;Main street 1;Springfield;\nBob;;;\n", errors);
        assertEquals(List.of(), errors);
        assertEquals("Ann", persons.get(0).getName());
        assertEquals(new Address("Main street 1", "Springfield"), persons.get(0).getAddress());
        assertNull(persons.get(1).getAddress());
    }

    @Test
    void beanFactoryByMap_assignCellToRecord_fails() throws Exception {
        BeanFactoryByMap<Object> factory = new BeanFactoryByMap<>(BeanMap.ofClass(Customer.class));
        Line line = new Line("Customer");
        assertThrows(InstantiationException.class, () -> factory.createBean(line));
    }

    private static <T> String compose(Schema<?> schema, BeanMap beanMap, List<T> beans) throws IOException {
        StringWriter writer = new StringWriter();
        new BeanCollection2TextConverter<T>(schema, beanMap).convert(beans, writer);
        return writer.toString();
    }

    @Test
    void nestedBeanInRecord_compose() throws IOException {
        Phone phone = new Phone();
        phone.setNumber("555-1234");
        assertEquals("Ann;;;555-1234" + System.lineSeparator() + "Bob;;;",
                compose(personSchema(), BeanMap.ofClass(PersonWithPhone.class),
                        List.of(new PersonWithPhone("Ann", phone), new PersonWithPhone("Bob", null))));
    }

    @Test
    void nestedRecordInBean_compose() throws IOException {
        MutablePerson ann = new MutablePerson();
        ann.setName("Ann");
        ann.setAddress(new Address("Main street 1", "Springfield"));
        MutablePerson bob = new MutablePerson();
        bob.setName("Bob");
        assertEquals("Ann;Main street 1;Springfield;" + System.lineSeparator() + "Bob;;;",
                compose(personSchema(), BeanMap.ofClass(MutablePerson.class), List.of(ann, bob)));
    }

    /** No bean map: the line type is the class name and the cell names are the component names. */
    @Test
    void namesInSchema_compose() throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .withLine(CsvSchemaLine.builder(Contact.class.getName())
                        .withCells(CsvSchemaCell.builder("name").build(),
                                CsvSchemaCell.builder("age").withType(CellType.INTEGER).build(),
                                CsvSchemaCell.builder("address.street").build(),
                                CsvSchemaCell.builder("address.city").build())
                        .build())
                .build();
        StringWriter writer = new StringWriter();
        try (Bean2TextConverter<Contact> converter = new Bean2TextConverter<>(schema, writer)) {
            converter.convert(new Contact("Ann", 42, new Address("Main street 1", "Springfield")));
            converter.convert(new Contact("Bob", 7, null));
        }
        assertEquals("Ann;42;Main street 1;Springfield" + System.lineSeparator() + "Bob;7;;", writer.toString());

        // ...and back again, also without bean map.
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Contact("Ann", 42, new Address("Main street 1", "Springfield")),
                        new Contact("Bob", 7, null)),
                parse(new Text2BeanConverter<>(schema), writer.toString(), errors));
        assertEquals(List.of(), errors);
    }

    // ---------- Master and detail lines ----------

    @JSaParLine(lineType = "Order")
    public record Order(@JSaParCell(name = "orderId") long orderId,
                        @JSaParCell(name = "customer") String customer,
                        @JSaParDetail(lineType = "OrderLine", keyCell = "sku") Map<String, OrderLine> lines,
                        @JSaParDetail(lineType = "HistoryLog") List<HistoryLog> history) {
        public Order {
            // Typical for records: defensive immutable copies, which fail for null.
            lines = Map.copyOf(lines);
            history = List.copyOf(history);
        }
    }

    @JSaParLine(lineType = "OrderLine")
    public record OrderLine(@JSaParCell(name = "sku") String sku,
                            @JSaParCell(name = "quantity") int quantity,
                            @JSaParDetail(lineType = "Discount") List<Discount> discounts) {
    }

    @JSaParLine(lineType = "Discount")
    public record Discount(@JSaParCell(name = "percent") int percent) {
    }

    @JSaParLine(lineType = "HistoryLog")
    public record HistoryLog(@JSaParCell(name = "text") String text) {
    }

    private static Schema<?> masterDetailSchema() throws IOException {
        try (Reader reader = new FileReader("examples/09_CsvSchemaMasterDetail.xml", StandardCharsets.UTF_8)) {
            return Schema.ofXml(reader);
        }
    }

    private static final String ORDERS = """
            O;1001;2026-11-02;ACME
            L;SKU-1;2
            D;10
            H;Created by web shop
            L;SKU-7;1
            O;1002;2026-11-03;Globex
            L;SKU-3;5
            """;

    @Test
    void masterDetailRecords_parse() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = parse(new Text2BeanConverter<>(masterDetailSchema(), Order.class), ORDERS, errors);
        assertEquals(List.of(), errors);
        assertEquals(List.of(
                new Order(1001, "ACME",
                        Map.of("SKU-1", new OrderLine("SKU-1", 2, List.of(new Discount(10))),
                                "SKU-7", new OrderLine("SKU-7", 1, List.of())),
                        List.of(new HistoryLog("Created by web shop"))),
                new Order(1002, "Globex",
                        Map.of("SKU-3", new OrderLine("SKU-3", 5, List.of())),
                        List.of())), orders);
    }

    @Test
    void masterDetailRecords_compose() throws IOException {
        Order order = new Order(1001, "ACME",
                new LinkedHashMap<>(Map.of("SKU-1", new OrderLine("SKU-1", 2, List.of(new Discount(10))))),
                List.of(new HistoryLog("Created by web shop")));
        StringWriter writer = new StringWriter();
        new BeanCollection2TextConverter<Order>(masterDetailSchema(), BeanMap.ofClass(Order.class))
                .convert(List.of(order), writer);
        assertEquals("""
                O;1001;;ACME
                L;SKU-1;2
                D;10
                H;Created by web shop""", writer.toString());
    }

    // ---------- BeanFactoryDefault ----------

    public record Contact(String name, int age, Address address) {
    }

    private static List<Object> composeDefault(Line line, List<JSaParException> errors) {
        BeanComposer<Object> composer = new BeanComposer<>(new BeanFactoryDefault<>());
        composer.setErrorConsumer(errors::add);
        List<Object> beans = new ArrayList<>();
        composer.setBeanConsumer((Object bean) -> beans.add(bean));
        composer.composeLine(line);
        return beans;
    }

    @Test
    void beanFactoryDefault_record() {
        Line line = new Line(Contact.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new IntegerCell("age", 42))
                .addCell(new StringCell("address.street", "Main street 1"));
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Contact("Ann", 42, new Address("Main street 1", null))), composeDefault(line, errors));
        assertEquals(List.of(), errors);

        // Another line with other cells, mapping is extended.
        Line line2 = new Line(Contact.class.getName())
                .addCell(new StringCell("address.city", "Springfield"));
        assertEquals(List.of(new Contact(null, 0, new Address(null, "Springfield"))), composeDefault(line2, errors));
        assertEquals(List.of(), errors);
    }

    @Test
    void beanFactoryDefault_record_unknownCell() {
        Line line = new Line(Contact.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new StringCell("nickname", "Annie"))
                .addCell(new StringCell("class.name", "x"));
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Contact("Ann", 0, null)), composeDefault(line, errors));
        assertEquals(2, errors.size());
    }

    @Test
    void beanFactoryDefault_createBean_record_fails() {
        Line line = new Line(Contact.class.getName());
        assertThrows(InstantiationException.class, () -> new BeanFactoryDefault<>().createBean(line));
    }

    public record ContactWithPhone(String name, Phone phone) {
    }

    @Test
    void beanFactoryDefault_record_objectMethodDenied() {
        Line line = new Line(ContactWithPhone.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new StringCell("phone.class.name", "x"));
        List<JSaParException> errors = new ArrayList<>();
        List<Object> beans = composeDefault(line, errors);
        assertEquals(1, beans.size());
        assertEquals("Ann", ((ContactWithPhone) beans.getFirst()).name());
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().getMessage().contains("is not a bean property"), errors.getFirst().getMessage());
    }

    @Test
    void beanFactoryDefault_record_unmappableCell_reportedForCell() {
        Line line = new Line(ContactWithPhone.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new StringCell("phone.class.classLoader.defaultAssertionStatus", "true"));
        List<JSaParException> errors = new ArrayList<>();
        List<Object> beans = assertDoesNotThrow(() -> composeDefault(line, errors));
        assertEquals(List.of(new ContactWithPhone("Ann", null)), beans);
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().getMessage().contains("There is no property"), errors.getFirst().getMessage());
    }

    @Test
    void beanFactoryDefault_record_conflictingCellNames_reportedForCell() {
        Line line = new Line(ContactWithPhone.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new StringCell("phone", "123"))
                .addCell(new StringCell("phone.number", "456"));
        List<JSaParException> errors = new ArrayList<>();
        List<Object> beans = assertDoesNotThrow(() -> composeDefault(line, errors));
        assertEquals(List.of(new ContactWithPhone("Ann", null)), beans);
        assertEquals(2, errors.size(), errors.toString());
        assertTrue(errors.get(1).getMessage().contains("There is no property phone.number"), errors.get(1).getMessage());
    }

    public record Tag(char code, Character mark) {
    }

    @Test
    void numberToCharComponent() {
        Line line = new Line(Tag.class.getName())
                .addCell(new IntegerCell("code", 65))
                .addCell(new IntegerCell("mark", 66));
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Tag('A', 'B')), composeDefault(line, errors));
        assertEquals(List.of(), errors);
    }

    @Test
    void stringToCharComponent() {
        Line line = new Line(Tag.class.getName())
                .addCell(new StringCell("code", "A"))
                .addCell(new StringCell("mark", "B"));
        List<JSaParException> errors = new ArrayList<>();
        assertEquals(List.of(new Tag('A', 'B')), composeDefault(line, errors));
        assertEquals(List.of(), errors);
    }

    /** Has a derived getter that looks like a property but is not a record component. */
    public record OrderWithDerived(List<OrderLine> lines) {
        public List<OrderLine> getItems() {
            return lines;
        }
    }

    @Test
    void detailOfRecord_notAComponent_fails() {
        BeanException e = assertThrows(BeanException.class, () -> BeanMap.builder()
                .withLine("Order", OrderWithDerived.class, b -> b.withDetail("OrderLine", "items"))
                .build());
        assertTrue(e.getMessage().contains("not a record component"), e.getMessage());
    }

    @Test
    void beanFactoryDefault_recordInBean_error() {
        Line line = new Line(MutablePerson.class.getName())
                .addCell(new StringCell("name", "Ann"))
                .addCell(new StringCell("address.street", "Main street 1"));
        List<JSaParException> errors = new ArrayList<>();
        List<Object> beans = composeDefault(line, errors);
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().getMessage().contains("record"), errors.getFirst().getMessage());
        assertEquals("Ann", ((MutablePerson) beans.getFirst()).getName());
    }
}
