package org.estatio.capex.dom.invoice.state.transitions;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionType;

@Mixin
public class IncomingInvoice_approveAsCountryDirector extends IncomingInvoice_abstractTransition {

    public IncomingInvoice_approveAsCountryDirector(IncomingInvoice incomingInvoice) {
        super(incomingInvoice, IncomingInvoiceStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR);
    }

    @Action()
    @MemberOrder(sequence = "3")
    public IncomingInvoice $$(@Nullable final String comment) {
        return super.$$(comment);
    }

}
