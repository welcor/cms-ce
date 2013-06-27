/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import com.google.common.collect.Sets;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.xslt.XsltResourceHelper;

final class XsltTrackingUriResolver
    implements URIResolver
{
    private final XsltResourceLoader resourceLoader;

    private final Set<FileResourceName> includeSet;

    public XsltTrackingUriResolver( final XsltResourceLoader resourceLoader )
    {
        this.resourceLoader = resourceLoader;
        this.includeSet = Sets.newHashSet();
    }

    public Set<FileResourceName> getIncludes()
    {
        return this.includeSet;
    }

    @Override
    public Source resolve( final String href, final String base )
        throws TransformerException
    {
        final FileResourceName name = new FileResourceName( XsltResourceHelper.resolveRelativePath( href, base ) );
        final Source source = this.resourceLoader.load( name );

        this.includeSet.add( name );
        return source;
    }
}
