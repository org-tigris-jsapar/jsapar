package org.jsapar.parse.bean;

import org.jsapar.compose.bean.BeanComposeException;
import org.jsapar.error.BeanException;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Internal class that builds an object from cells and detail beans. A mutable bean is assigned property by property
 * as the cells arrive, while the values of a record are collected and the record is created when {@link #build()} is
 * called, since a record cannot be changed after it is created. Nested objects get nodes of their own, so records and
 * beans can be nested within each other.
 * @since 2.5
 */
public abstract sealed class ObjectNode permits ObjectNode.Mutable, ObjectNode.RecordValues {

    private static final ClassValue<RecordType> RECORD_TYPES = new ClassValue<>() {
        @Override
        protected RecordType computeValue(Class<?> type) {
            return RecordType.of(type);
        }
    };

    private ObjectNode() {
    }

    /**
     * @param c The class of the object to build.
     * @return A node that collects the values of a record, or a node that assigns the properties of a new instance of
     * any other class.
     * @throws InstantiationException    If the class is abstract.
     * @throws IllegalAccessException    If the default constructor is not accessible.
     * @throws NoSuchMethodException     If a class that is not a record has no public default constructor.
     * @throws InvocationTargetException If the default constructor fails.
     */
    public static ObjectNode ofClass(Class<?> c) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (c.isRecord())
            return new RecordValues(RECORD_TYPES.get(c));
        return new Mutable(c.getConstructor().newInstance());
    }

    /**
     * @param bean An existing mutable bean.
     * @return A node that assigns the properties of the bean.
     */
    public static ObjectNode ofBean(Object bean) {
        return new Mutable(bean);
    }

    /**
     * Assigns the value of a cell to the property it is mapped to, possibly a property of a nested object. Empty
     * cells are ignored.
     * @param bean2Cell The mapping of the cell, of this object.
     * @param cell      The cell.
     * @throws BeanComposeException      If the value cannot be assigned.
     * @throws InvocationTargetException If a getter, setter or constructor fails.
     * @throws IllegalAccessException    If a getter, setter or constructor is not accessible.
     * @throws InstantiationException    If a nested object cannot be created.
     * @throws NoSuchMethodException     If a nested object has no public default constructor.
     */
    public void assign(Bean2Cell bean2Cell, Cell<?> cell) throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if (cell.isEmpty())
            return;
        ObjectNode node = this;
        while (bean2Cell.getChildren() != null) {
            Bean2Cell child = bean2Cell.getChildren().getBean2CellByName(cell.getName());
            if (child == null)
                return;
            node = node.child(bean2Cell);
            bean2Cell = child;
        }
        node.set(bean2Cell, cell);
    }

    /**
     * Adds the bean of a detail line to the collection or map property of this object.
     * @param bean2Detail The mapping of the detail line.
     * @param detailLine  The detail line.
     * @param detailBean  The bean of the detail line.
     * @throws BeanComposeException      If the detail bean cannot be added.
     * @throws InvocationTargetException If a getter or setter fails.
     * @throws IllegalAccessException    If a getter or setter is not accessible.
     */
    public abstract void addDetail(Bean2Detail bean2Detail, Line detailLine, Object detailBean) throws BeanComposeException, InvocationTargetException, IllegalAccessException;

    /**
     * Makes sure that the collection and map properties of a record that detail lines are mapped to are not null,
     * also if there were no detail lines. Does nothing for a mutable bean.
     * @param bean2Details The detail line mappings of this object.
     * @throws BeanComposeException If no collection or map can be created for a property.
     */
    public abstract void initDetails(Collection<Bean2Detail> bean2Details) throws BeanComposeException;

    /**
     * @return The object, with all assigned values. A record is created by this method.
     * @throws BeanComposeException      If a nested record cannot be assigned to its property.
     * @throws InvocationTargetException If a constructor or setter fails, for instance if the constructor of a record
     *                                   validates its values.
     * @throws IllegalAccessException    If a constructor or setter is not accessible.
     * @throws InstantiationException    If an object cannot be created.
     */
    public abstract Object build() throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException;

    abstract void set(Bean2Cell leaf, Cell<?> cell) throws BeanComposeException, InvocationTargetException, IllegalAccessException;

    abstract ObjectNode child(Bean2Cell base) throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException;

    private static String noMethodMessage(PropertyDescriptor propertyDescriptor, Class<?> c, String method) {
        return "The property " + propertyDescriptor.getName() + " of class " + c.getName() + " has no " + method + " method.";
    }

    /**
     * Node of a mutable bean. Values are assigned directly by setters. Nested records are created and assigned by
     * {@link #build()}.
     */
    static final class Mutable extends ObjectNode {
        private final Object bean;
        /** Nested objects by property name. Created when needed. */
        private Map<String, Child> children;

        private record Child(PropertyDescriptor propertyDescriptor, ObjectNode node) {
        }

        private Mutable(Object bean) {
            this.bean = bean;
        }

        @Override
        void set(Bean2Cell leaf, Cell<?> cell) throws BeanComposeException, InvocationTargetException, IllegalAccessException {
            PropertyDescriptor propertyDescriptor = leaf.getPropertyDescriptor();
            Method setter = propertyDescriptor.getWriteMethod();
            if (setter == null)
                throw new BeanComposeException(noMethodMessage(propertyDescriptor, bean.getClass(), "setter"));
            setter.invoke(bean, leaf.toPropertyValue(setter.getParameterTypes()[0], cell));
        }

        @Override
        ObjectNode child(Bean2Cell base) throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
            PropertyDescriptor propertyDescriptor = base.getPropertyDescriptor();
            if (children == null)
                children = new LinkedHashMap<>();
            Child child = children.get(propertyDescriptor.getName());
            if (child != null)
                return child.node();

            Method getter = propertyDescriptor.getReadMethod();
            if (getter == null)
                throw new BeanComposeException(noMethodMessage(propertyDescriptor, bean.getClass(), "getter"));
            if (getter.getDeclaringClass() == Object.class)
                throw new BeanComposeException("The property " + propertyDescriptor.getName() + " of class "
                        + bean.getClass().getName() + " is not a bean property.");
            Class<?> childClass = base.getChildren().getLineClass();
            Object existing = getter.invoke(bean);
            ObjectNode node;
            if (childClass.isRecord()) {
                // Assigned by build() when all values are known.
                if (propertyDescriptor.getWriteMethod() == null)
                    throw new BeanComposeException(noMethodMessage(propertyDescriptor, bean.getClass(), "setter"));
                node = existing == null ? new RecordValues(RECORD_TYPES.get(childClass)) : RecordValues.copyOf((Record) existing);
            } else if (existing != null) {
                node = new Mutable(existing);
            } else {
                Method setter = propertyDescriptor.getWriteMethod();
                if (setter == null)
                    throw new BeanComposeException(noMethodMessage(propertyDescriptor, bean.getClass(), "setter"));
                Object childBean = childClass.getConstructor().newInstance();
                setter.invoke(bean, childBean);
                node = new Mutable(childBean);
            }
            children.put(propertyDescriptor.getName(), new Child(propertyDescriptor, node));
            return node;
        }

        @Override
        public void addDetail(Bean2Detail bean2Detail, Line detailLine, Object detailBean) throws BeanComposeException, InvocationTargetException, IllegalAccessException {
            bean2Detail.add(bean, detailLine, detailBean);
        }

        @Override
        public void initDetails(Collection<Bean2Detail> bean2Details) {
            // Kept as it is, as before records were supported.
        }

        @Override
        public Object build() throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException {
            if (children == null)
                return bean;
            for (Child child : children.values()) {
                Object childObject = child.node().build();
                if (child.node() instanceof RecordValues)
                    child.propertyDescriptor().getWriteMethod().invoke(bean, childObject);
            }
            return bean;
        }
    }

    /**
     * Node of a record. Collects the values of the components and creates the record with its canonical constructor
     * when {@link #build()} is called. Components without value get null, or zero or false if primitive.
     */
    static final class RecordValues extends ObjectNode {
        private final RecordType type;
        private final Object[] values;
        /** Nested objects by component index. Created when needed. */
        private ObjectNode[] children;

        private RecordValues(RecordType type) {
            this.type = type;
            this.values = new Object[type.accessors().length];
        }

        /**
         * @param existing An existing record.
         * @return A node that starts with the values of an existing record.
         */
        private static RecordValues copyOf(Record existing) throws InvocationTargetException, IllegalAccessException {
            RecordValues recordValues = new RecordValues(RECORD_TYPES.get(existing.getClass()));
            Method[] accessors = recordValues.type.accessors();
            for (int i = 0; i < accessors.length; i++)
                recordValues.values[i] = accessors[i].invoke(existing);
            return recordValues;
        }

        private int indexOf(String propertyName) throws BeanComposeException {
            Integer index = type.indexByName().get(propertyName);
            if (index == null)
                throw new BeanComposeException("The property " + propertyName + " is not a component of record "
                        + type.recordClass().getName() + ".");
            return index;
        }

        @Override
        void set(Bean2Cell leaf, Cell<?> cell) throws BeanComposeException {
            int index = indexOf(leaf.getPropertyDescriptor().getName());
            values[index] = leaf.toPropertyValue(type.types()[index], cell);
        }

        @Override
        ObjectNode child(Bean2Cell base) throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
            int index = indexOf(base.getPropertyDescriptor().getName());
            if (children == null)
                children = new ObjectNode[values.length];
            if (children[index] == null) {
                Object existing = values[index];
                if (existing instanceof Record existingRecord)
                    children[index] = copyOf(existingRecord);
                else if (existing != null)
                    children[index] = new Mutable(existing);
                else
                    children[index] = ofClass(type.types()[index]);
            }
            return children[index];
        }

        @Override
        public void addDetail(Bean2Detail bean2Detail, Line detailLine, Object detailBean) throws BeanComposeException {
            int index = indexOf(bean2Detail.getPropertyName());
            if (values[index] == null)
                values[index] = bean2Detail.newContainer(type.recordClass());
            bean2Detail.addTo(values[index], detailLine, detailBean, type.recordClass());
        }

        @Override
        public void initDetails(Collection<Bean2Detail> bean2Details) throws BeanComposeException {
            for (Bean2Detail bean2Detail : bean2Details) {
                int index = indexOf(bean2Detail.getPropertyName());
                if (values[index] == null)
                    values[index] = bean2Detail.newContainer(type.recordClass());
            }
        }

        @Override
        public Object build() throws BeanComposeException, InvocationTargetException, IllegalAccessException, InstantiationException {
            Object[] arguments = values.clone();
            Class<?>[] types = type.types();
            for (int i = 0; i < arguments.length; i++) {
                if (children != null && children[i] != null)
                    arguments[i] = children[i].build();
                if (arguments[i] == null && types[i].isPrimitive())
                    arguments[i] = Array.get(Array.newInstance(types[i], 1), 0);
            }
            return type.constructor().newInstance(arguments);
        }
    }

    /**
     * Reflection information about a record class, cached per class.
     */
    private record RecordType(Class<?> recordClass,
                              Constructor<?> constructor,
                              Method[] accessors,
                              Class<?>[] types,
                              Map<String, Integer> indexByName) {

        /**
         * The canonical constructor and the accessors are made accessible if possible, so that records that are not
         * public can be used as long as their package is open to this module.
         */
        static RecordType of(Class<?> recordClass) {
            RecordComponent[] components = recordClass.getRecordComponents();
            Method[] accessors = new Method[components.length];
            Class<?>[] types = new Class<?>[components.length];
            Map<String, Integer> indexByName = new HashMap<>();
            for (int i = 0; i < components.length; i++) {
                accessors[i] = components[i].getAccessor();
                accessors[i].trySetAccessible();
                types[i] = components[i].getType();
                indexByName.put(components[i].getName(), i);
            }
            try {
                Constructor<?> constructor = recordClass.getDeclaredConstructor(types);
                constructor.trySetAccessible();
                return new RecordType(recordClass, constructor, accessors, types, Map.copyOf(indexByName));
            } catch (NoSuchMethodException e) {
                throw new BeanException("Record " + recordClass.getName() + " has no canonical constructor", e);
            }
        }
    }
}
