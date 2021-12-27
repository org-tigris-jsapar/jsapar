package org.jsapar.compose.bean;

import org.jsapar.compose.ComposeException;
import org.jsapar.compose.Composer;
import org.jsapar.compose.line.ValidationHandler;
import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.Line;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Composer class that composes java beans based on a document or by single lines. The result is that for each bean that
 * was successfully composed, registered bean consumer will be called..
 * You can register a consumer by calling {@link #setBeanConsumer(Consumer)} or {@link #setBeanConsumer(BiConsumer)}
 *
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 */
@SuppressWarnings("UnusedReturnValue")
public class BeanComposer<T> implements Composer{
    private BiConsumer<T, Line> beanLineConsumer;
    private Consumer<JSaParException> errorConsumer = new ExceptionErrorConsumer();
    private BeanFactory<T>            beanFactory;
    private BeanComposeConfig config;
    private final ValidationHandler validationHandler = new ValidationHandler();

    /**
     * Creates a bean composer with {@link BeanFactoryDefault} as {@link BeanFactory}
     */
    public BeanComposer() {
        this(new BeanComposeConfig(), new BeanFactoryDefault<>());
    }

    /**
     * @param config Configuration to use
     */
    public BeanComposer(BeanComposeConfig config) {
        this(config, new BeanFactoryDefault<>());
    }

    /**
     * Creates a bean composer with a customized {@link BeanFactory}. You can implement your own {@link BeanFactory} in
     * order to control which bean class should be created for each line that is composed.
     *
     * @param beanFactory An implementation of the {@link BeanFactory} interface.
     */
    public BeanComposer(BeanFactory<T> beanFactory) {
        this(new BeanComposeConfig(), beanFactory);
    }

    /**
     * Creates a bean composer with a customized {@link BeanFactory}. You can implement your own {@link BeanFactory} in
     * order to control which bean class should be created for each line that is composed.
     * @param config Configuration to use
     * @param beanFactory An implementation of the {@link BeanFactory} interface.
     */
    public BeanComposer(BeanComposeConfig config, BeanFactory<T> beanFactory) {
        this.config = config;
        this.beanFactory = beanFactory;
    }


    public Optional<T> toBean(Line line){
        try {
            T bean = beanFactory.createBean(line);
            if (bean == null) {
                validationHandler.lineValidationError(line,
                        "BeanFactory failed to instantiate object for this line because there was no associated class. You can supress errors like this by setting config.onUndefinedLineType=OMIT_LINE",
                        config.getOnUndefinedLineType(), errorConsumer);
                return Optional.empty();
            } else {
                assign(line, bean);
                return Optional.of(bean);
            }
        } catch (InstantiationException|NoSuchMethodException|InvocationTargetException e) {
            generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
        } catch (IllegalAccessException e) {
            generateErrorEvent(line, "Failed to call set method. Skipped creating bean", e);
        } catch (ClassNotFoundException e) {
            generateErrorEvent(line, "Class not found. Skipped creating bean", e);
        } catch (ClassCastException e) {
            generateErrorEvent(line,
                    "Class of the created bean is not inherited from the generic type specified when creating the BeanComposer",
                    e);
        }
        return Optional.empty();
    }

    @Override
    public boolean composeLine(Line line) {
        T bean = null;
        try {
            bean = beanFactory.createBean(line);
            if (bean == null) {
                if (!validationHandler.lineValidationError(line,
                        "BeanFactory failed to instantiate object for this line because there was no associated class. You can supress errors like this by setting config.onUndefinedLineType=OMIT_LINE",
                        config.getOnUndefinedLineType(), errorConsumer)) {
                    return false;
                }
            } else {
                assign(line, bean);
            }
        } catch (InstantiationException|NoSuchMethodException|InvocationTargetException e) {
            generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
        } catch (IllegalAccessException e) {
            generateErrorEvent(line, "Failed to call set method. Skipped creating bean", e);
        } catch (ClassNotFoundException e) {
            generateErrorEvent(line, "Class not found. Skipped creating bean", e);
        } catch (ClassCastException e) {
            generateErrorEvent(line,
                    "Class of the created bean is not inherited from the generic type specified when creating the BeanComposer",
                    e);
        }
        beanLineConsumer.accept(bean, line);
        return true;
    }

    private void generateErrorEvent(Line line, String message, Throwable t) {
        errorConsumer.accept( new ComposeException(message, line, t));
    }


    /**
     * Deprecated since 2.2. Use one of {@link #setBeanConsumer(Consumer)} or {@link #setBeanConsumer(BiConsumer)} instead.
     * @param eventListener The bean event listener to get callback from
     */
    @Deprecated
    public void setComposedEventListener(BeanEventListener<T> eventListener) {
        this.beanLineConsumer = new BeanEventListenerConsumer<>(eventListener);
    }

    /**
     * Allows to handle both bean and line for each bean that is composed.
     * @param beanConsumer The {@link BiConsumer} that will be called for each bean that is composed. First argument is the bean, the second is the line.
     * @see #setBeanConsumer(Consumer)
     */
    public void setBeanConsumer(BiConsumer<T, Line> beanConsumer){
        this.beanLineConsumer = beanConsumer;
    }

    /**
     * @param beanConsumer The consumer that will be called for each bean that is composed.
     * @see #setBeanConsumer(BiConsumer)
     */
    public void setBeanConsumer(Consumer<T> beanConsumer){
        this.beanLineConsumer = (bean, line)-> beanConsumer.accept(bean);
    }

    @Override
    public void setErrorConsumer(Consumer<JSaParException> errorEventListener) {
        this.errorConsumer = errorEventListener;
    }

    public BeanFactory<T> getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory<T> beanFactory) {
        this.beanFactory = beanFactory;
    }


    /**
     * Assigns the cells of a line as attributes to an object.
     *
     * @param line           The line to get parameters from.
     * @param objectToAssign The object to assign cell attributes to. The object will be modified.
     * @return The object that was assigned. The same object that was supplied as parameter.
     */
    private T assign(Line line, T objectToAssign) {

        for (Cell cell : line) {
            String sName = cell.getName();
            if (sName == null || sName.isEmpty() || cell.isEmpty())
                continue;

            try {
                beanFactory.assignCellToBean(line.getLineType(), objectToAssign, cell);
            } catch (BeanComposeException
                    | IllegalArgumentException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException
                    | InstantiationException e) {
                errorConsumer.accept( new ComposeException(e.getMessage() + " while handling cell " + cell, e));
            }
        }
        return objectToAssign;
    }


    public BeanComposeConfig getConfig() {
        return config;
    }

    public void setConfig(BeanComposeConfig config) {
        this.config = config;
    }
}
