package org.estatio.module.capex.dom.docs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.document.dom.impl.docs.Document;
import org.estatio.module.document.dom.impl.docs.DocumentAbstract;
import org.estatio.module.party.dom.Organisation;

@DomainService(nature = NatureOfService.DOMAIN)
public class LookupNewestIncomingInvoiceDocumentService {

    @Programmatic
    public Blob newestInvoiceFor(final Organisation organisation) {
        Optional<IncomingInvoice> invoiceIfAny = incomingInvoiceRepository.findBySellerAndApprovalStates(organisation, Arrays
                .asList(IncomingInvoiceApprovalState.values()))
                .stream()
                .max(Comparator.comparing(IncomingInvoice::getInvoiceDate));

        Optional<Document> documentIfAny = invoiceIfAny.isPresent() ?
                pdfService.lookupIncomingInvoicePdfFrom(invoiceIfAny.get()) :
                Optional.empty();

        return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
    }

    @XmlTransient
    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @XmlTransient
    @Inject
    LookupAttachedPdfService pdfService;
}
