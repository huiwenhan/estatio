/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.seed;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Clob;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import org.estatio.module.base.seed.RenderingStrategies;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.lease.spiimpl.document.binders.AttachToNone;
import org.estatio.module.lease.spiimpl.document.binders.ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments;
import org.estatio.module.lease.spiimpl.document.binders.FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover;
import org.estatio.module.lease.spiimpl.document.binders.StringInterpolatorToSsrsUrlOfInvoice;
import org.estatio.module.lease.spiimpl.document.binders.StringInterpolatorToSsrsUrlOfInvoiceSummary;

public class DocumentTypeAndTemplatesFSForInvoicesUsingSsrs extends DocumentTemplateFSAbstract {


    public static final String URL = "${reportServerBaseUrl}";

    private LocalDate templateDateIfAny;

    public DocumentTypeAndTemplatesFSForInvoicesUsingSsrs() {
        this(null);
    }

    public DocumentTypeAndTemplatesFSForInvoicesUsingSsrs(
            final LocalDate templateDateIfAny) {
        this.templateDateIfAny = templateDateIfAny;
    }

    LocalDate getTemplateDateElseNow() {
        return templateDateIfAny != null ? templateDateIfAny : clockService.now();
    }

    protected DocumentType upsertType(
            DocumentTypeData documentTypeData,
            ExecutionContext executionContext) {

        return upsertType(documentTypeData.getRef(), documentTypeData.getName(), executionContext);
    }


    @Override
    protected void execute(final ExecutionContext executionContext) {

        final LocalDate templateDate = getTemplateDateElseNow();

        // prereqs
        executionContext.executeChild(this, ApplicationTenancy_enum.Global.builder());
        executionContext.executeChild(this, ApplicationTenancy_enum.It.builder());

        upsertTemplatesForInvoice(templateDate, executionContext);

        upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(templateDate, executionContext);
    }

