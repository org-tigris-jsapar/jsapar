package org.jsapar.compose.bean;

import org.jsapar.model.Line;

import java.util.function.BiConsumer;

@Deprecated
public class BeanEventListenerConsumer<T> implements BiConsumer<T, Line> {
    private final BeanEventListener<T> beanEventListener;

    public BeanEventListenerConsumer(BeanEventListener<T> beanEventListener) {
        this.beanEventListener = beanEventListener;
    }

    @Override
    public void accept(T bean, Line line) {
        beanEventListener.beanComposedEvent(new BeanEvent<>(bean, line));
    }
}
