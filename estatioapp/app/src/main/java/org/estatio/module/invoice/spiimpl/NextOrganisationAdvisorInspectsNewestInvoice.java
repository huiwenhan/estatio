package org.estatio.module.invoice.spiimpl;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.invoice.dom.docs.LookupNewestInvoiceDocumentService;
import org.estatio.module.party.app.MissingChamberOfCommerceCodeManager;
import org.estatio.module.party.dom.Organisation;

@DomainService(nature = NatureOfService.DOMAIN)
public class NextOrganisationAdvisorInspectsNewestInvoice implements
        MissingChamberOfCommerceCodeManager.NextOrganisationAdvisor {

    @Override
    public boolean shouldVisitNext(final Organisation organisation) {
        return newestInvoiceService.newestInvoiceFor(organisation) != null;
    }

    @XmlTransient
    @Inject
    LookupNewestInvoiceDocumentService newestInvoiceService;

}
