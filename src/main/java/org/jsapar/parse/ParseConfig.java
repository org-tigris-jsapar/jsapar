package org.jsapar.parse;

import org.jsapar.error.ValidationAction;

/**
 * Configuration that controls behavior while parsing.
 * <p/>
 * Created by stejon0 on 2016-07-12.
 */
public class ParseConfig {

    private ValidationAction onUndefinedLineType = ValidationAction.EXCEPTION;
    private ValidationAction onLineInsufficient  = ValidationAction.NONE;
    private ValidationAction onLineOverflow      = ValidationAction.NONE;

    /**
     * @return The action to take if the cell value conditions of the line does not match any of the defined line types
     * within the schema.
     */
    public ValidationAction getOnUndefinedLineType() {
        return onUndefinedLineType;
    }

    /**
     * @param onUndefinedLineType The action to take if the cell value conditions of the line does not match any of the
     *                            defined line types within the schema.
     */
    public void setOnUndefinedLineType(ValidationAction onUndefinedLineType) {
        this.onUndefinedLineType = onUndefinedLineType;
    }

    /**
     * @return The action to take if there is insufficient input to build a complete line. For Csv, this happens for
     * instance when there are too few columns. For fixed width files this happens when the line is too short.
     */
    public ValidationAction getOnLineInsufficient() {
        return onLineInsufficient;
    }

    /**
     * @param onLineInsufficient The action to take if there is insufficient input to build a complete line. For Csv, this happens for
     * instance when there are too few columns. For fixed width files this happens when the line is too short.
     */
    public void setOnLineInsufficient(ValidationAction onLineInsufficient) {
        this.onLineInsufficient = onLineInsufficient;
    }

    /**
     * @return The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     * instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     */
    public ValidationAction getOnLineOverflow() {
        return onLineOverflow;
    }

    /**
     * @param onLineOverflow The action to take if there is too much data in the input after parsing a complete line. For Csv, this happens for
     * instance when there are too many columns. For fixed width files this happens when the line is too long compared to what is defined in the schema.
     */
    public void setOnLineOverflow(ValidationAction onLineOverflow) {
        this.onLineOverflow = onLineOverflow;
    }
}
