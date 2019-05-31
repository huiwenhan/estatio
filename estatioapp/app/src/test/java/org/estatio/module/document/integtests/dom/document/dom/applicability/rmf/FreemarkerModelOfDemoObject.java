package org.estatio.module.document.integtests.dom.document.dom.applicability.rmf;

import org.estatio.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.estatio.module.document.dom.impl.docs.DocumentTemplate;
import org.estatio.module.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;

import lombok.Value;

public class FreemarkerModelOfDemoObject extends RendererModelFactoryAbstract<DemoObjectWithUrl> {

    public FreemarkerModelOfDemoObject() {
        super(DemoObjectWithUrl.class);
    }

    @Override protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final DemoObjectWithUrl demoObject) {
        return new DataModel(demoObject);
    }

    @Value
    public static class DataModel {
        DemoObjectWithUrl demoObject;
    }

}