    private void upsertTemplatesForInvoice(
            final LocalDate templateDate,
            final ExecutionContext executionContext) {

        final String url = "${reportServerBaseUrl}";

        final RenderingStrategy fmkRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_FMK);
        final RenderingStrategy sipcRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy siRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);



        //
        // prelim letter
        //


        // template for PL cover note
        final DocumentTypeData docTypeDataForPrelimCoverNote = DocumentTypeData.COVER_NOTE_PRELIM_LETTER;
        final DocumentType docTypeForPrelimCoverNote =
                upsertType(docTypeDataForPrelimCoverNote, executionContext);

        String contentText = loadResource("PrelimLetterEmailCoverNote.html.ftl");
        String subjLneText = loadResource("PrelimLetterEmailCoverNoteSubjectLine.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote, docTypeDataForPrelimCoverNote,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("PrelimLetterEmailCoverNote-ITA.html.ftl");
        subjLneText = loadResource("PrelimLetterEmailCoverNoteSubjectLine-ITA.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForPrelimCoverNote, docTypeDataForPrelimCoverNote,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);


        // template for PL itself
        final DocumentTypeData typeDataForPrelim = DocumentTypeData.PRELIM_LETTER;
        final DocumentType docTypeForPrelim =
                upsertType(typeDataForPrelim, executionContext);
        String titleText = loadResource("PrelimLetterTitle.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim, typeDataForPrelim,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                false,
                url
                        + "PreliminaryLetterV2"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );

        // (currently) this is identical to global
        titleText = loadResource("PrelimLetterTitle-ITA.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForPrelim, typeDataForPrelim,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                false,
                url
                        + "PreliminaryLetterV2"
                        + "&id=${this.id}"
                        + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );


        //
        // invoice
        //

        // template for invoice cover note
        final DocumentTypeData typeDataForInvoiceCoverNote = DocumentTypeData.COVER_NOTE_INVOICE;
        final DocumentType docTypeForInvoiceCoverNote =
                upsertType(typeDataForInvoiceCoverNote, executionContext);

        contentText = loadResource("InvoiceEmailCoverNote.html.ftl");
        subjLneText = loadResource("InvoiceEmailCoverNoteSubjectLine.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote, typeDataForInvoiceCoverNote,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
                executionContext);

        contentText = loadResource("InvoiceEmailCoverNote-ITA.html.ftl");
        subjLneText = loadResource("InvoiceEmailCoverNoteSubjectLine-ITA.ftl");
        upsertDocumentTemplateForTextHtmlWithApplicability(
                docTypeForInvoiceCoverNote, typeDataForInvoiceCoverNote,
                templateDate, ApplicationTenancy_enum.It.getPath(), " (Italy)",
                contentText,
                subjLneText,
                Document.class,
                FreemarkerModelOfPrelimLetterOrInvoiceDocForEmailCover.class,
                AttachToNone.class,
				executionContext
        );


        // template for invoice itself
        final DocumentTypeData typeDataForInvoice = DocumentTypeData.INVOICE;
        final DocumentType docTypeForInvoice = upsertType(typeDataForInvoice, executionContext);

        titleText = loadResource("InvoiceTitle.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice, typeDataForInvoice,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                false,
                url
                + "InvoiceItaly"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );

        // (currently) this is identical to global
        // TODO: this was, and certainly now is, not true any more. Is seeding the right way to go? I find it confusing because throught the UI you can upload a template and that one is overwritten by this seed ... - see ECP-906
        titleText = loadResource("InvoiceTitle-ITA.ftl");
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoice, typeDataForInvoice,
                templateDate, ApplicationTenancy_enum.It.getPath(), "( Italy)",
                false,
                url
                + "InvoiceItaly"
                + "&id=${this.id}"
                + "&rs:Command=Render&rs:Format=PDF",
                titleText,
                Invoice.class,
                StringInterpolatorToSsrsUrlOfInvoice.class,
                ForPrimaryDocOfInvoiceAttachToInvoiceAndAnyRelevantSupportingDocuments.class,
                executionContext
        );

        //
        // document types without any templates
        // (used to attach supporting documents to invoice)
        //
        upsertType(DocumentTypeData.SUPPLIER_RECEIPT, executionContext);
        upsertType(DocumentTypeData.TAX_REGISTER, executionContext);
        upsertType(DocumentTypeData.SPECIAL_COMMUNICATION, executionContext);
        upsertType(DocumentTypeData.CALCULATION, executionContext);

    }

    private void upsertTemplatesForInvoiceSummaryForPropertyDueDateStatus(
            final LocalDate templateDate, final ExecutionContext executionContext) {

        final RenderingStrategy sipcRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SIPC);
        final RenderingStrategy siRenderingStrategy =
                renderingStrategyRepository.findByReference(RenderingStrategies.REF_SI);

        final DocumentTypeData typeDataForInvoices = DocumentTypeData.INVOICES;
        final DocumentType documentTypeForInvoices = upsertType(typeDataForInvoices, executionContext);
        upsertTemplateForPdfWithApplicability(
                documentTypeForInvoices, typeDataForInvoices,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "Invoices"
                + "&dueDate=${this.dueDate}&${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Invoices overview",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        final DocumentTypeData typeDataForInvoicesPrelim = DocumentTypeData.INVOICES_PRELIM;
        final DocumentType docTypeForInvoicesPrelim = upsertType(typeDataForInvoicesPrelim, executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoicesPrelim, typeDataForInvoicesPrelim,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "PreliminaryLetterV2"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary letter for Invoices",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );

        final DocumentTypeData typeDataForInvoicesForSeller = DocumentTypeData.INVOICES_FOR_SELLER;
        final DocumentType docTypeForInvoicesForSeller = upsertType(typeDataForInvoicesForSeller, executionContext);
        upsertTemplateForPdfWithApplicability(
                docTypeForInvoicesForSeller, typeDataForInvoicesForSeller,
                templateDate, ApplicationTenancy_enum.Global.getPath(), null,
                true,
                URL
                + "PreliminaryLetterV2"
                + "&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}"
                + "&rs:Command=Render&rs:Format=PDF",
                "Preliminary Invoice for Seller",
                InvoiceSummaryForPropertyDueDateStatus.class,
                StringInterpolatorToSsrsUrlOfInvoiceSummary.class,
                AttachToNone.class,  // since preview only
                executionContext
        );
    }

    private DocumentTemplate upsertTemplateForPdfWithApplicability(
            final DocumentType documentType,
            final DocumentTypeData typeData,
            final LocalDate templateDate,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText,
            final String nameText,
            final Class<?> applicableToClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final DocumentTemplate template =
                upsertTemplateForPdf(documentType, typeData, templateDate, atPath, templateNameSuffixIfAny, previewOnly,
				        contentText,
                        nameText,
                        executionContext);

        mixin(DocumentTemplate._applicable.class, template)
                .applicable(applicableToClass, rendererModelFactoryClass, attachmentAdvisorClass);

        return template;
    }

    private DocumentTemplate upsertTemplateForPdf(
            final DocumentType docType,
            final DocumentTypeData typeData,
            final LocalDate templateDate,
            final String atPath,
            final String templateNameSuffixIfAny,
            final boolean previewOnly,
            final String contentText,
            final String nameText,
            final ExecutionContext executionContext) {

        return upsertDocumentTextTemplate(
                docType, typeData, templateDate, atPath,
                ".pdf",
                previewOnly,
                buildTemplateName(typeData, docType, templateNameSuffixIfAny),
                MimeTypeData.APPLICATION_PDF.asStr(),
                contentText,
                nameText,
                executionContext);
    }

    private void upsertDocumentTemplateForTextHtmlWithApplicability(
            final DocumentType docType,
            final DocumentTypeData typeData,
            final LocalDate templateDate,
            final String atPath,
            final String nameSuffixIfAny,
            final String contentText,
            final String nameText,
            final Class<?> domainClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass,
            final ExecutionContext executionContext) {

        final Clob clob = new Clob(buildTemplateName(docType, typeData, nameSuffixIfAny, ".html"), MimeTypeData.TEXT_HTML.asStr(), contentText);
        final DocumentTemplate documentTemplate = upsertDocumentClobTemplate(
                docType, typeData, templateDate, atPath,
                ".html",
                false,
                clob,
                nameText,
                executionContext);

        mixin(DocumentTemplate._applicable.class, documentTemplate).applicable(domainClass, rendererModelFactoryClass, attachmentAdvisorClass);

        executionContext.addResult(this, documentTemplate);
    }


    public static String loadResource(final String resourceName) {
        final URL templateUrl = Resources
                .getResource(DocumentTypeAndTemplatesFSForInvoicesUsingSsrs.class, resourceName);
        try {
            return Resources.toString(templateUrl, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource URL '%s'", templateUrl));
        }
    }


    @Inject
    RenderingStrategyRepository renderingStrategyRepository;
    @Inject
    ClockService clockService;


}
