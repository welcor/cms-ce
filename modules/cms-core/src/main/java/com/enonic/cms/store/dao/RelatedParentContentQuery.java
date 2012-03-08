package com.enonic.cms.store.dao;


import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.group.GroupKey;

public class RelatedParentContentQuery
{
    private List<ContentKey> contents;

    private Collection<GroupKey> securityFilter;

    private boolean includeOnlyMainVersions;

    private DateTime now;

    private boolean includeOfflineContent;


    public RelatedParentContentQuery contents( List<ContentKey> contents )
    {
        this.contents = contents;
        return this;
    }

    public RelatedParentContentQuery securityFilter( Collection<GroupKey> securityFilter )
    {
        this.securityFilter = securityFilter;
        return this;
    }

    public RelatedParentContentQuery now( DateTime now )
    {
        this.now = now;
        return this;
    }

    public RelatedParentContentQuery includeOnlyMainVersions( boolean value )
    {
        includeOnlyMainVersions = value;
        return this;
    }

    public RelatedParentContentQuery includeOfflineContent( boolean value )
    {
        includeOfflineContent = value;
        return this;
    }

    public List<ContentKey> getContents()
    {
        return contents;
    }

    public Collection<GroupKey> getSecurityFilter()
    {
        return securityFilter;
    }

    public boolean isIncludeOnlyMainVersions()
    {
        return includeOnlyMainVersions;
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