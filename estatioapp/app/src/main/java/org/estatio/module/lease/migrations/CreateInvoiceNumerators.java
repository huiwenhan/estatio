/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.migrations;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.services.message.MessageService;

@DomainObject(
        objectType = "org.estatio.module.lease.migrations.CreateInvoiceNumerators"
)
public class CreateInvoiceNumerators extends DiscoverableFixtureScript {


    @Override
    protected void execute(ExecutionContext ec) {
        messageService.warnUser("This fixture script does nothing");
//        final List<FixedAssetRoleTypeEnum> roleTypes = Arrays.asList(
//                FixedAssetRoleTypeEnum.PROPERTY_OWNER,
//                FixedAssetRoleTypeEnum.TENANTS_ASSOCIATION);
//
//        for (final Property property : propertyRepository.allProperties()) {
//            for (final FixedAssetRole fixedAssetRole : fixedAssetRoleRepository.findAllForProperty(property)){
//                if (roleTypes.contains(fixedAssetRole.getType())) {
//                    final Party party = fixedAssetRole.getParty();
//                    final Numerator numerator =
//                            estatioNumeratorRepository.createInvoiceNumberNumerator(
//                                    property,
//                                    party,
//                                    PropertyOwnerBuilder.numeratorReferenceFor(property),
//                                    bi(0));
//
//                    ec.addResult(this, property.getReference(), numerator);
//                }
//            }
//        }
    }
    // //////////////////////////////////////

    @javax.inject.Inject
    MessageService messageService;
//    @javax.inject.Inject
//    NumeratorForOutgoingInvoicesRepository estatioNumeratorRepository;
//
//    @javax.inject.Inject
//    PropertyRepository propertyRepository;
//
//    @javax.inject.Inject
//    FixedAssetRoleRepository fixedAssetRoleRepository;
}
