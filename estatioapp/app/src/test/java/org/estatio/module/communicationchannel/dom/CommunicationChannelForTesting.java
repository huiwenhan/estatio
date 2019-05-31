package org.estatio.module.communicationchannel.dom;

import org.estatio.module.communications.dom.impl.commchannel.CommunicationChannel;

@javax.jdo.annotations.Discriminator("org.estatio.module.communicationchannel.dom.CommunicationChannelForTesting")
public class CommunicationChannelForTesting extends CommunicationChannel {

    public String getName() {
        return null;
    }

}
