package com.enonic.cms.core.xslt.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.store.resource.FileResourceEvent;
import com.enonic.cms.store.resource.FileResourceListener;

@Component
public final class TemplatesXsltCacheInvalidator
    implements FileResourceListener
{
    private TemplatesXsltCache cache;

    @Override
    public void resourceChanged( final FileResourceEvent event )
    {
        this.cache.clear();
    }

    @Autowired
    public void setCache( final TemplatesXsltCache cache )
    {
        this.cache = cache;
    }
}
