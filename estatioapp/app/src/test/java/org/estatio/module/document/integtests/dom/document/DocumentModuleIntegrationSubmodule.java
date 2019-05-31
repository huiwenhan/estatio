package org.estatio.module.document.integtests.dom.document;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.document.DocumentModule;
import org.estatio.module.document.integtests.demo.DocumentModuleDemoDomSubmodule;
import org.estatio.module.document.integtests.dom.document.dom.paperclips.demowithurl.PaperclipForDemoObjectWithUrl;
import org.estatio.module.document.integtests.dom.document.dom.paperclips.other.PaperclipForOtherObject;

@XmlRootElement(name = "module")
public class DocumentModuleIntegrationSubmodule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new DocumentModule(),
                new DocumentModuleDemoDomSubmodule()
        );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {

            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(PaperclipForDemoObjectWithUrl.class);
                deleteFrom(PaperclipForOtherObject.class);
            }

        };
    }
}
