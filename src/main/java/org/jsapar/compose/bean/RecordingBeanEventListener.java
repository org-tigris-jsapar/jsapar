package org.jsapar.compose.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves all beans that was composed into a list that can be retrieved by calling {@link #getBeans()} when done
 * composing. The Generic type T should be set to a common base class of all the expected beans. Use Object as
 * base class if there is no common base class for all beans. A {@link ClassCastException} will be thrown if the bean
 * created could not be converted into the class defined by T.
 * Created by stejon0 on 2016-10-15.
 */
public class RecordingBeanEventListener<T> implements BeanComposedEventListener<T> {
    private List<T> beans = new ArrayList<>();

    /**
     * @return All composed beans.
     */
    public List<T> getBeans() {
        return beans;
    }

    /**
     * Called every time that a bean, on root level, is successfully composed. Child beans do not generate events.
     * This implementation saves the composed bean into an internal list to be retrieved later by calling
     * {@link #getBeans()}
     * @param event The event that contains the composed bean.
     */
    @Override
    public void beanComposedEvent(BeanComposedEvent<T> event) {
        beans.add(event.getBean());
    }
}
