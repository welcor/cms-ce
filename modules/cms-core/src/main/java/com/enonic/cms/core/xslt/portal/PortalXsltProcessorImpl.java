package com.enonic.cms.core.xslt.portal;

import javax.xml.transform.Transformer;

import com.enonic.cms.core.xslt.base.SaxonXsltProcessor;

final class PortalXsltProcessorImpl
    extends SaxonXsltProcessor
    implements PortalXsltProcessor
{
    public PortalXsltProcessorImpl( final Transformer transformer )
    {
        super( transformer );
    }
}
