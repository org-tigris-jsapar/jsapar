package org.jsapar;

import org.jsapar.bean.JSaParCell;
import org.jsapar.bean.JSaParContainsCells;
import org.jsapar.bean.JSaParLine;

import java.time.Instant;
import java.util.Date;

/**
 * Utility class for the tests. This class is used by the test classes.
 * 
 * @author Jonas Stenberg
 * 
 */
@SuppressWarnings("unused")
@JSaParLine(lineType = "Company")
public class TstCompanyAnnotated {

    @JSaParCell(name = "Name")
    private String         name;
    @JSaParContainsCells(name = "Address") // Flattens subclass entries
    private TstPostAddress    address;
    @JSaParCell(name = "Created at")
    private Instant created;

    public TstCompanyAnnotated() {
    }

    public TstCompanyAnnotated(String name, TstPostAddress address, Instant created) {
        this.name = name;
        this.address = address;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TstPostAddress getAddress() {
        return address;
    }

    public void setAddress(TstPostAddress address) {
        this.address = address;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
