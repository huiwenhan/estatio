package org.estatio.module.document.dom.spi;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.document.dom.impl.docs.DocumentNature;
import org.estatio.module.document.dom.impl.renderers.Renderer;
import org.estatio.module.document.dom.services.ClassNameViewModel;

public interface RendererClassNameService {

    @Programmatic
    public List<ClassNameViewModel> renderClassNamesFor(
            final DocumentNature inputNature,
            final DocumentNature outputNature);

    @Programmatic
    <C extends Renderer> Class<C> asClass(final String className);

}
