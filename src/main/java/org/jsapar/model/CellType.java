package org.jsapar.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jsapar.format.BooleanFormat;
import org.jsapar.format.RegExpFormat;
import org.jsapar.schema.SchemaException;
import org.jsapar.utils.StringUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author stejon0
 *
 */
public enum CellType {
    STRING {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new StringCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new StringCell(sName, sValue);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            if (sPattern != null)
                return new RegExpFormat(sPattern);
            else
                return null;
        }
    },
    DATE {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new DateCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new DateCell(sName, sValue, locale);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            if(sPattern == null || sPattern.isEmpty())
                sPattern = "yyyy-MM-dd HH:mm:ss.SSS"; // Default to ISO date time format.
            return new java.text.SimpleDateFormat(sPattern);
        }
    },
    INTEGER {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new IntegerCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new IntegerCell(sName, sValue, locale);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            if (locale == null)
                locale = Locale.getDefault();
            if (sPattern != null && sPattern.length() > 0)
                return new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            else
                return NumberFormat.getIntegerInstance(locale);
        }
    },
    BOOLEAN {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new BooleanCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new BooleanCell(sName, sValue, locale);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) throws SchemaException {
            String[] aTrueFalse = sPattern.trim().split("\\s*;\\s*");
            if(aTrueFalse.length < 1 || aTrueFalse.length > 2)
                throw new SchemaException("Boolean format pattern should only contain two fields separated with ; character");
            return new BooleanFormat(aTrueFalse[0], aTrueFalse.length==2 ? aTrueFalse[1] : "");
        }
    },
    FLOAT {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new FloatCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new FloatCell(sName, sValue, locale);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            if (locale == null)
                locale = Locale.getDefault();
            if (sPattern != null && sPattern.length() > 0)
                return new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            else
                return NumberFormat.getInstance(locale);
        }
    },
    DECIMAL {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            if (format != null && format instanceof DecimalFormat) {
                // This is necessary because some locales (e.g. swedish)
                // have non breakable space as grouping character. Naturally
                // we want to remove all space characters including the
                // non breakable.
                DecimalFormat decFormat = (DecimalFormat) format;
                char groupingSeparator = decFormat.getDecimalFormatSymbols().getGroupingSeparator();
                if (Character.isSpaceChar(groupingSeparator)) {
                    sValue = StringUtils.removeAllSpaces(sValue);
                }
            }
            return new BigDecimalCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new BigDecimalCell(sName, sValue, locale);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            if (locale == null)
                locale = Locale.getDefault();
            DecimalFormat decFormat = new java.text.DecimalFormat(sPattern, new DecimalFormatSymbols(locale));
            decFormat.setParseBigDecimal(true);
            return decFormat;
        }
    },
    CHARACTER {
        @Override
        public Cell makeCell(String sName, String sValue, Format format) throws ParseException {
            return new CharacterCell(sName, sValue, format);
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException {
            return new CharacterCell(sName, sValue);
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            return null;
        }
    },
    CUSTOM {
        @Override
        public Cell makeCell(String sName, String sValue, Format format)  {
            throw new UnsupportedOperationException("Custom Cell type needs to override makeCell method.");
        }

        @Override
        public Cell makeCell(String sName, String sValue, Locale locale) throws ParseException  {
            throw new UnsupportedOperationException("Custom Cell type needs to override makeCell method.");
        }

        @Override
        public Format makeFormat(String sPattern, Locale locale) {
            throw new UnsupportedOperationException("CUSTOM cell type formatter can not be created without specifying a formatter.");
        }
    };
    public abstract Cell makeCell(String sName, String sValue, java.text.Format format) throws ParseException;

    public abstract Cell makeCell(String sName, String sValue, Locale locale) throws ParseException;

    public abstract Format makeFormat(String sPattern, Locale locale) ;
}
