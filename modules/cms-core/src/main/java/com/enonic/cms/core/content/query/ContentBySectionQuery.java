/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class ContentBySectionQuery
    extends AbstractContentQuery
{
    private Collection<MenuItemKey> menuItemKeys;

    private SectionFilterStatus sectionFilterStatus = SectionFilterStatus.APPROVED_ONLY;

    private boolean searchInAllSections = false;

    private String facets;

    public Collection<MenuItemKey> getMenuItemKeys()
    {
        return menuItemKeys;
    }

    /**
     * @param menuItemKeys The menus to search for content.
     */
    public void setMenuItemKeys( Collection<MenuItemKey> menuItemKeys )
    {
        if ( menuItemKeys == null )
        {
            throw new IllegalArgumentException( "menuItemKeys cannot be NULL." );
        }
        this.menuItemKeys = menuItemKeys;
        this.searchInAllSections = false;
    }

    public ContentIndexQuery createAndSetupContentQuery( Collection<MenuItemEntity> sections, Collection<GroupKey> securityFilter )
    {
        MenuItemKey orderBySection = null;

        // Apply default sorting if no order set and the one given section is not ordered
        if ( StringUtils.isEmpty( this.getOrderBy() ) )
        {
            final MenuItemEntity section = sections.size() == 1 ? sections.iterator().next() : null;
            if ( ( section != null ) && section.isOrderedSection() )
            {
                orderBySection = section.getKey();
            }
            else
            {
                this.setOrderBy( "@timestamp DESC" );
            }
        }

        ContentIndexQuery query = new ContentIndexQuery( this.getQuery(), this.getOrderBy() );
        query.setOrderBySection( orderBySection );
        if ( this.useContentTypeFilter() )
        {
            query.setContentTypeFilter( this.getContentTypeFilter() );
        }
        if ( isSearchInAllSections() )
        {
            query.setSectionFilter( null, sectionFilterStatus );
        }
        else
        {
            query.setSectionFilter( sections, sectionFilterStatus );
        }
        query.setSecurityFilter( securityFilter );
        query.setIndex( this.getIndex() );
        query.setCount( this.getCount() );
        query.setCategoryAccessTypeFilter( getCategoryAccessTypeFilter(), getCategoryAccessTypeFilterPolicy() );
        query.setFacets( facets );
        checkAndApplyPublishedOnlyFilter( query );

        return query;
    }

    public void setSectionFilterStatus( SectionFilterStatus sectionFilterStatus )
    {
        this.sectionFilterStatus = sectionFilterStatus;
    }

    public boolean isSearchInAllSections()
    {
        return searchInAllSections;
    }

    public void setSearchInAllSections()
    {
        if ( menuItemKeys != null )
        {
            throw new IllegalStateException( "Searching in all sections is not possible when menuItemKeys have been set." );
        }
        searchInAllSections = true;
    }

    public boolean hasSectionFilter()
    {
        return ( menuItemKeys != null && menuItemKeys.size() > 0 ) || searchInAllSections;
    }

    public void setFacets( final String facets )
    {
        this.facets = facets;
    }

    @Override
    public void validate()
    {
        super.validate();

        if ( !hasSectionFilter() )
        {
            throw new InvalidContentBySectionQueryException( "Required section filter missing" );
        }
    }
}
