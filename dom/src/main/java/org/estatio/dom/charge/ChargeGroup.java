package org.estatio.dom.charge;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(
        name = "charge_findChargeGroupByReference", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.charge.ChargeGroup " +
        		"WHERE reference.mathes(:r)")
@Immutable
@Bounded
public class ChargeGroup extends EstatioRefDataObject<ChargeGroup> implements ComparableByReference<ChargeGroup> {


    public ChargeGroup() {
        super("reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @MemberOrder(sequence = "1")
    @Title(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String description;

    @Title(sequence = "2", prepend = "-")
    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "group")
    private SortedSet<Charge> charges = new TreeSet<Charge>();

    @MemberOrder(sequence = "1")
    public SortedSet<Charge> getCharges() {
        return charges;
    }

    public void setCharges(final SortedSet<Charge> charges) {
        this.charges = charges;
    }

}
