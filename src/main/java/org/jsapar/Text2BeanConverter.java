package org.jsapar;

import org.jsapar.compose.bean.*;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.convert.ConvertTask;
import org.jsapar.parse.bean.BeanMap;
import org.jsapar.parse.text.TextParseConfig;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.Schema;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Reader;

/**
 * Converts text input to Java bean objects.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * @see AbstractConverter
 */
public class Text2BeanConverter<T> extends AbstractConverter {

    private final Schema         parseSchema;
    private BeanFactory<T> beanFactory;
    private BeanComposeConfig composeConfig = new BeanComposeConfig();
    private TextParseConfig   parseConfig   = new TextParseConfig();

    public Text2BeanConverter(Schema parseSchema) throws IntrospectionException, ClassNotFoundException {
        this.parseSchema = parseSchema;
        this.beanFactory = new BeanFactoryByMap<>(BeanMap.ofSchema(parseSchema));
    }

    public void setBeanFactory(BeanFactory<T> beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void convert(Reader reader, BeanComposedEventListener<T> eventListener) throws IOException {
        BeanComposer<T> composer = new BeanComposer<>(composeConfig, beanFactory);
        ConvertTask convertTask = new ConvertTask(new TextParseTask(this.parseSchema, reader, parseConfig), composer);
        composer.setComposedEventListener(eventListener);
        if (beanFactory != null)
            composer.setBeanFactory(beanFactory);
        execute(convertTask);
    }

    public void setComposeConfig(BeanComposeConfig composeConfig) {
        this.composeConfig = composeConfig;
    }

    public BeanComposeConfig getComposeConfig() {
        return composeConfig;
    }

    public TextParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(TextParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
