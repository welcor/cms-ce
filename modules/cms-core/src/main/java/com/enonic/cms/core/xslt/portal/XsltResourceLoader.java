package com.enonic.cms.core.xslt.portal;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.FileResourceService;
import com.enonic.cms.core.xslt.XsltResourceHelper;

final class XsltResourceLoader
{
    private final FileResourceService resourceService;

    public XsltResourceLoader( final FileResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public Source load( final FileResourceName name )
        throws TransformerException
    {
        final FileResource resource = this.resourceService.getResource( name );

        if ( resource == null )
        {
            throw new TransformerException( "Failed to find resource [" + name.toString() + "]" );
        }

        final InputStream resourceData = this.resourceService.getResourceStream( name, true );

        if ( resourceData == null )

        {
            throw new TransformerException( "Failed to find resource data for [" + name.toString() + "]" );
        }

        final StreamSource source = new StreamSource();
        source.setInputStream( resourceData );
        source.setSystemId( XsltResourceHelper.createUri( name.getPath() ) );
        return source;
    }
}
