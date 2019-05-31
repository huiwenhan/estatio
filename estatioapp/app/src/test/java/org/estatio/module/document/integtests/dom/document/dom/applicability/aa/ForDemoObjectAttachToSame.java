package org.estatio.module.document.integtests.dom.document.dom.applicability.aa;

import java.util.Collections;
import java.util.List;

import org.estatio.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.estatio.module.document.dom.impl.applicability.AttachmentAdvisorAbstract;
import org.estatio.module.document.dom.impl.docs.Document;
import org.estatio.module.document.dom.impl.docs.DocumentTemplate;
import org.estatio.module.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

import lombok.Value;

public class ForDemoObjectAttachToSame extends AttachmentAdvisorAbstract<DemoObjectWithUrl> {

    public ForDemoObjectAttachToSame() {
        super(DemoObjectWithUrl.class);
    }

    @Override
    protected List<PaperclipSpec> doAdvise(
            final DocumentTemplate documentTemplate,
            final DemoObjectWithUrl demoObject,
            final Document createdDocument) {
        return Collections.singletonList(new AttachmentAdvisor.PaperclipSpec(null, demoObject, createdDocument));
    }

    @Value
    public static class DataModel {
        DemoObjectWithUrl demoObject;
    }

}

