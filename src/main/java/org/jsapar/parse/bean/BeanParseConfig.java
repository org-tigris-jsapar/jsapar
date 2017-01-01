package org.jsapar.parse.bean;

/**
 * Configuration to be used while parsing Java beans into Line/Cell structure.
 */
public class BeanParseConfig {

    private int maxSubLevels = 10;

    /**
     * Sets maximum number of sub-objects that are read while storing a line object.
     * @param maxSubLevels Maximum number of sub-objects that are read while storing a line object
     */
    public void setMaxSubLevels(int maxSubLevels) {
        this.maxSubLevels = maxSubLevels;
    }

    /**
     * @return The maximum number of sub-objects that are read while storing a line object.
     */
    public int getMaxSubLevels() {
        return maxSubLevels;
    }
}
