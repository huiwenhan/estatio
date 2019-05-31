package org.estatio.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.module.document.dom.impl.docs.DocumentTemplate;

@DomainObject(
        objectType = "org.incode.module.document.dom.impl.docs.DocumentTemplateForTesting",
        editing = Editing.DISABLED
)
public class DocumentTemplateForTesting extends DocumentTemplate {
}
