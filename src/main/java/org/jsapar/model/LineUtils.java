package org.jsapar.model;

import org.jsapar.error.JSaParNumberFormatException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Optional;

/**
 * Utility functions for {@link Line} that simplifies to get and set primitive types from line cells.
 */
@SuppressWarnings("WeakerAccess")
public class LineUtils {

    /**
     * Just to avoid that anyone creates an instance.
     */
    private LineUtils() {
    }

    /**
     * Checks if there is a cell with the specified name and if it is not empty.
     *
     * @param cellName The name of the cell to check.
     * @return true if the cell with the specified name exists and that it contains a value.
     */
    public static boolean isCellSet(Line line, String cellName) {
        return line.getNonEmptyCell(cellName).isPresent();
    }

    /**
     * Checks if there is a cell with the specified name and type and that is not empty.
     *
     * @param cellName The name of the cell to check.
     * @param type     The type to check.
     * @return true if the cell with the specified name contains a value of the specified type.
     */
    public static boolean isCellSet(Line line, String cellName, CellType type) {
        return line.getNonEmptyCell(cellName)
                .map(cell1 -> cell1.getCellType().equals(type))
                .orElse(false);
    }


    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param line     The line to alter.
     * @param cellName The name of the cell to add/replace.
     * @param value    The string value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setStringCellValue(Line line, String cellName, String value) {
        line.putCellValue(cellName, value, StringCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param line     The line to alter
     * @param cellName The name of the cell to add/replace.
     * @param value    The string value to set. If null, existing value will be removed but no new value will be set.
     */
    public static <E extends Enum<E>> void setEnumCellValue(Line line, String cellName, E value) {
        line.putCellValue(cellName, value, (n, v) -> new StringCell(n, String.valueOf(v)));
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param line     The line to alter
     * @param cellName The name of the cell to add/replace.
     * @param value    The integer value to set.
     */
    public static void setIntCellValue(Line line, String cellName, int value) {
        line.putCell(new IntegerCell(cellName, value));
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param line     The line to alter
     * @param cellName The name of the cell to add/replace.
     * @param value    The long integer value to set.
     */
    public static void setLongCellValue(Line line, String cellName, long value) {
        line.putCell(new IntegerCell(cellName, value));
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param line     The line to alter
     * @param cellName The name of the cell to add/replace.
     * @param value    The double value to set.
     */
    public static void setDoubleCellValue(Line line, String cellName, double value) {
        line.putCellValue(cellName, value, FloatCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or replaces an
     * existing cell if there already is one with the same name.
     *
     * @param line     The line to alter
     * @param cellName The name of the cell to add/replace.
     * @param value    The boolean value to set.
     */
    public static void setBooleanCellValue(Line line, String cellName, boolean value) {
        line.putCellValue(cellName, value, BooleanCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or replaces an
     * existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The character value to set.
     */
    public static void setCharCellValue(Line line, String cellName, char value) {
        line.putCellValue(cellName, value, CharacterCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The date value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setDateCellValue(Line line, String cellName, Date value) {
        line.putCellValue(cellName, value, DateCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setDecimalCellValue(Line line, String cellName, BigDecimal value) {
        line.putCellValue(cellName, value, BigDecimalCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setBigIntegerCellValue(Line line, String cellName, BigInteger value) {
        line.putCellValue(cellName, value, BigDecimalCell::new);
    }

    /**
     * Utility function that gets the string cell value of the specified cell.
     *
     * @param line     The line to get value from
     * @param cellName The name of the cell to get
     * @return The optional value of the specified cell, not present if there is no such cell.
     */
    public static Optional<String> getStringCellValue(Line line, String cellName) {
        return line.getCell(cellName).map(Cell::getStringValue);
    }


    /**
     * Get a cell in the specified line but throw an {@link IllegalStateException} if the cell does not exist in the
     * line.
     *
     * @param line     The line to check in
     * @param cellName The name of the cell to get
     * @return The cell with the specified name.
     * @throws IllegalStateException if the cell does not exist.
     */
    private static Cell getExistingCell(Line line, String cellName) throws IllegalStateException {
        return line.getCell(cellName).orElseThrow(
                () -> new IllegalStateException("There is no cell with the name '" + cellName + "' in this line"));
    }

    /**
     * Utility function that gets the integer cell value of the specified cell. If the specified
     * cell does not exist, a {@link IllegalStateException} is thrown. Tries to parse an integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get.
     * @return The integer value of the cell with the specified name.
     * @throws IllegalStateException If the cell does not exist
     * @throws NumberFormatException If the cell could not be converted into an integer value
     */
    public static int getIntCellValue(Line line, String cellName) throws IllegalStateException, NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getValue().intValue();
        }
        if (cell.isEmpty())
            throw new NumberFormatException(
                    "The cell [" + cell + "] does not have a value and thus cannot be parsed into an integer value.");

        try {
            return Integer.parseInt(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to an integer value.", e);
        }
    }

    /**
     * Utility function that gets the integer cell value of the specified cell. If the specified
     * cell does not exist, the defaultValue is returned. Tries to parse an integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The integer value of the cell with the specified name.
     * @throws NumberFormatException If the cell could not be converted into an integer value
     */
    public static int getIntCellValue(Line line, String cellName, int defaultValue) throws NumberFormatException {
        Optional<Cell> cell = line.getNonEmptyCell(cellName);
        if (!cell.isPresent())
            return defaultValue;
        if (cell.get() instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell.get();
            return numberCell.getValue().intValue();
        }

        try {
            return Integer.parseInt(cell.get().getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to an integer value.", e);
        }
    }

    /**
     * Utility function that gets the long integer cell value of the specified cell. If the specified
     * cell does not exist, a {@link IllegalStateException} is thrown. Tries to parse a long integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The long integer value of the cell with the specified name.
     * @throws IllegalStateException If the cell does not exist
     * @throws NumberFormatException If the cell could not be converted into an integer value
     */
    public static long getLongCellValue(Line line, String cellName) throws NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getValue().longValue();
        }

        try {
            return Long.parseLong(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a long integer value.", e);
        }

    }

    /**
     * Utility function that gets the long integer cell value of the specified cell. If the specified
     * cell does not exist, the supplied default value is returned. Tries to parse a long integer value if cell is
     * not of type NumberCell. Throws a NumberFormatException if the value is not a parsable
     * integer.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The long integer value of the cell with the specified name.
     * @throws NumberFormatException If the cell value could not be converted into a long integer value.
     */
    public static long getLongCellValue(Line line, String cellName, long defaultValue) throws NumberFormatException {
        Optional<Cell> cell = line.getNonEmptyCell(cellName);
        if (!cell.isPresent())
            return defaultValue;
        if (cell.get() instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell.get();
            return numberCell.getValue().longValue();
        }

        try {
            return Long.parseLong(cell.get().getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a long integer value.", e);
        }
    }

    /**
     * Utility function that gets the char cell value of the specified cell. If the specified cell does not exist, a
     * {@link IllegalStateException} is thrown. Tries to parse a character value if cell is not of type CharacterCell. Throws a
     * NumberFormatException if the value is not a parsable character.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The char value of the cell with the specified name.
     * @throws IllegalStateException If the cell does not exist
     * @throws NumberFormatException If the cell could not be converted into an integer value
     */
    public static char getCharCellValue(Line line, String cellName) throws NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof CharacterCell) {
            CharacterCell chCell = (CharacterCell) cell;
            return chCell.getValue();
        }

        String s = cell.getStringValue();
        if (s.isEmpty())
            throw new NumberFormatException(
                    "Could not convert string cell [" + cell + "] to a character since string is empty.");
        return s.charAt(0);
    }

    /**
     * Utility function that gets the character cell value of the specified cell. If the specified cell does not exist,
     * the defaultValue is returned. Tries to parse a character value if cell is not of type CharacterCell. Throws a
     * NumberFormatException if the value is not a parsable character.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The char value of the cell with the specified name.
     * @throws NumberFormatException If cell value is empty.
     */
    public static char getCharCellValue(Line line, String cellName, char defaultValue) throws NumberFormatException {
        Optional<Cell> cell = line.getNonEmptyCell(cellName);
        if (!cell.isPresent())
            return defaultValue;
        if (cell.get() instanceof CharacterCell) {
            CharacterCell chCell = (CharacterCell) cell.get();
            return chCell.getValue();
        }

        String s = cell.get().getStringValue();
        if (s.isEmpty())
            throw new NumberFormatException(
                    "Could not convert string cell [" + cell + "] to a character since cell value is empty.");
        return s.charAt(0);
    }

    /**
     * Utility function that gets the boolean cell value of the specified cell. If the specified
     * cell does not exist, a {@link IllegalStateException} is thrown. Tries to parse a boolean value if cell is
     * not of type BooleanCell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The boolean value of the cell with the supplied name.
     * @throws IllegalStateException If the cell does not exist
     */
    public static boolean getBooleanCellValue(Line line, String cellName) throws IllegalStateException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof BooleanCell) {
            BooleanCell booleanCell = (BooleanCell) cell;
            return booleanCell.getValue();
        }

        return Boolean.valueOf(cell.getStringValue());
    }

    /**
     * Utility function that gets the boolean cell value of the specified cell. If the specified
     * cell does not exist, the defaultValue is returned. Tries to parse a boolean value if cell is
     * not of type BooleanCell.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The boolean value of the cell with the supplied name.
     */
    public static boolean getBooleanCellValue(Line line, String cellName, boolean defaultValue) {
        Optional<Cell> cell = line.getNonEmptyCell(cellName);
        if (!cell.isPresent())
            return defaultValue;
        if (cell.get() instanceof BooleanCell) {
            BooleanCell booleanCell = (BooleanCell) cell.get();
            return booleanCell.getValue();
        }

        return Boolean.valueOf(cell.get().getStringValue());
    }

    /**
     * Utility function that gets the date cell value of the specified cell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The date value of the specified cell.
     * @throws IllegalStateException If the cell is not of type {@link DateCell}.
     */
    public static Optional<Date> getDateCellValue(Line line, String cellName)
            throws IllegalStateException, NumberFormatException {
        return line.getNonEmptyCell(cellName).map(LineUtils::dateOfCell);
    }

    private static Date dateOfCell(Cell cell) {
        if (cell instanceof DateCell) {
            DateCell dateCell = (DateCell) cell;
            return dateCell.getValue();
        }

        throw new IllegalStateException("The cell " + cell + " is not of type DateCell.");
    }


    /**
     * Utility function that gets the local date cell value of the specified cell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The local date value of the specified cell. Optional.empty if cell does not exist.
     * @throws IllegalStateException If the cell is not of type {@link TemporalCell}.
     * @throws java.time.DateTimeException If the value cannot be converted to a local date.
     */
    public static Optional<LocalDate> getLocalDateCellValue(Line line, String cellName)
            throws IllegalStateException{
        return line.getNonEmptyCell(cellName).map(LineUtils::temporalOfCell).map(LocalDate::from);
    }

    /**
     * Utility function that gets the local date time cell value of the specified cell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The local date value of the specified cell. Optional.empty if cell does not exist.
     * @throws IllegalStateException If the cell is not of type {@link TemporalCell}.
     * @throws java.time.DateTimeException If the value cannot be converted to a {@link LocalDateTime}.
     */
    public static Optional<LocalDateTime> getLocalDateTimeCellValue(Line line, String cellName)
            throws IllegalStateException{
        return line.getNonEmptyCell(cellName).map(LineUtils::temporalOfCell).map(LocalDateTime::from);
    }

    /**
     * Utility function that gets the local time cell value of the specified cell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The local date value of the specified cell. Optional.empty if cell does not exist.
     * @throws IllegalStateException If the cell is not of type {@link TemporalCell}.
     * @throws java.time.DateTimeException If the value cannot be converted to a {@link ZonedDateTime}.
     */
    public static Optional<ZonedDateTime> getLocalTimeCellValue(Line line, String cellName)
            throws IllegalStateException{
        return line.getNonEmptyCell(cellName).map(LineUtils::temporalOfCell).map(ZonedDateTime::from);
    }

    /**
     * Utility function that gets the zoned date time cell value of the specified cell.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The zoned date value of the specified cell. Optional.empty if cell does not exist.
     * @throws IllegalStateException If the cell is not of type {@link TemporalCell}.
     * @throws java.time.DateTimeException If the value cannot be converted to a zoned date time.
     */
    public static Optional<LocalDateTime> getZonedDateTimeCellValue(Line line, String cellName)
            throws IllegalStateException{
        return line.getNonEmptyCell(cellName).map(LineUtils::temporalOfCell).map(LocalDateTime::from);
    }

    @SuppressWarnings("unchecked")
    private static Temporal temporalOfCell(Cell cell) {
        if (cell instanceof TemporalCell) {
            TemporalCell<Temporal> temporalCell = (TemporalCell<Temporal>) cell;
            return temporalCell.getValue();
        }

        throw new IllegalStateException("The cell " + cell + " is not of type TemporalCell.");
    }

    /**
     * Utility function that gets the enum cell value of the specified cell. If the specified cell does not exist, the
     * supplied default value is returned.
     *
     * @param line      The line to get from
     * @param cellName  The name of the cell to get
     * @param enumClass The class of the enum to convert the value into.
     * @return The enum cell value if the cell.
     * @throws IllegalStateException    If the cell does not exist
     * @throws IllegalArgumentException If the enum type of the defaultValue does not have an enum constant with the name equal to the value
     *                                  of the specified cell.
     */
    public static <E extends Enum<E>> Optional<E> getEnumCellValue(Line line, String cellName, Class<E> enumClass)
            throws IllegalArgumentException {
        return line.getNonEmptyCell(cellName).map( it -> enumOfCell(it, enumClass));
    }

    private static <E extends Enum<E>> E enumOfCell(Cell cell, Class<E> enumClass) {
        String s = cell.getStringValue();

        try {
            return Enum.valueOf(enumClass, s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Error while trying to convert cell [" + cell + "] to an enum value of type " + enumClass
                            .getSimpleName() + ".", e);
        }
    }

    /**
     * Utility function that gets the Enum cell value of the specified cell. If the specified cell does not exist, the
     * supplied default value is returned.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The enum cell value if the cell exist and can be converted to an enum. Returns the defaultValue if the cell does
     * not exist.
     * @throws IllegalArgumentException If the enum type of the defaultValue does not have an enum constant with the name equal to the value
     *                                  of the specified cell.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E getEnumCellValue(Line line, String cellName, E defaultValue)
            throws IllegalArgumentException {
        Optional<E> optionalEnum = line.getNonEmptyCell(cellName).map(it -> (E)enumOfCell(it, defaultValue.getClass()));
        return optionalEnum.orElse(defaultValue);
    }

    /**
     * Utility function that gets the double cell value of the specified cell. If the specified cell
     * does not exist, a {@link IllegalStateException} is thrown. Tries to parse a double value if cell is not of
     * type FloatCell. Throws a NumberFormatException if the value is not a parsable double.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The double value of the cell with the specified name.
     * @throws IllegalStateException If the cell does not exist
     * @throws NumberFormatException If the cell could not be converted into a double value
     */
    public static double getDoubleCellValue(Line line, String cellName)
            throws IllegalStateException, NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getValue().doubleValue();
        }
        if (cell.isEmpty())
            throw new NumberFormatException("The cell [" + cell
                    + "] does not have a value and thus cannot be parsed into a floating point value.");

        try {
            return Double.parseDouble(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a floating point value.", e);
        }
    }

    /**
     * Utility function that gets the double cell value of the specified cell. If the specified cell does not exist, the
     * supplied defaultValue is returned. Tries to parse a double value if cell is not of type FloatCell. Throws a
     * NumberFormatException if the value is not a parsable double.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException When value cannot be converted to a double
     */
    public static double getDoubleCellValue(Line line, String cellName, double defaultValue)
            throws NumberFormatException{
        Optional<Cell> cell = line.getNonEmptyCell(cellName);
        if (!cell.isPresent())
            return defaultValue;
        if (cell.get() instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell.get();
            return numberCell.getValue().doubleValue();
        }

        try {
            return Double.parseDouble(cell.get().getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a floating point value.", e);
        }
    }

    /**
     * Utility function that gets an optional BigDecimal cell value of the specified cell. Tries to parse a BigDecimal
     * value if cell is not of type BigDecimalCell. Throws a
     * NumberFormatException if the value is not a parseable BigDecimal.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @return The optional double value of the cell with the specified name.
     * @throws NumberFormatException When value cannot be converted to a BigDecimal
     */
    public static Optional<BigDecimal> getDecimalCellValue(Line line, String cellName) throws NumberFormatException {
        return line.getNonEmptyCell(cellName).map(LineUtils::bigDecimalOfCell);
    }

    private static BigDecimal bigDecimalOfCell(Cell cell) {
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigDecimalValue();
        }
        else if (cell instanceof NumberCell) {
            return new BigDecimal(((FloatCell) cell).getValue().doubleValue());
        }

        try {
            return new BigDecimal(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a decimal value.", e);
        }
    }

    /**
     * Utility function that gets the optional BigInteger cell value of the specified cell. If the specified cell does
     * not exist or is empty, the supplied optional will not be set. Tries to parse a BigInteger value if cell is not of
     * type DecimalCell. Throws a NumberFormatException if the value is not a parsable integer.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @return The optional BigInteger value of the cell with the specified name.
     * @throws NumberFormatException When value cannot be converted to a BigInteger
     */
    public static Optional<BigInteger> getBigIntegerCellValue(Line line, String cellName)
            throws NumberFormatException, IllegalStateException {
        return line.getNonEmptyCell(cellName).map(LineUtils::bigIntegerOfCell);

    }

    private static BigInteger bigIntegerOfCell(Cell cell) {
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigIntegerValue();
        }
        try {
            return new BigInteger(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to an integer value.", e);
        }
    }

}
