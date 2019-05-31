package org.estatio.module.invoice.contributions;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.estatio.module.invoice.dom.docs.LookupNewestInvoiceDocumentService;
import org.estatio.module.lease.app.MissingChamberOfCommerceCodeManager;

@Mixin(method = "prop")
public class MissingChamberOfCommerceCodeManager_newestInvoice {

    private final MissingChamberOfCommerceCodeManager manager;

    public MissingChamberOfCommerceCodeManager_newestInvoice(final MissingChamberOfCommerceCodeManager manager) {
        this.manager = manager;
    }

    @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Blob prop() {
        return newestInvoiceService.newestInvoiceFor(manager.getOrganisation());
    }

    @XmlTransient
    @Inject
    LookupNewestInvoiceDocumentService newestInvoiceService;
}
