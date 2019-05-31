package org.estatio.module.document.dom.spi;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.document.dom.services.ClassNameViewModel;

public interface RendererModelFactoryClassNameService {

    @Programmatic
    public List<ClassNameViewModel> rendererModelFactoryClassNames();

}
