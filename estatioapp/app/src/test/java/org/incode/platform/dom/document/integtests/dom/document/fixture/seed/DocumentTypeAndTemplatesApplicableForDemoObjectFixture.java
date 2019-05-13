package org.incode.platform.dom.document.integtests.dom.document.fixture.seed;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

import lombok.Getter;

public class DocumentTypeAndTemplatesApplicableForDemoObjectFixture extends DocumentTemplateFSAbstract {

    // applicable to DemoObject.class

    public static final String DOC_TYPE_REF_TAX_RECEIPT = "TAX_RECEIPT";
    public static final String DOC_TYPE_REF_SUPPLIER_RECEIPT = "SUPPLIER_RECEIPT";

    @Getter
    DocumentTemplate fmkTemplate;

    @Getter
    DocumentTemplate siTemplate;

    @Getter
    DocumentTemplate xdpTemplate;

    @Getter
    DocumentTemplate xddTemplate;

    @Override
    protected void execute(final ExecutionContext executionContext) {

    }


}
