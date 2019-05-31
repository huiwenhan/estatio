package org.estatio.module.communications.integtests.demo.fixture.teardown;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;
import org.estatio.module.communications.integtests.demo.dom.invoice.DemoInvoice;

public class DemoModuleTearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(ExecutionContext executionContext) {

        deleteFrom(DemoInvoice.class);
        deleteFrom(DemoObjectWithNotes.class);

    }

}
