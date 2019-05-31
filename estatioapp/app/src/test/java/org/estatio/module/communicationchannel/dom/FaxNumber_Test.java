package org.estatio.module.communicationchannel.dom;

import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.communications.dom.impl.commchannel.CommunicationChannelOwner;
import org.estatio.module.communications.dom.impl.commchannel.PhoneOrFaxNumber;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;

public class FaxNumber_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(CommunicationChannelOwner.class, CommunicationChannelOwnerForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new PhoneOrFaxNumber(), PojoTester.FilterSet.excluding("owner"));
        }

    }
}