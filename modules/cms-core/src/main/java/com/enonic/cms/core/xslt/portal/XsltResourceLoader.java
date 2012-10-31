package com.enonic.cms.core.xslt.portal;

import javax.xml.transform.TransformerException;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceData;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.FileResourceService;
import com.enonic.cms.core.xslt.XsltResource;

final class XsltResourceLoader
{
    private final FileResourceService resourceService;

    public XsltResourceLoader( final FileResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public XsltResource load( final FileResourceName name )
        throws TransformerException
    {
        final FileResource resource = this.resourceService.getResource( name );

        if ( resource == null )
        {
            throw new TransformerException( "Failed to find resource [" + name.toString() + "]" );
        }

        final FileResourceData resourceData = this.resourceService.getResourceData( name );

        if ( resourceData == null )

        {
            throw new TransformerException( "Failed to find resource data for [" + name.toString() + "]" );
        }

        return new XsltResource( name.getPath(), resourceData.getAsString() );
    }
}
