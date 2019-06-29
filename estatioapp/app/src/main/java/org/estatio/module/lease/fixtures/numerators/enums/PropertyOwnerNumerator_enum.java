package org.estatio.module.lease.fixtures.numerators.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.invoicegroup.dom.InvoiceGroupRepository;
import org.estatio.module.invoicegroup.fixtures.InvoiceGroup_enum;
import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.lease.fixtures.numerators.builders.PropertyOwnerNumeratorBuilder;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyOwnerNumerator_enum implements PersonaWithFinder<Numerator>, PersonaWithBuilderScript<Numerator, PropertyOwnerNumeratorBuilder> {

    BudNl   (PropertyAndUnitsAndOwnerAndManager_enum.BudNl, InvoiceGroup_enum.BudNl),
    RonIt   (PropertyAndUnitsAndOwnerAndManager_enum.RonIt, InvoiceGroup_enum.RonIt),
    GraIt   (PropertyAndUnitsAndOwnerAndManager_enum.GraIt, InvoiceGroup_enum.GraIt),
    HanSe   (PropertyAndUnitsAndOwnerAndManager_enum.HanSe, InvoiceGroup_enum.HanSe),
    KalNl   (PropertyAndUnitsAndOwnerAndManager_enum.KalNl, InvoiceGroup_enum.KalNl),
    MacFr   (PropertyAndUnitsAndOwnerAndManager_enum.MacFr, InvoiceGroup_enum.MacFr),
    MnsFr   (PropertyAndUnitsAndOwnerAndManager_enum.MnsFr, InvoiceGroup_enum.MnsFr),
    OxfGb   (PropertyAndUnitsAndOwnerAndManager_enum.OxfGb, InvoiceGroup_enum.OxfGb),
    VivFr   (PropertyAndUnitsAndOwnerAndManager_enum.VivFr, InvoiceGroup_enum.VivFr);

    private final PropertyAndUnitsAndOwnerAndManager_enum propertyAndUnitsAndOwnerAndManager_d;
    private final InvoiceGroup_enum invoiceGroup_d;

    @Override
    public Numerator findUsing(final ServiceRegistry2 serviceRegistry) {

        final Property property = propertyAndUnitsAndOwnerAndManager_d.findUsing(serviceRegistry);
        final Organisation owner = propertyAndUnitsAndOwnerAndManager_d.getOwner_d().findUsing(serviceRegistry);

        final InvoiceGroupRepository invoiceGroupRepository =
                serviceRegistry.lookupService(InvoiceGroupRepository.class);
        final NumeratorForOutgoingInvoicesRepository repository =
                serviceRegistry.lookupService(NumeratorForOutgoingInvoicesRepository.class);

        return invoiceGroupRepository.findContainingProperty(property)
                .map(invoiceGroup -> repository.findInvoiceNumberNumerator(invoiceGroup, owner))
                .orElse(null);
    }

    @Override
    public PropertyOwnerNumeratorBuilder builder() {
        return new PropertyOwnerNumeratorBuilder()
                .setPrereq((f, ec) -> {
                    final InvoiceGroup invoiceGroup = f.objectFor(invoiceGroup_d, ec);
                    f.setInvoiceGroup(invoiceGroup);
                })
                .setPrereq((f,ec) -> f.setOwner(f.objectFor(propertyAndUnitsAndOwnerAndManager_d.getOwner_d(), ec)))
                ;
    }
}
