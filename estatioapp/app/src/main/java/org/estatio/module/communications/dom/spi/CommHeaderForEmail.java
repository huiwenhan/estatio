package org.estatio.module.communications.dom.spi;

import org.estatio.module.communications.dom.impl.commchannel.EmailAddress;

import lombok.Getter;
import lombok.Setter;

public class CommHeaderForEmail extends CommHeaderAbstract<EmailAddress> {

    @Getter @Setter
    private String cc ;

    @Getter @Setter
    private String bcc;

    @Getter @Setter
    private EmailAddress from;

}
