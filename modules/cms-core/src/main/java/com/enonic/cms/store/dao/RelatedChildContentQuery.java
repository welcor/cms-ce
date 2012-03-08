package com.enonic.cms.store.dao;


import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.security.group.GroupKey;

public class RelatedChildContentQuery
{
    private List<ContentVersionKey> contentVersions;

    private Collection<GroupKey> securityFilter;

    private DateTime now;

    private boolean includeOfflineContent;

    public RelatedChildContentQuery contentVersions( List<ContentVersionKey> contentVersions )
    {
        this.contentVersions = contentVersions;
        return this;
    }

    public RelatedChildContentQuery securityFilter( Collection<GroupKey> securityFilter )
    {
        this.securityFilter = securityFilter;
        return this;
    }

    public RelatedChildContentQuery now( DateTime now )
    {
        this.now = now;
        return this;
    }

    public RelatedChildContentQuery includeOfflineContent( boolean value )
    {
        includeOfflineContent = value;
        return this;
    }

    public List<ContentVersionKey> getContentVersions()
    {
        return contentVersions;
    }

    public Collection<GroupKey> getSecurityFilter()
    {
        return securityFilter;
    }

    public DateTime getNow()
    {
        return now;
    }

    public boolean isIncludeOfflineContent()
    {
        return includeOfflineContent;
    }

    public boolean hasSecurityFilter()
    {
        return securityFilter != null && !securityFilter.isEmpty();
    }
}
