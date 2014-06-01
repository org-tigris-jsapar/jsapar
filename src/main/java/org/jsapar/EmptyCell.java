/**
 * 
 */
package org.jsapar;

import java.text.Format;
import java.text.ParseException;
import java.util.Locale;

import org.jsapar.schema.SchemaException;


/**
 * @author stejon0
 *
 */
public class EmptyCell extends Cell implements Comparable<EmptyCell>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 36481831017227154L;
	private final static String STRING_VALUE="";

	/**
	 * @param cellType
	 */
	public EmptyCell(CellType cellType) {
		super(cellType);
	}

	/**
	 * @param name
	 * @param cellType
	 */
	public EmptyCell(String name, CellType cellType) {
		super(name, ((cellType == null) ? CellType.STRING : cellType) );
	}

	/* (non-Javadoc)
	 * @see org.jsapar.Cell#getStringValue(java.text.Format)
	 */
	@Override
	public String getStringValue(Format format)  {
		return STRING_VALUE;
	}
	
	/* (non-Javadoc)
	 * @see org.jsapar.Cell#getStringValue()
	 */
	@Override
	public String getStringValue(){
		return STRING_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.jsapar.Cell#getValue()
	 */
	@Override
	public Object getValue() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jsapar.Cell#setValue(java.lang.String, java.text.Format)
	 */
	@Override
	public void setValue(String value, Format format) throws ParseException {
	}

    @Override
    public int compareTo(EmptyCell right){
    	return 0;
    }

	@Override
	public void setValue(String value, Locale locale) throws ParseException {
	}

    @Override
    public int compareValueTo(Cell right) throws SchemaException {
        // Everything is equal to empty cell :-S.
        return 0;
    }
    
}
