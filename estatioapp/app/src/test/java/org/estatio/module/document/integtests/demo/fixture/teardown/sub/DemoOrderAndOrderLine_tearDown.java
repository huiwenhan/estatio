package org.estatio.module.document.integtests.demo.fixture.teardown.sub;

import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.document.integtests.demo.dom.order.DemoOrder;
import org.estatio.module.document.integtests.demo.dom.order.DemoOrderLine;

public class DemoOrderAndOrderLine_tearDown extends TeardownFixtureAbstract2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {
        deleteFrom(DemoOrderLine.class);
        deleteFrom(DemoOrder.class);
    }

}
