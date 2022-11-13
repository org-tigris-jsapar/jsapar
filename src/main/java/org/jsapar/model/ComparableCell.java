package org.jsapar.model;

/**
 */
interface ComparableCell<T extends Comparable<? super T>> extends Cell<T> {


    @Override
    default int compareValueTo(Cell<T> right) {
        if(right.isEmpty()){
            if(this.isEmpty())
                return 0;
            return -1;
        }
        return this.getValue().compareTo(right.getValue());
    }

}
