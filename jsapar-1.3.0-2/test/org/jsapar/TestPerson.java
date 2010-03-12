/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar;

import java.util.Date;

/**
 * Utility class for the tests. This class is used by the test classes.
 * 
 * @author Jonas Stenberg
 * 
 */
public class TestPerson {

    private String         firstName;
    private String         lastName;
    private short          shoeSize;
    private long           luckyNumber;
    private int            streetNumber;
    private java.util.Date birthTime;

    public TestPerson(String firstName, String lastName, short shoeSize, long luckyNumber, Date birthTime, int streetNumber) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.shoeSize = shoeSize;
        this.setStreetNumber(streetNumber);
        this.luckyNumber = luckyNumber;
        this.birthTime = birthTime;
    }

    /**
     * 
     */
    public TestPerson() {
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the shoeSize
     */
    public short getShoeSize() {
        return shoeSize;
    }

    /**
     * @param shoeSize
     *            the shoeSize to set
     */
    public void setShoeSize(short shoeSize) {
        this.shoeSize = shoeSize;
    }

    /**
     * @return the happyNumber
     */
    public long getLuckyNumber() {
        return luckyNumber;
    }

    public void setLuckyNumber(String theNumber) {
        this.luckyNumber = Long.valueOf(theNumber);
    }

    /**
     * @param happyNumber
     *            the happyNumber to set
     */
    public void setLuckyNumber(long happyNumber) {
        this.luckyNumber = happyNumber;
    }

    /**
     * @return the birthTime
     */
    public java.util.Date getBirthTime() {
        return birthTime;
    }

    /**
     * @param birthTime
     *            the birthTime to set
     */
    public void setBirthTime(java.util.Date birthTime) {
        this.birthTime = birthTime;
    }

    /**
     * @param streetNumber the streetNumber to set
     */
    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * @return the streetNumber
     */
    public int getStreetNumber() {
        return streetNumber;
    }

}
