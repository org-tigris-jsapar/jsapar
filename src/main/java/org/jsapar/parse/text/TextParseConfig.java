package org.jsapar.parse.text;

import org.jsapar.error.ValidationAction;

/**
 * Configuration that controls behavior while parsing text.
 * <p/>
 * Created by stejon0 on 2016-07-12.
 */
public class TextParseConfig {

    /**
     * The action to take if the cell value conditions of the line does not match any of the defined line types
     * within the schema. Default is to throw exception.
     */
    private ValidationAction onUndefinedLineType = ValidationAction.EXCEPTION;
    /**
     * The action to take if there is insufficient input to build a complete line. For Csv, this happens for
     * instance when there are too few columns. For fixed width files this happens when the line is too short.
     * Default is no action.
     */
    private ValidationAction onLineInsufficient  = ValidationAction.NONE;

    /**
     * The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     * instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     * Default is no action.
     */
    private ValidationAction onLineOverflow      = ValidationAction.NONE;

    public ValidationAction getOnUndefinedLineType() {
        return onUndefinedLineType;
    }

    public void setOnUndefinedLineType(ValidationAction onUndefinedLineType) {
        this.onUndefinedLineType = onUndefinedLineType;
    }

    public ValidationAction getOnLineInsufficient() {
        return onLineInsufficient;
    }

    public void setOnLineInsufficient(ValidationAction onLineInsufficient) {
        this.onLineInsufficient = onLineInsufficient;
    }

    public ValidationAction getOnLineOverflow() {
        return onLineOverflow;
    }

    public void setOnLineOverflow(ValidationAction onLineOverflow) {
        this.onLineOverflow = onLineOverflow;
    }
}
