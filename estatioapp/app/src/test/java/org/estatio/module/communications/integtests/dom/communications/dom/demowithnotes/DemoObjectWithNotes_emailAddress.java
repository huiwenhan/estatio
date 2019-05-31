package org.estatio.module.communications.integtests.dom.communications.dom.demowithnotes;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.communications.dom.impl.commchannel.CommunicationChannelOwner_emailAddressTitles;
import org.estatio.module.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;

@Mixin(method = "prop")
public class DemoObjectWithNotes_emailAddress extends
        CommunicationChannelOwner_emailAddressTitles {

    public DemoObjectWithNotes_emailAddress(final DemoObjectWithNotes demoCustomer) {
        super(demoCustomer, " | ");
    }


}
