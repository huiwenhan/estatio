package org.estatio.module.docrendering.xdocgoten.dom.impl;

import org.estatio.module.docrendering.gotenberg.dom.impl.RendererForGotenbergDocxToPdfAbstract;
import org.estatio.module.docrendering.xdocreport.dom.impl.RendererForXDocReportToDocx;

public class RendererForXDocReportToDocxThenGotenbergToPdf extends RendererForGotenbergDocxToPdfAbstract {

    public RendererForXDocReportToDocxThenGotenbergToPdf() {
        super(new RendererForXDocReportToDocx());
    }

}
