package org.jsapar.model;

import org.jsapar.error.JSaParNumberFormatException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Utility functions for {@link Line} that simplifies to get and set primitive types from line cells.
 */
public class LineUtils {

    /**
     * Checks if there is a cell with the specified name and if it is not empty.
     *
     * @param cellName The name of the cell to check.
     * @return true if the cell with the specified name exists and that it contains a value.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isCellSet(Line line, String cellName) {
        Cell cell = line.getCell(cellName);
        if (cell == null)
            return false;

        return !cell.isEmpty();
    }

    /**
     * Checks if there is a cell with the specified name and type and that is not empty.
     *
     * @param cellName The name of the cell to check.
     * @param type     The type to check.
     * @return true if the cell with the specified name contains a value of the specified type.
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean isCellSet(Line line, String cellName, CellType type) {
        Cell cell = line.getCell(cellName);
        if (cell == null)
            return false;

        if (cell.isEmpty())
            return false;

        return cell.getCellType().equals(type);
    }


    private static <T> void setCellValue(Line line, String cellName, T value, CellCreator<T> cellCreator) {
        if (value == null)
            line.removeCell(cellName);
        else
            line.putCell(cellCreator.createCell(cellName, value));
    }

    private interface CellCreator<T> {
        Cell createCell(String name, T value);
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
        setCellValue(line, cellName, value, StringCell::new);
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
        setCellValue(line, cellName, value, (n, v) -> new StringCell(n, String.valueOf(v)));
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
        line.putCell(new FloatCell(cellName, value));
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
        line.putCell(new BooleanCell(cellName, value));
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or replaces an
     * existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The character value to set.
     */
    public static void setCharCellValue(Line line, String cellName, char value) {
        line.putCell(new CharacterCell(cellName, value));
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The date value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setDateCellValue(Line line, String cellName, Date value) {
        setCellValue(line, cellName, value, DateCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setDecimalCellValue(Line line, String cellName, BigDecimal value) {
        setCellValue(line, cellName, value, BigDecimalCell::new);
    }

    /**
     * Utility function that adds a cell with the specified name and value to the end of the line or
     * replaces an existing cell if there already is one with the same name.
     *
     * @param cellName The name of the cell to add/replace.
     * @param value    The value to set. If null, existing value will be removed but no new value will be set.
     */
    public static void setBigIntegerCellValue(Line line, String cellName, BigInteger value) {
        setCellValue(line, cellName, value, BigDecimalCell::new);
    }

    /**
     * Utility function that gets the string cell value of the specified cell.
     *
     * @param line     The line to get value from
     * @param cellName The name of the cell to get
     * @return The value of the specified cell or null if there is no such cell.
     */
    public static String getStringCellValue(Line line, String cellName) {
        Cell cell = line.getCell(cellName);
        return (cell != null) ? cell.getStringValue() : null;
    }

    /**
     * Utility function that gets the string cell value of the specified cell.
     *
     * @param line         The line to get value from
     * @param cellName     The name of the cell to get
     * @param defaultValue The default value to return if the cell does not exists or is empty
     * @return The value of the specified cell. Returns the default value if there is no cell with supplied name or if the cell is empty.
     */
    public static String getStringCellValue(Line line, String cellName, String defaultValue) {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty() || cell.getStringValue() == null || cell.getStringValue().isEmpty())
            return defaultValue;
        return cell.getStringValue();
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
        Cell cell = line.getCell(cellName);
        if (cell == null)
            throw new IllegalStateException("There is no cell with the name '" + cellName + "' in this line");
        return cell;
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
            return numberCell.getNumberValue().intValue();
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
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().intValue();
        }

        try {
            return Integer.parseInt(cell.getStringValue());
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
            return numberCell.getNumberValue().longValue();
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
     * @throws NumberFormatException
     */
    public static long getLongCellValue(Line line, String cellName, long defaultValue) throws NumberFormatException {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().longValue();
        }

        try {
            return Long.parseLong(cell.getStringValue());
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
            return chCell.getCharacterValue();
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
     * @throws NumberFormatException
     */
    public static char getCharCellValue(Line line, String cellName, char defaultValue) throws NumberFormatException {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof CharacterCell) {
            CharacterCell chCell = (CharacterCell) cell;
            return chCell.getCharacterValue();
        }

        String s = cell.getStringValue();
        if (s.isEmpty())
            throw new NumberFormatException(
                    "Could not convert string cell [" + cell + "] to a character since string is empty.");
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
            return booleanCell.getBooleanValue();
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
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;
        if (cell instanceof BooleanCell) {
            BooleanCell booleanCell = (BooleanCell) cell;
            return booleanCell.getBooleanValue();
        }

        return Boolean.valueOf(cell.getStringValue());
    }

    /**
     * Utility function that gets the date cell value of the specified cell. If the specified
     * cell does not exist or if it is not a DateCell, a {@link IllegalStateException} is thrown.
     *
     * @param line     The line to get from
     * @param cellName The name of the cell to get
     * @return The date value of the specified cell.
     * @throws NumberFormatException If the cell does not exist
     */
    public static Date getDateCellValue(Line line, String cellName)
            throws IllegalStateException, NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof DateCell) {
            DateCell dateCell = (DateCell) cell;
            return dateCell.getDateValue();
        }

        throw new NumberFormatException("The cell " + cell + " is not of type DateCell.");
    }

    /**
     * Utility function that gets the date cell value of the specified cell. If the specified
     * cell does not exist, the supplied default value is returned. If if it is not a DateCell, a NumberFormatException is thrown.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue The default value that will be returned if the cell does not exist or does not have any value.
     * @return The date cell value if the cell exist and is of type DateCell. Returns the defaultValue if the cell does not exist.
     * @throws NumberFormatException If if it is not a DateCell.
     */
    public static Date getDateCellValue(Line line, String cellName, Date defaultValue) throws NumberFormatException {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

        if (cell instanceof DateCell) {
            DateCell dateCell = (DateCell) cell;
            return dateCell.getDateValue();
        } else if (cell instanceof StringCell) {
            if (cell.getStringValue().isEmpty())
                return defaultValue;
        }

        throw new NumberFormatException("The cell " + cell + " is not of type DateCell.");
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
    public static <E extends Enum<E>> E getEnumCellValue(Line line, String cellName, Class<E> enumClass)
            throws IllegalArgumentException {
        Cell cell = getExistingCell(line, cellName);
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
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

        String s = cell.getStringValue();
        try {
            return (E) Enum.valueOf(defaultValue.getClass(), s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Error while trying to convert cell [" + cell + "] to an enum value of type " + defaultValue
                            .getClass().getSimpleName() + ".", e);
        }
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
            return numberCell.getNumberValue().doubleValue();
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
     * @throws NumberFormatException
     */
    public static double getDoubleCellValue(Line line, String cellName, double defaultValue)
            throws NumberFormatException, IllegalStateException {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

        if (cell instanceof NumberCell) {
            NumberCell numberCell = (NumberCell) cell;
            return numberCell.getNumberValue().doubleValue();
        }

        try {
            return Double.parseDouble(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a floating point value.", e);
        }
    }

    /**
     * Utility function that gets the BigDecimal cell value of the specified cell. If the specified cell does not exist,
     * a {@link IllegalStateException} is thrown. Tries to parse a BigDecimal value if cell is not of type BigDecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException
     */
    public static BigDecimal getDecimalCellValue(Line line, String cellName) throws NumberFormatException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigDecimalValue();
        } else if (cell.isEmpty())
            throw new NumberFormatException(
                    "The cell [" + cell + "] does not have a value and thus cannot be parsed into a decimal value.");
        else if (cell instanceof NumberCell) {
            return new BigDecimal(((FloatCell) cell).getNumberValue().doubleValue());
        }

        try {
            return new BigDecimal(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a decimal value.", e);
        }

    }

    /**
     * Utility function that gets the BigDecimal cell value of the specified cell. If the specified cell does not exist,
     * a {@link IllegalStateException} is thrown. Tries to parse a BigDecimal value if cell is not of type BigDecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException
     */
    public static BigDecimal getDecimalCellValue(Line line, String cellName, BigDecimal defaultValue)
            throws NumberFormatException, IllegalStateException{
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

        else if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigDecimalValue();
        } else if (cell instanceof NumberCell) {
            return new BigDecimal(((FloatCell) cell).getNumberValue().doubleValue());
        }

        try {
            return new BigDecimal(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to a decimal value.", e);
        }
    }

    /**
     * Utility function that gets the BigInteger cell value of the specified cell. If the specified cell does not exist,
     * the supplied defaultValue is returned. Tries to parse a BigInteger value if cell is not of type DecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException
     */
    public static BigInteger getBigIntegerCellValue(Line line, String cellName)
            throws NumberFormatException, IllegalStateException {
        Cell cell = getExistingCell(line, cellName);
        if (cell instanceof BigDecimalCell) {
            BigDecimalCell numberCell = (BigDecimalCell) cell;
            return numberCell.getBigIntegerValue();
        }
        if (cell.isEmpty())
            throw new NumberFormatException(
                    "The cell [" + cell + "] does not have a value and thus cannot be parsed into an integer value.");

        try {
            return new BigInteger(cell.getStringValue());
        } catch (NumberFormatException e) {
            throw new JSaParNumberFormatException(
                    "Error while trying to convert cell [" + cell + "] to an integer value.", e);
        }

    }

    /**
     * Utility function that gets the BigInteger cell value of the specified cell. If the specified cell does not exist,
     * the supplied defaultValue is returned. Tries to parse a BigInteger value if cell is not of type DecimalCell. Throws a
     * NumberFormatException if the value is not a parsable BigDecimal.
     *
     * @param line         The line to get from
     * @param cellName     The name of the cell to get
     * @param defaultValue Default value that will be returned if the cell does not exist or does not have any value.
     * @return The double value of the cell with the specified name.
     * @throws NumberFormatException
     * @throws IllegalStateException
     */
    public static BigInteger getBigIntegerCellValue(Line line, String cellName, BigInteger defaultValue)
            throws NumberFormatException, IllegalStateException {
        Cell cell = line.getCell(cellName);
        if (cell == null || cell.isEmpty())
            return defaultValue;

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
