package org.estatio.module.document.dom.impl.applicability;

import java.util.List;

import org.estatio.module.document.dom.impl.docs.Document;
import org.estatio.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.document.dom.impl.applicability.AttachmentAdvisor;

public class SomeAttachmentAdvisor implements AttachmentAdvisor {
    @Override public List<PaperclipSpec> advise(
            final DocumentTemplate documentTemplate, final Object domainObject, final Document createdDocument) {
        return null;
    }
}
