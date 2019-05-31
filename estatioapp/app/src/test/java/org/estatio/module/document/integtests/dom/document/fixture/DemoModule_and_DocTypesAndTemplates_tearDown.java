package org.estatio.module.document.integtests.dom.document.fixture;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.document.fixture.teardown.DocumentModule_tearDown;
import org.estatio.module.document.integtests.demo.fixture.teardown.sub.DemoObjectWithUrl_tearDown;
import org.estatio.module.document.integtests.demo.fixture.teardown.sub.OtherObject_tearDown;
import org.estatio.module.document.integtests.dom.document.dom.paperclips.demowithurl.PaperclipForDemoObjectWithUrl;
import org.estatio.module.document.integtests.dom.document.dom.paperclips.other.PaperclipForOtherObject;

public class DemoModule_and_DocTypesAndTemplates_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // paperclip links
        deleteFrom(PaperclipForDemoObjectWithUrl.class);
        deleteFrom(PaperclipForOtherObject.class);

        // documents
        executionContext.executeChild(this, new DocumentModule_tearDown());

        // demo objects
        executionContext.executeChild(this, new DemoObjectWithUrl_tearDown());
        executionContext.executeChild(this, new OtherObject_tearDown());

    }


}
