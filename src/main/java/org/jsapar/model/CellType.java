package org.jsapar.model;

import org.jsapar.parse.cell.*;

/**
 * Enum used to be able to denote the type of an instance of a {@link Cell}.
 *
 */
public enum CellType {

    /**
     * Cell contains {@link String} content.
     */
    STRING(new StringCellFactory(), false, false, false),
    /**
     * Cell contains {@link java.util.Date} content
     */
    DATE(new DateCellFactory(), false, false, false),
    /**
     * Cell contains integer content, up to the size of a long.
     */
    INTEGER(new IntegerCellFactory(), true, true, false),
    /**
     * Cell contains boolean content.
     */
    BOOLEAN(new BooleanCellFactory(), true, false, false),
    /**
     * Cell contains floating point precision content up to the size of a double.
     */
    FLOAT(new FloatCellFactory(), false, true, false),
    /**
     * Cell contains {@link java.math.BigDecimal} or {@link java.math.BigInteger} content.
     */
    DECIMAL(new BigDecimalCellFactory(), false, true, false),
    /**
     * Cell contains single character content.
     */
    CHARACTER(new CharacterCellFactory(), true, false, false),
    /**
     * Cell contains custom content.
     */
    CUSTOM(null, false, false, false),
    /**
     * Cell contains {@link java.time.LocalDateTime} content.
     */
    LOCAL_DATE_TIME(new LocalDateTimeCellFactory(), false, false, true),
    /**
     * Cell contains {@link java.time.LocalDate} content.
     */
    LOCAL_DATE(new LocalDateCellFactory(), false, false, true),
    /**
     * Cell contains {@link java.time.LocalTime} content.
     */
    LOCAL_TIME(new LocalTimeCellFactory(), false, false, true),
    /**
     * Cell contains {@link java.time.ZonedDateTime} content.
     */
    ZONED_DATE_TIME(new ZonedDateTimeCellFactory(), false, false, true),

    /**
     * Cell contains an enumerated set of values.
     */
    ENUM(new EnumCellFactory(), true, false, false);

    private final CellFactory cellFactory;
    private final boolean atomic;
    private final boolean number;
    private final boolean temporal;

    CellType(CellFactory cellFactory, boolean atomic, boolean number, boolean temporal) {
        this.cellFactory = cellFactory;
        this.atomic = atomic;
        this.number = number;
        this.temporal = temporal;
    }

    public CellFactory getCellFactory() {
        return cellFactory;
    }

    /**
     * @return True if this type is atomic in a sense that it cannot contain quote character, cell- or line separator character.
     */
    public boolean isAtomic() {
        return atomic;
    }

    /**
     * @return True if this type is numeric, that is if the content can be seen as a subtype of {@link Number}
     */
    public boolean isNumber(){
        return number;
    }

    /**
     * @return True if this type is temporal, that is parses and produces objects that implements {@link java.time.temporal.TemporalAccessor}
     */
    public boolean isTemporal() {
        return temporal;
    }
}
