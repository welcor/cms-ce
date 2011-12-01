package com.enonic.cms.core.search;


import java.util.Collection;
import java.util.Set;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public final class ContentSearchQuery
{
    private String query;

    private int from;

    private int count;

    private Set<GroupKey> groupFilter;

    private Collection<CategoryKey> categoryFilter;

    private Set<ContentTypeKey> contentTypeFilter;

    private Set<MenuItemKey> sectionFilter;

    private ContentHitFilter hitFilter;

    private AttachmentFilters attachmentFilters;

    public String getQuery()
    {
        return this.query != null ? this.query : "";
    }

    public void setQuery( String value )
    {
        this.query = value;
    }

    public int getFrom()
    {
        return this.from;
    }

    public void setFrom( int from )
    {
        this.from = from;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount( int count )
    {
        this.count = count;
    }

    public Set<GroupKey> getGroupFilter()
    {
        return this.groupFilter;
    }

    public Collection<CategoryKey> getCategoryFilter()
    {
        return this.categoryFilter;
    }

    public Set<ContentTypeKey> getContentTypeFilter()
    {
        return this.contentTypeFilter;
    }

    public Set<MenuItemKey> getSectionFilter()
    {
        return this.sectionFilter;
    }

    // Should not be indexed
    public void setGroupFilter( Set<GroupKey> groupFilter )
    {
        this.groupFilter = groupFilter;
    }

    public void setCategoryFilter( Collection<CategoryKey> categoryFilter )
    {
        this.categoryFilter = categoryFilter;
    }

    public void setContentTypeFilter( Set<ContentTypeKey> contentTypeFilter )
    {
        this.contentTypeFilter = contentTypeFilter;
    }

    // Should not be indexed
    public void setSectionFilter( Set<MenuItemKey> sectionFilter )
    {
        this.sectionFilter = sectionFilter;
    }

    public boolean hasGroupFilter()
    {
        return ( this.groupFilter != null ) && !this.groupFilter.isEmpty();
    }

    public boolean hasCategoryFilter()
    {
        return ( this.categoryFilter != null ) && !this.categoryFilter.isEmpty();
    }

    public boolean hasContentTypeFilter()
    {
        return ( this.contentTypeFilter != null ) && !this.contentTypeFilter.isEmpty();
    }

    public boolean hasSectionFilter()
    {
        return ( this.sectionFilter != null ) && !this.sectionFilter.isEmpty();
    }

    public ContentHitFilter getHitFilter()
    {
        return this.hitFilter != null ? this.hitFilter : ContentHitFilters.all();
    }

    public void setHitFilter( ContentHitFilter hitFilter )
    {
        this.hitFilter = hitFilter;
    }

    public AttachmentFilters getAttachmentFilters()
    {
        return attachmentFilters;
    }

    public void setAttachmentFilters( AttachmentFilters attachmentFilters )
    {
        this.attachmentFilters = attachmentFilters;
    }

    public boolean hasAttachmentFilter()
    {
        return this.attachmentFilters != null && !this.attachmentFilters.filters.isEmpty();
    }


}
