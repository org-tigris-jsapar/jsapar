/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar;

/**
 * Utility class for the tests. This class is used by the test classes.
 * 
 * @author Jonas Stenberg
 * 
 */
public class TestPerson {

    String firstName;
    String lastName;
    int shoeSize;
    long luckyNumber;
    java.util.Date birthTime;

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
    public int getShoeSize() {
	return shoeSize;
    }

    /**
     * @param shoeSize
     *            the shoeSize to set
     */
    public void setShoeSize(int shoeSize) {
	this.shoeSize = shoeSize;
    }

    /**
     * @return the happyNumber
     */
    public long getLuckyNumber() {
	return luckyNumber;
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

}
