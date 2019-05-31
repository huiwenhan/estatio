/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.module.lease.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "lease.ChamberOfCommerceCodesMenu"
)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.2"
)
public class ChamberOfCommerceCodesMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-wrench")
    @MemberOrder(sequence = "98")
    public MissingChamberOfCommerceCodeManager fixMissingChamberOfCommerceCodes(
            final Country country,
            final IPartyRoleType role,
            final @ParameterLayout(named = "Start from bottom?") boolean reversed) {
        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);
        List<Organisation> organisationsMissingCode = organisationRepository.findByAtPathMissingChamberOfCommerceCode(applicationTenancy.getPath())
                .stream()
                .filter(org -> org.hasPartyRoleType(role))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), lst -> {
                            if (reversed) Collections.reverse(lst);
                            return lst;
                        }
                ));

        return new MissingChamberOfCommerceCodeManager(organisationsMissingCode);
    }

    public List<Country> choices0FixMissingChamberOfCommerceCodes() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public List<PartyRoleType> choices1FixMissingChamberOfCommerceCodes() {
        return Arrays.asList(
                partyRoleTypeRepository.findByKey(LeaseAgreementRoleTypeEnum.TENANT.getKey()),
                partyRoleTypeRepository.findByKey(IncomingInvoiceRoleTypeEnum.SUPPLIER.getKey())
        );
    }

    // //////////////////////////////////////

    @Inject
    OrganisationRepository organisationRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

}
