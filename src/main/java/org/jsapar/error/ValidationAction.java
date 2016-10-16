package org.jsapar.error;

/**
 * Enum describing different actions to take upon validation.
 * @see org.jsapar.parse.ParseConfig
 * @see org.jsapar.compose.bean.BeanComposerConfig
 */
public enum ValidationAction {
    /**
     * Generate an error event.
     */
    ERROR,

    /**
     * Throw an exception
     */
    EXCEPTION,

    /**
     * Silently ignore the current line
     */
    IGNORE_LINE,

    /**
     * Do nothing, which in most cases is the same as silently ignoring the current line.
     */
    NONE
}
