package org.estatio.module.lease.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(objectType = "party.MissingChamberOfCommerceCodeManager")
@DomainObjectLayout(cssClassFa = "fa-wrench")
@XmlRootElement(name = "missingChamberOfCommerceCodeViewModel")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class MissingChamberOfCommerceCodeManager {

    public String title() {
        return "Fix missing Chamber of Commerce codes";
    }

    public MissingChamberOfCommerceCodeManager(final List<Organisation> remainingOrganisations) {
        this.remainingOrganisations = remainingOrganisations;
        this.organisation = this.remainingOrganisations.remove(0);
    }

    @Getter @Setter
    private Organisation organisation;

    public String getRoles() {
        return organisation.getRoles().stream()
                .map(PartyRole::getRoleType)
                .filter(type -> type.getKey().equals(LeaseAgreementRoleTypeEnum.TENANT.getKey()) || type.getKey().equals(IncomingInvoiceRoleTypeEnum.SUPPLIER.getKey()))
                .map(PartyRoleType::getTitle)
                .collect(Collectors.joining(", "));
    }

    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private String chamberOfCommerceCode;

    @Getter @Setter
    public List<Organisation> remainingOrganisations = new ArrayList<>();

    @Getter @Setter
    public List<Organisation> skippedOrganisations = new ArrayList<>();

    @Getter @Setter
    @CollectionLayout(named = "No suggestions and no invoice")
    public List<Organisation> noSuggestions = new ArrayList<>();

    @Action(associateWith = "candidateCodes", semantics = SemanticsOf.IDEMPOTENT)
    public MissingChamberOfCommerceCodeManager chooseChamberOfCommerceCode(final List<OrganisationNameNumberViewModel> choice) {
        setChamberOfCommerceCode(choice.get(0).getChamberOfCommerceCode());
        return this;
    }

    public String validateChooseChamberOfCommerceCode(final List<OrganisationNameNumberViewModel> choice) {
        return choice.size() != 1 ?
                "Must select exactly one option from the list of suggestions" :
                null;
    }

    public List<OrganisationNameNumberViewModel> getCandidateCodes() {
        return lookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(organisation);
    }

    public MissingChamberOfCommerceCodeManager save() {
        this.organisation.setChamberOfCommerceCode(getChamberOfCommerceCode());

        prepareForNextOrganisation();

        return this;
    }

    public String disableSave() {
        return chamberOfCommerceCode == null ? "Chamber of Commerce code is required to save" : null;
    }

    public MissingChamberOfCommerceCodeManager skip() {
        this.skippedOrganisations.add(this.organisation);

        prepareForNextOrganisation();

        return this;
    }

    private void prepareForNextOrganisation() {
        if (organisation == null)
            return; // we hit the end of the recursive cycle

        this.organisation = this.remainingOrganisations.isEmpty() ? null : this.remainingOrganisations.remove(0);
        this.chamberOfCommerceCode = null;

        // not very helpful to the user, skip to next in line
        if (! shouldVisitNext(this.organisation)) {

            this.noSuggestions.add(this.organisation);
            prepareForNextOrganisation();
        }
    }

    /**
     * We'll visit the next organisation if at least one of the advisors thinks we should.
     */
    private boolean shouldVisitNext(final Organisation organisation) {
        for (final NextOrganisationAdvisor advisor : advisors) {
            if(advisor.shouldVisitNext(organisation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * SPI
     */
    public interface NextOrganisationAdvisor {

        boolean shouldVisitNext(Organisation organisation);

    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class InspectCandidateCodes implements NextOrganisationAdvisor {

        @Override
        public boolean shouldVisitNext(final Organisation organisation) {
            return !lookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(organisation).isEmpty();
        }

        @XmlTransient
        @Inject
        ChamberOfCommerceCodeLookUpService lookUpService;
    }


    @XmlTransient
    @Inject
    List<NextOrganisationAdvisor> advisors;

    @XmlTransient
    @Inject
    ChamberOfCommerceCodeLookUpService lookUpService;

}
