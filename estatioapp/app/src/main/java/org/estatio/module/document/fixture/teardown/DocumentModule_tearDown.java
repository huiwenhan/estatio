package org.estatio.module.document.fixture.teardown;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.document.dom.impl.applicability.Applicability;
import org.estatio.module.document.dom.impl.docs.Document;
import org.estatio.module.document.dom.impl.docs.DocumentAbstract;
import org.estatio.module.document.dom.impl.docs.DocumentTemplate;
import org.estatio.module.document.dom.impl.paperclips.Paperclip;
import org.estatio.module.document.dom.impl.types.DocumentType;

public class DocumentModule_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(Paperclip.class);
        deleteFrom(Applicability.class);
        deleteFrom(Document.class);
        deleteFrom(DocumentTemplate.class);
        deleteFrom(DocumentAbstract.class);
        deleteFrom(DocumentType.class);
    }


}
