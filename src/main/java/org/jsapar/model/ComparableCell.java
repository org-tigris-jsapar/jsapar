package org.jsapar.model;

/**
 */
interface ComparableCell<T extends Comparable<? super T>> extends Cell<T> {


    @Override
    default int compareValueTo(Cell<T> right) {
        return this.getValue().compareTo(right.getValue());
    }

}
