package org.jsapar.model;

/**
 */
public interface ComparableCell<T extends Comparable<? super T>> extends Cell<T>, Comparable<ComparableCell<T> > {


    @Override
    default int compareValueTo(Cell<T> right) {
        return this.getValue().compareTo(right.getValue());
    }

    @Override
    default int compareTo(ComparableCell<T> right) {
        return this.compareValueTo(right);
    }

}
