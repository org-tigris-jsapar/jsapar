package org.jsapar.parse.cell;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by stejon0 on 2016-10-23.
 */
public interface CellFactory {

    static CellFactory getInstance(CellType cellType){
        return cellType.getCellFactory();
    }

    Cell makeCell(String name, String value, Format format) throws ParseException;

    Format makeFormat(Locale locale);

    Format makeFormat(Locale locale, String pattern);

}
