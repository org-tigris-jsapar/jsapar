package org.jsapar.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stejon0 on 2016-03-12.
 */
public class MatchingCellValueCondition implements CellValueCondition {
    private final Pattern pattern;

    public MatchingCellValueCondition(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean satisfies(String value) {
        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
