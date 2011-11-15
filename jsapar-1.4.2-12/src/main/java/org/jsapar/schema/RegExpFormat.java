/**
 * 
 */
package org.jsapar.schema;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.regex.Pattern;

/**
 * @author stejon0
 *
 */
public class RegExpFormat extends Format {

/**
	 * 
	 */
	private static final long serialVersionUID = 650069334821232790L;
	//	private String pattern;
	private Pattern pattern;
	/**
	 * 
	 */
	public RegExpFormat(String pattern) {
		this.pattern=Pattern.compile(pattern);
	}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		String sValue = String.valueOf(obj);
		int nEnd = Math.min(sValue.length(), pos.getEndIndex());
		toAppendTo.append(sValue.substring(pos.getBeginIndex(), nEnd));
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		String sToParse = source.substring(pos.getIndex(), source.length() );
		if( !this.pattern.matcher(source).matches() ){
			pos.setErrorIndex(pos.getIndex());
		}
		else
			pos.setIndex(source.length());
		return sToParse;
	}

	/* (non-Javadoc)
	 * @see java.text.Format#parseObject(java.lang.String)
	 */
	@Override
	public Object parseObject(String source) throws ParseException {
		if(!this.pattern.matcher(source).matches())
			throw new ParseException("Value does not match regular expression.", 0);
		return source;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pattern='" + this.pattern + "'";
	}
	
	

}
