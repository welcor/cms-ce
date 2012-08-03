/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.viewtransformer;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.rendering.StyleSheetURIResolver;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;

/**
 * May 13, 2009
 */
public abstract class AbstractXsltViewTransformer

{
    protected final static String XSLT_NS = "http://www.w3.org/1999/XSL/Transform";

    protected StyleSheetURIResolver styleSheetURIResolver;

    protected ResourceService resourceService;

    protected XsltProcessor createProcessor( ResourceKey styleSheetKey, XMLDocument xslt )
        throws XsltProcessorException
    {
        XsltResource resource = new XsltResource( styleSheetKey.toString(), xslt.getAsString() );
        XsltProcessorManager manager = XsltProcessorManagerAccessor.getProcessorManager();
        return manager.createCachedProcessor( resource, styleSheetURIResolver );
    }

    protected XsltProcessor createProcessor( ResourceKey styleSheetKey, XMLDocument xslt, boolean omitXmlDecl )
        throws XsltProcessorException
    {
        XsltProcessor processor;
        processor = createProcessor( styleSheetKey, xslt );
        processor.setOmitXmlDecl( omitXmlDecl );
        return processor;
    }


    protected void setup()
    {
        styleSheetURIResolver = new StyleSheetURIResolver( resourceService );
    }
}
