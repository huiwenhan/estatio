package org.estatio.module.document.dom.contracttests.with;

import org.incode.module.base.dom.with.WithCodeUnique;
import org.incode.module.base.dom.with.WithFieldUniqueContractTestAllAbstract;

public class WithCodeUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation extends
        WithFieldUniqueContractTestAllAbstract<WithCodeUnique> {

    public WithCodeUniqueContractForIncodeModuleTest_hasJdoUniqueIndexAnnotation() {
        super("org.estatio.module.document", "code", WithCodeUnique.class);
    }

}
