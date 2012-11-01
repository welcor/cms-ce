package com.enonic.cms.core.xslt.portal;

import java.util.Map;

import javax.xml.transform.Transformer;

import net.sf.saxon.Controller;

import com.enonic.cms.core.xslt.base.SaxonXsltProcessor;

final class PortalXsltProcessorImpl
    extends SaxonXsltProcessor
    implements PortalXsltProcessor
{
    public PortalXsltProcessorImpl( final Transformer transformer )
    {
        super( transformer );
    }

    @Override
    public Map<String, String> getCustomParameterTypes()
    {
        final Controller controller = getController();
        return ParamTypeExtractor.extract( controller );
    }
}
