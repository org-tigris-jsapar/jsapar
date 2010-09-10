package org.jsapar;

public class TestPostAddress {
	private String street;
	private String town;

	
	public TestPostAddress() {
		super();
	}
	
	
	public TestPostAddress(String street, String town) {
		super();
		this.street = street;
		this.town = town;
	}


	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
}
