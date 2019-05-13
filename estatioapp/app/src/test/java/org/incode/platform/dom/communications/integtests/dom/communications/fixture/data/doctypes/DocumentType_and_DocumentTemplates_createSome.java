package org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.doctypes;

import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import org.estatio.module.invoice.dom.DocumentTypeData;

public class DocumentType_and_DocumentTemplates_createSome extends DocumentTemplateFSAbstract {

    public static final String DOC_TYPE_REF_INVOICE = DocumentTypeData.INVOICE.getRef();
    public static final String DOC_TYPE_REF_RECEIPT = DocumentTypeData.SUPPLIER_RECEIPT.getRef();


    public static final String DOC_TYPE_REF_FREEMARKER_HTML = DocumentTypeData.COVER_NOTE_PRELIM_LETTER.getRef();


    @Override
    protected void execute(final ExecutionContext executionContext) {

    }


}
