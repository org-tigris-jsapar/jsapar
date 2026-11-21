package org.jsapar.compose.bean;

import org.jsapar.compose.ComposeException;
import org.jsapar.compose.Composer;
import org.jsapar.compose.line.ValidationHandler;
import org.jsapar.error.BeanException;
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
 * was successfully composed, registered bean consumer will be called.
 * You can register a consumer by calling {@link #setBeanConsumer(Consumer)} or {@link #setBeanConsumer(BiConsumer)}
 *
 * @param <T> common base class of all the expected beans. Use Object as base class if there is no common base class for all beans.
 */
@SuppressWarnings("UnusedReturnValue")
public final class BeanComposer<T> implements Composer{
    private BiConsumer<T, Line> beanLineConsumer;
    private Consumer<JSaParException> errorConsumer = new ExceptionErrorConsumer();
    private BeanFactory<T>            beanFactory;
    private BeanComposeConfig config;
    /** If not null, the class that all composed beans need to be of. */
    private Class<? extends T> beanClass;
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


    /**
     * The behavior in case of mapping error depends on the configuration supplied when creating this instance.
     * @param line The line to convert into a bean instance.
     * @return Optionally a converted bean or Optional.empty() if no bean could be constructed based on the line, or
     * if the bean is not of the class set by {@link #setBeanClass(Class)}.
     * @throws ComposeException in case of error while composing the bean.
     */
    public Optional<T> toBean(Line line){
        return createBean(line).filter(bean -> isOfBeanClass(bean, line));
    }

    /**
     * Creates the bean of a line without checking its class against the bean class, since this is also used for
     * detail lines, whose beans are usually of other classes than the beans of the master lines.
     * @param line The line.
     * @return Optionally a bean.
     */
    private Optional<T> createBean(Line line){
        try {
            BeanBuilder<T> builder = beanFactory.createBeanBuilder(line);
            if (builder == null) {
                validationHandler.lineValidationError(line, NO_CLASS_MESSAGE, config.getOnUndefinedLineType(), errorConsumer);
                return Optional.empty();
            }
            return Optional.of(build(line, builder));
        } catch (ReflectiveOperationException | BeanComposeException | ClassCastException | IllegalArgumentException e) {
            handleBeanError(line, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean composeLine(Line line) {
        T bean;
        try {
            BeanBuilder<T> builder = beanFactory.createBeanBuilder(line);
            if (builder == null) {
                // There is no bean to pass to the bean consumer, whatever the validation action.
                validationHandler.lineValidationError(line, NO_CLASS_MESSAGE, config.getOnUndefinedLineType(), errorConsumer);
                return false;
            }
            bean = build(line, builder);
        } catch (ReflectiveOperationException | BeanComposeException | ClassCastException | IllegalArgumentException e) {
            handleBeanError(line, e);
            return false;
        }
        if (!isOfBeanClass(bean, line))
            return false;
        beanLineConsumer.accept(bean, line);
        return true;
    }

    /**
     * Checks the class of a bean before it is passed on, since the generic type of this class cannot be checked at
     * runtime. Reports an error if the bean is not of the bean class.
     * @param bean The bean of a line.
     * @param line The line.
     * @return True if no bean class is set or if the bean is of the bean class.
     */
    private boolean isOfBeanClass(T bean, Line line) {
        if (beanClass == null || beanClass.isInstance(bean))
            return true;
        String beanClassName = bean == null ? "null" : bean.getClass().getName();
        generateErrorEvent(line, "The bean is not of the bean class " + beanClass.getName() + ". Skipped the line",
                new ClassCastException("The bean of class " + beanClassName + " is not of class " + beanClass.getName()));
        return false;
    }

    private static final String NO_CLASS_MESSAGE = "BeanFactory failed to instantiate object for this line because there was no associated class. You can supress errors like this by setting config.onUndefinedLineType=OMIT_LINE";

    private void handleBeanError(Line line, Exception e) {
        switch (e) {
            case InstantiationException ignored -> generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
            case NoSuchMethodException ignored -> generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
            case InvocationTargetException ignored -> generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
            case IllegalAccessException ignored -> generateErrorEvent(line, "Failed to access a constructor or method. Skipped creating bean", e);
            case ClassNotFoundException ignored -> generateErrorEvent(line, "Class not found. Skipped creating bean", e);
            case ClassCastException ignored -> generateErrorEvent(line,
                    "Class of the created bean is not inherited from the generic type specified when creating the BeanComposer", e);
            default -> generateErrorEvent(line, "Failed to create bean. Skipped creating bean", e);
        }
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
    public void setErrorConsumer(Consumer<JSaParException> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    public BeanFactory<T> getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory<T> beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Sets the class that all composed beans need to be of. Since the generic type of this class cannot be checked at
     * runtime, a bean of another class would otherwise be passed to the bean consumer, where it causes a
     * {@link ClassCastException}. When a bean class is set, a bean of another class is instead reported to the error
     * consumer and the line is skipped. Beans of detail lines are not checked, since they are usually of other classes.
     * @param beanClass The class that all composed beans need to be of, or null to not check the class of the beans,
     *                  which is the default.
     * @since 2.5
     */
    public void setBeanClass(Class<? extends T> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * @return The class that all composed beans need to be of, or null if the class of the beans is not checked.
     * @see #setBeanClass(Class)
     * @since 2.5
     */
    public Class<? extends T> getBeanClass() {
        return beanClass;
    }


    /**
     * Assigns the cells and detail beans of a line to a bean builder and builds the bean. Errors while assigning a
     * cell or a detail bean are passed to the error consumer, and the bean is built anyway.
     *
     * @param line    The line to get cells and details from.
     * @param builder The builder of the bean.
     * @return The built bean.
     */
    private T build(Line line, BeanBuilder<T> builder) throws ReflectiveOperationException, BeanComposeException {
        for (Cell<?> cell : line) {
            String sName = cell.getName();
            if (sName == null || sName.isEmpty() || cell.isEmpty())
                continue;

            try {
                builder.assignCell(cell);
            } catch (BeanComposeException
                    | IllegalArgumentException
                    | ReflectiveOperationException e) {
                errorConsumer.accept( new ComposeException(e.getMessage() + " while handling cell " + cell, e));
            }
        }
        if (line.hasDetails())
            assignDetails(line, builder);
        return builder.build();
    }

    /**
     * Creates beans of the detail lines, recursively, and adds them to the builder of the master bean. Detail lines
     * that are not mapped to a property of the master bean are ignored.
     * @param line    The master line.
     * @param builder The builder of the bean of the master line.
     */
    private void assignDetails(Line line, BeanBuilder<T> builder) {
        for (Line detail : line.getDetails()) {
            if (!beanFactory.hasDetail(line.getLineType(), detail.getLineType()))
                continue;
            Optional<T> detailBean = createBean(detail);
            if (detailBean.isEmpty())
                continue;
            try {
                builder.assignDetail(detail, detailBean.get());
            } catch (BeanComposeException
                    | BeanException
                    | IllegalArgumentException
                    | ReflectiveOperationException e) {
                errorConsumer.accept(new ComposeException(e.getMessage() + " while handling detail line", detail, e));
            }
        }
    }


    public BeanComposeConfig getConfig() {
        return config;
    }

    public void setConfig(BeanComposeConfig config) {
        this.config = config;
    }
}
