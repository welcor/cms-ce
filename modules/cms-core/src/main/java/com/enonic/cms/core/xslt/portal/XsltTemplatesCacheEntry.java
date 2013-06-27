/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import com.google.common.collect.Sets;

import com.enonic.cms.core.resource.FileResourceName;

final class XsltTemplatesCacheEntry
    implements Templates
{
    private final FileResourceName name;

    private final Templates templates;

    private final Set<FileResourceName> resourceSet;

    private final long timestamp;

    private long lastValidated;

    public XsltTemplatesCacheEntry( final FileResourceName name, final Templates templates )
    {
        this.name = name;
        this.templates = templates;
        this.timestamp = System.currentTimeMillis();
        this.resourceSet = Sets.newHashSet();
        this.resourceSet.add( this.name );
        this.lastValidated = this.timestamp;
    }

    public FileResourceName getName()
    {
        return this.name;
    }

    public long getCompileTimestamp()
    {
        return this.timestamp;
    }

    public Set<FileResourceName> getResourceSet()
    {
        return this.resourceSet;
    }

    @Override
    public Transformer newTransformer()
        throws TransformerConfigurationException
    {
        return this.templates.newTransformer();
    }

    @Override
    public Properties getOutputProperties()
    {
        return this.templates.getOutputProperties();
    }

    public long getLastValidated()
    {
        return lastValidated;
    }

    public void setLastValidated( final long lastValidated )
    {
        this.lastValidated = lastValidated;
    }

    public void addIncludes( final Collection<FileResourceName> includes )
    {
        this.resourceSet.addAll( includes );
    }
}
