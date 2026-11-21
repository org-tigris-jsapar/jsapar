package org.jsapar;

import org.jsapar.bean.BeanMap;
import org.jsapar.bean.JSaParCell;
import org.jsapar.bean.JSaParDetail;
import org.jsapar.bean.JSaParLine;
import org.jsapar.compose.ComposeException;
import org.jsapar.error.BeanException;
import org.jsapar.error.JSaParException;
import org.jsapar.schema.Schema;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mapping of master-detail lines to beans, using the example files examples/09_*.
 */
class MasterDetailBeanTest {

    @JSaParLine(lineType = "Order")
    public static class Order {
        @JSaParCell(name = "orderId")
        private long orderId;
        @JSaParCell(name = "date")
        private LocalDate date;
        @JSaParCell(name = "customer")
        private String customer;
        @JSaParDetail(lineType = "OrderLine", keyCell = "sku")
        private Map<String, OrderLine> lines;
        @JSaParDetail(lineType = "HistoryLog")
        private List<HistoryLog> history;

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }
        public Map<String, OrderLine> getLines() { return lines; }
        public void setLines(Map<String, OrderLine> lines) { this.lines = lines; }
        public List<HistoryLog> getHistory() { return history; }
        public void setHistory(List<HistoryLog> history) { this.history = history; }
    }

    @JSaParLine(lineType = "OrderLine")
    public static class OrderLine {
        @JSaParCell(name = "sku")
        private String sku;
        @JSaParCell(name = "quantity")
        private int quantity;
        /** Initialized here and no setter. */
        @JSaParDetail(lineType = "Discount")
        private final List<Discount> discounts = new ArrayList<>();

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public List<Discount> getDiscounts() { return discounts; }
    }

    @JSaParLine(lineType = "Discount")
    public static class Discount {
        @JSaParCell(name = "percent")
        private int percent;

        public int getPercent() { return percent; }
        public void setPercent(int percent) { this.percent = percent; }
    }

    @JSaParLine(lineType = "HistoryLog")
    public static class HistoryLog {
        @JSaParCell(name = "text")
        private String text;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    /** Only maps history logs, to a Set. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithHistorySet {
        @JSaParCell(name = "orderId")
        private long orderId;
        @JSaParDetail(lineType = "HistoryLog")
        private Set<HistoryLog> history;

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }
        public Set<HistoryLog> getHistory() { return history; }
        public void setHistory(Set<HistoryLog> history) { this.history = history; }
    }

    /** Detail beans keyed by an integer cell. */
    @JSaParLine(lineType = "Order")
    public static class OrderByQuantity {
        @JSaParDetail(lineType = "OrderLine", keyCell = "quantity")
        private TreeMap<Long, OrderLine> lines;

        public TreeMap<Long, OrderLine> getLines() { return lines; }
        public void setLines(TreeMap<Long, OrderLine> lines) { this.lines = lines; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderWithKeyOnList {
        @JSaParDetail(lineType = "HistoryLog", keyCell = "text")
        private List<HistoryLog> history;

        public List<HistoryLog> getHistory() { return history; }
        public void setHistory(List<HistoryLog> history) { this.history = history; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderWithMapWithoutKey {
        @JSaParDetail(lineType = "HistoryLog")
        private Map<String, HistoryLog> history;

        public Map<String, HistoryLog> getHistory() { return history; }
        public void setHistory(Map<String, HistoryLog> history) { this.history = history; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderWithNonCollection {
        @JSaParDetail(lineType = "HistoryLog")
        private HistoryLog history;

        public HistoryLog getHistory() { return history; }
        public void setHistory(HistoryLog history) { this.history = history; }
    }

    /** The history is an immutable list that cannot be added to. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithImmutableHistory {
        @JSaParCell(name = "orderId")
        private long orderId;
        @JSaParDetail(lineType = "HistoryLog")
        private List<HistoryLog> history = List.of();

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }
        public List<HistoryLog> getHistory() { return history; }
        public void setHistory(List<HistoryLog> history) { this.history = history; }
    }

    /** Detail attribute with a wildcard type. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithWildcard {
        @JSaParDetail(lineType = "HistoryLog")
        private List<? extends HistoryLog> history;

        public List<? extends HistoryLog> getHistory() { return history; }
        public void setHistory(List<? extends HistoryLog> history) { this.history = history; }
    }

    /** Detail attribute whose element type is not annotated. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithUnmappedDetail {
        @JSaParDetail(lineType = "HistoryLog")
        private List<Object> history;

        public List<Object> getHistory() { return history; }
        public void setHistory(List<Object> history) { this.history = history; }
    }

    /** Detail attribute of an interface type that is initialized by the bean. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithSortedHistory {
        @JSaParDetail(lineType = "HistoryLog")
        private SortedSet<HistoryLog> history = new TreeSet<>(Comparator.comparing(HistoryLog::getText).reversed());

        public SortedSet<HistoryLog> getHistory() { return history; }
        public void setHistory(SortedSet<HistoryLog> history) { this.history = history; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderWithQueue {
        @JSaParDetail(lineType = "HistoryLog")
        private Queue<HistoryLog> history;

        public Queue<HistoryLog> getHistory() { return history; }
        public void setHistory(Queue<HistoryLog> history) { this.history = history; }
    }

    /** Detail attribute of a type that cannot be created and that is not initialized. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithBlockingQueue {
        @JSaParDetail(lineType = "HistoryLog")
        private java.util.concurrent.BlockingQueue<HistoryLog> history;

        public java.util.concurrent.BlockingQueue<HistoryLog> getHistory() { return history; }
        public void setHistory(java.util.concurrent.BlockingQueue<HistoryLog> history) { this.history = history; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderByIntegerQuantity {
        @JSaParDetail(lineType = "OrderLine", keyCell = "quantity")
        private Map<Integer, OrderLine> lines;

        public Map<Integer, OrderLine> getLines() { return lines; }
        public void setLines(Map<Integer, OrderLine> lines) { this.lines = lines; }
    }

    /** A map class that gives the key type in its superclass. */
    public static class OrderLinesByQuantity<V> extends LinkedHashMap<Integer, V> {
    }

    @JSaParLine(lineType = "Order")
    public static class OrderByIntegerQuantityMapSubclass {
        @JSaParDetail(lineType = "OrderLine", keyCell = "quantity")
        private OrderLinesByQuantity<OrderLine> lines;

        public OrderLinesByQuantity<OrderLine> getLines() { return lines; }
        public void setLines(OrderLinesByQuantity<OrderLine> lines) { this.lines = lines; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderByQuantityText {
        @JSaParDetail(lineType = "OrderLine", keyCell = "quantity")
        private SortedMap<String, OrderLine> lines;

        public SortedMap<String, OrderLine> getLines() { return lines; }
        public void setLines(SortedMap<String, OrderLine> lines) { this.lines = lines; }
    }

    @JSaParLine(lineType = "Order")
    public static class OrderBySkuAsDate {
        @JSaParDetail(lineType = "OrderLine", keyCell = "sku")
        private Map<LocalDate, OrderLine> lines;

        public Map<LocalDate, OrderLine> getLines() { return lines; }
        public void setLines(Map<LocalDate, OrderLine> lines) { this.lines = lines; }
    }

    /** The detail line type differs from the line type of the element class. */
    @JSaParLine(lineType = "Order")
    public static class OrderWithWrongDetailLineType {
        @JSaParDetail(lineType = "History")
        private List<HistoryLog> history;

        public List<HistoryLog> getHistory() { return history; }
        public void setHistory(List<HistoryLog> history) { this.history = history; }
    }

    private static Schema<?> csvSchema() throws IOException {
        try (Reader reader = new FileReader("examples/09_CsvSchemaMasterDetail.xml", StandardCharsets.UTF_8)) {
            return Schema.ofXml(reader);
        }
    }

    private static <T> List<T> convert(Text2BeanConverter<T> converter, Reader reader, List<JSaParException> errors) throws IOException {
        converter.setErrorConsumer(errors::add);
        List<T> beans = new ArrayList<>();
        converter.convertForEach(reader, (T bean) -> beans.add(bean));
        return beans;
    }

    private static Reader ordersCsv() throws IOException {
        return new FileReader("examples/09_Orders.csv", StandardCharsets.UTF_8);
    }

    private static void assertOrders(List<Order> orders) {
        assertEquals(2, orders.size());
        Order order1 = orders.get(0);
        assertEquals(1001, order1.getOrderId());
        assertEquals(LocalDate.of(2026, 11, 2), order1.getDate());
        assertEquals("ACME", order1.getCustomer());
        assertInstanceOf(LinkedHashMap.class, order1.getLines());
        assertEquals(List.of("SKU-1", "SKU-7"), new ArrayList<>(order1.getLines().keySet()));
        OrderLine item1 = order1.getLines().get("SKU-1");
        assertEquals(2, item1.getQuantity());
        assertEquals(1, item1.getDiscounts().size());
        assertEquals(10, item1.getDiscounts().getFirst().getPercent());
        assertTrue(order1.getLines().get("SKU-7").getDiscounts().isEmpty());
        assertEquals(1, order1.getHistory().size());
        assertEquals("Created by web shop", order1.getHistory().getFirst().getText());

        Order order2 = orders.get(1);
        assertEquals(1002, order2.getOrderId());
        assertEquals(Set.of("SKU-3"), order2.getLines().keySet());
        assertEquals(5, order2.getLines().get("SKU-3").getQuantity());
        assertNull(order2.getHistory(), "No history lines, so no list is created");
    }

    @Test
    void annotatedClass_convertForEach() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = convert(new Text2BeanConverter<>(csvSchema(), Order.class), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertOrders(orders);
    }

    @Test
    void annotatedClass_beanClass_detailBeansNotChecked() throws IOException {
        Text2BeanConverter<Order> converter = new Text2BeanConverter<>(csvSchema(), Order.class);
        converter.setBeanClass(Order.class);
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = convert(converter, ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertOrders(orders);
    }

    @Test
    void annotatedClass_stream() throws IOException {
        Text2BeanConverter<Order> converter = new Text2BeanConverter<>(csvSchema(), Order.class);
        try (Reader reader = ordersCsv(); Stream<Order> stream = converter.stream(reader)) {
            assertOrders(stream.toList());
        }
    }

    @Test
    void ofClasses_addsDetailClasses() {
        BeanMap beanMap = BeanMap.ofClass(Order.class);
        assertEquals(OrderLine.class, beanMap.getBeanPropertyMap("OrderLine").getLineClass());
        assertEquals(Discount.class, beanMap.getBeanPropertyMap("Discount").getLineClass());
        assertEquals(HistoryLog.class, beanMap.getBeanPropertyMap("HistoryLog").getLineClass());
    }

    @Test
    void builder() throws IOException {
        BeanMap beanMap = BeanMap.builder()
                .withLine("Order", Order.class, b -> b
                        .withCell("orderId").withCell("date").withCell("customer")
                        .withDetail("OrderLine", "lines", "sku")
                        .withDetail("HistoryLog", "history"))
                .withLine("OrderLine", OrderLine.class, b -> b
                        .withCell("sku").withCell("quantity")
                        .withDetail("Discount", "discounts"))
                .withLine("Discount", Discount.class, b -> b.withCell("percent"))
                .withLine("HistoryLog", HistoryLog.class, b -> b.withCell("text"))
                .build();
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = convert(new Text2BeanConverter<>(csvSchema(), beanMap), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertOrders(orders);
    }

    @Test
    void xmlBeanMap() throws IOException {
        String prefix = MasterDetailBeanTest.class.getName() + "$";
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<beanmap xmlns='http://jsapar.tigris.org/BeanMapSchema/2.0'>"
                + "<bean name='" + prefix + "Order' linetype='Order'>"
                + "<property name='orderId' cellname='orderId'/>"
                + "<property name='date' cellname='date'/>"
                + "<property name='customer' cellname='customer'/>"
                + "<detail linetype='OrderLine' name='lines' keycell='sku'/>"
                + "<detail linetype='HistoryLog' name='history'/>"
                + "</bean>"
                + "<bean name='" + prefix + "OrderLine' linetype='OrderLine'>"
                + "<property name='sku' cellname='sku'/><property name='quantity' cellname='quantity'/>"
                + "<detail linetype='Discount' name='discounts'/>"
                + "</bean>"
                + "<bean name='" + prefix + "Discount' linetype='Discount'><property name='percent' cellname='percent'/></bean>"
                + "<bean name='" + prefix + "HistoryLog' linetype='HistoryLog'><property name='text' cellname='text'/></bean>"
                + "</beanmap>";
        BeanMap beanMap = BeanMap.ofXml(new StringReader(xml));
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = convert(new Text2BeanConverter<>(csvSchema(), beanMap), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertOrders(orders);
    }

    @Test
    void setProperty_unmappedDetailsIgnored() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderWithHistorySet> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderWithHistorySet.class), ordersCsv(), errors);
        assertEquals(List.of(), errors, "Order lines and discounts are not mapped and silently ignored");
        assertEquals(2, orders.size());
        assertInstanceOf(LinkedHashSet.class, orders.get(0).getHistory());
        assertEquals(1, orders.get(0).getHistory().size());
    }

    @Test
    void integerKey_concreteMapClass() throws IOException {
        BeanMap beanMap = BeanMap.ofClasses(OrderByQuantity.class, OrderLine.class);
        List<JSaParException> errors = new ArrayList<>();
        List<OrderByQuantity> orders = convert(new Text2BeanConverter<>(csvSchema(), beanMap), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertEquals(List.of(1L, 2L), new ArrayList<>(orders.get(0).getLines().keySet()));
        assertEquals("SKU-7", orders.get(0).getLines().get(1L).getSku());
    }

    @Test
    void missingAndDuplicateKey() throws IOException {
        String text = "O;1001;2026-11-02;ACME\nL;SKU-1;2\nL;;3\nL;SKU-1;4\n";
        List<JSaParException> errors = new ArrayList<>();
        List<Order> orders = convert(new Text2BeanConverter<>(csvSchema(), Order.class), new StringReader(text), errors);

        assertEquals(1, orders.size());
        Map<String, OrderLine> lines = orders.getFirst().getLines();
        assertEquals(Set.of("SKU-1"), lines.keySet());
        assertEquals(2, lines.get("SKU-1").getQuantity(), "The first line is kept on duplicate key");
        assertEquals(2, errors.size());
        assertInstanceOf(ComposeException.class, errors.get(0));
        assertTrue(errors.get(0).getMessage().contains("key cell sku"), errors.get(0).getMessage());
        assertTrue(errors.get(1).getMessage().contains("Duplicate key SKU-1"), errors.get(1).getMessage());
    }

    @Test
    void invalidMappings() {
        assertThrows(BeanException.class, () -> BeanMap.ofClass(OrderWithKeyOnList.class));
        assertThrows(BeanException.class, () -> BeanMap.ofClass(OrderWithMapWithoutKey.class));
        assertThrows(BeanException.class, () -> BeanMap.ofClass(OrderWithNonCollection.class));
        assertThrows(BeanException.class, () -> BeanMap.builder()
                .withLine("Order", Order.class, b -> b.withDetail("OrderLine", "doesNotExist"))
                .build());
    }

    @Test
    void immutableCollection_isReportedToErrorConsumer() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderWithImmutableHistory> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderWithImmutableHistory.class), ordersCsv(), errors);
        assertEquals(2, orders.size(), "Converting continues");
        assertEquals(1, errors.size());
        assertInstanceOf(ComposeException.class, errors.getFirst());
        assertTrue(errors.getFirst().getMessage().contains("history"), errors.getFirst().getMessage());
    }

    @Test
    void wildcardElementType_isAddedToBeanMap() throws IOException {
        assertEquals(HistoryLog.class, BeanMap.ofClass(OrderWithWildcard.class).getBeanPropertyMap("HistoryLog").getLineClass());
        List<JSaParException> errors = new ArrayList<>();
        List<OrderWithWildcard> orders = convert(new Text2BeanConverter<>(csvSchema(), OrderWithWildcard.class), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertEquals("Created by web shop", orders.getFirst().getHistory().getFirst().getText());
    }

    @Test
    void unmappedDetailLineType_failsWhenCreatingConverter() throws IOException {
        Schema<?> schema = csvSchema();
        BeanException e = assertThrows(BeanException.class,
                () -> new Text2BeanConverter<>(schema, OrderWithUnmappedDetail.class));
        assertTrue(e.getMessage().contains("HistoryLog") && e.getMessage().contains("history"), e.getMessage());
        // Fine when the class is given explicitly.
        new Text2BeanConverter<>(schema, BeanMap.ofSchema(schema, BeanMap.ofClasses(OrderWithUnmappedDetail.class, HistoryLog.class)));
    }

    @Test
    void interfaceTypes_initializedByBeanOrCreated() throws IOException {
        String text = "O;1001;2026-11-02;ACME\nH;a\nH;c\nH;b\n";
        List<JSaParException> errors = new ArrayList<>();
        List<OrderWithSortedHistory> sorted = convert(
                new Text2BeanConverter<>(csvSchema(), OrderWithSortedHistory.class), new StringReader(text), errors);
        assertEquals(List.of(), errors);
        assertEquals(List.of("c", "b", "a"), sorted.getFirst().getHistory().stream().map(HistoryLog::getText).toList());

        List<OrderWithQueue> queued = convert(
                new Text2BeanConverter<>(csvSchema(), OrderWithQueue.class), new StringReader(text), errors);
        assertEquals(List.of(), errors);
        assertInstanceOf(ArrayDeque.class, queued.getFirst().getHistory());
        assertEquals(List.of("a", "c", "b"), queued.getFirst().getHistory().stream().map(HistoryLog::getText).toList());

        List<OrderByQuantityText> byText = convert(
                new Text2BeanConverter<>(csvSchema(), OrderByQuantityText.class), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        assertInstanceOf(TreeMap.class, byText.getFirst().getLines());
        assertEquals(List.of("1", "2"), new ArrayList<>(byText.getFirst().getLines().keySet()));
    }

    @Test
    void uninitializedTypeThatCannotBeCreated_isReportedToErrorConsumer() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderWithBlockingQueue> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderWithBlockingQueue.class), ordersCsv(), errors);
        assertEquals(2, orders.size());
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().getMessage().contains("Initialize the property"), errors.getFirst().getMessage());
    }

    @Test
    void mapKey_convertedToDeclaredKeyType() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderByIntegerQuantity> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderByIntegerQuantity.class), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        Map<Integer, OrderLine> lines = orders.getFirst().getLines();
        assertEquals("SKU-7", lines.get(1).getSku());
        for (Integer key : lines.keySet())
            assertNotNull(key);
    }

    @Test
    void mapKey_keyTypeOfMapSubclass_convertedToDeclaredKeyType() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderByIntegerQuantityMapSubclass> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderByIntegerQuantityMapSubclass.class), ordersCsv(), errors);
        assertEquals(List.of(), errors);
        OrderLinesByQuantity<OrderLine> lines = orders.getFirst().getLines();
        assertEquals("SKU-7", lines.get(1).getSku());
        for (Object key : lines.keySet())
            assertInstanceOf(Integer.class, key);
    }

    @Test
    void mapKey_notConvertible_isReportedToErrorConsumer() throws IOException {
        List<JSaParException> errors = new ArrayList<>();
        List<OrderBySkuAsDate> orders = convert(
                new Text2BeanConverter<>(csvSchema(), OrderBySkuAsDate.class), ordersCsv(), errors);
        assertEquals(2, orders.size());
        assertEquals(3, errors.size(), "One per order line");
        assertTrue(errors.getFirst().getMessage().contains("java.time.LocalDate"), errors.getFirst().getMessage());
        assertTrue(orders.getFirst().getLines().isEmpty(), "The map is created but nothing is added");
    }

    @Test
    void ofClasses_detailLineTypeWithoutClass_fails() {
        BeanException e = assertThrows(BeanException.class, () -> BeanMap.ofClass(OrderWithWrongDetailLineType.class));
        assertTrue(e.getMessage().contains("History") && e.getMessage().contains("history"), e.getMessage());
    }
}
