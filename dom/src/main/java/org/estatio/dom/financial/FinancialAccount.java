package org.estatio.dom.financial;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public abstract class FinancialAccount extends EstatioTransactionalObject implements Comparable<FinancialAccount>{

    
    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Type (property)
    private FinancialAccountType type;
    
    @Hidden
    @MemberOrder(sequence = "1")
    public FinancialAccountType getType() {
        return type;
    }

    public void setType(final FinancialAccountType type) {
        this.type = type;
    }
    // }}
    
    // {{ Owner (property)
    private Party owner;

    @MemberOrder(sequence = "1")
    public Party getOwner() {
        return owner;
    }

    public void setOwner(final Party owner) {
        this.owner = owner;
    }
    // }}

    
    // {{ Comparable impl
    @Override
    public int compareTo(FinancialAccount o) {
        return getName().compareTo(o.getName());
    }
    // }}

}
