package org.jsapar;

import org.jsapar.bean.JSaParCell;

public class TstPostAddress {
    @JSaParCell(name = "Street")
    private String street;
    private String town;
    private TstPostAddress subAddress;

    @JSaParCell(name = "Street number")
    private int streetNumber;

    public TstPostAddress() {
        super();
    }

    public TstPostAddress(String street, String town) {
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

    public void setSubAddress(TstPostAddress subAddress) {
        this.subAddress = subAddress;
    }

    public TstPostAddress getSubAddress() {
        return subAddress;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }
}
