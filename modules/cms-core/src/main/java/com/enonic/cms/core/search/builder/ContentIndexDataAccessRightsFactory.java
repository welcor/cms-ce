/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.security.group.GroupKey;


public class ContentIndexDataAccessRightsFactory
    extends AbstractIndexDataFactory
{

    public void build( final XContentBuilder result, final Collection<ContentAccessEntity> contentAccessRights,
                       Map<GroupKey, CategoryAccessEntity> categoryAccessRights )
        throws Exception
    {

        if ( contentAccessRights == null || contentAccessRights.isEmpty() )
        {
            return;
        }

        final Set<String> readAccess = Sets.newTreeSet();
        final Set<String> deleteAccess = Sets.newTreeSet();
        final Set<String> updateAccess = Sets.newTreeSet();
        final Set<String> browseAccess = Sets.newTreeSet();
        final Set<String> approveAccess = Sets.newTreeSet();
        final Set<String> administrateAccess = Sets.newTreeSet();

        for ( final ContentAccessEntity contentAccess : contentAccessRights )
        {
            final GroupKey group = contentAccess.getGroup().getGroupKey();
            final String groupKey = group.toString();

            if ( contentAccess.isReadAccess() )
            {
                readAccess.add( groupKey );
            }
            if ( contentAccess.isUpdateAccess() )
            {
                updateAccess.add( groupKey );
            }
            if ( contentAccess.isDeleteAccess() )
            {
                deleteAccess.add( groupKey );
            }
        }

        for ( GroupKey categoryAccessGroup : categoryAccessRights.keySet() )
        {
            CategoryAccessEntity categoryAccess = categoryAccessRights.get( categoryAccessGroup );
            final String groupKey = categoryAccessGroup.toString();
            if ( categoryAccess.isReadAccess() )
            {
                // if a user has read access in a category, then the user will have read access to the content in the category
                readAccess.add( groupKey );
            }
            if ( categoryAccess.isAdminBrowseAccess() )
            {
                browseAccess.add( groupKey );
            }
            if ( categoryAccess.isPublishAccess() )
            {
                approveAccess.add( groupKey );
            }
            if ( categoryAccess.isAdminAccess() )
            {
                administrateAccess.add( groupKey );
            }
        }

        addStringSet( CONTENT_ACCESS_READ_FIELDNAME, readAccess, result, false, false );
        addStringSet( CONTENT_ACCESS_UPDATE_FIELDNAME, updateAccess, result, false, false );
        addStringSet( CONTENT_ACCESS_DELETE_FIELDNAME, deleteAccess, result, false, false );
        addStringSet( CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME, browseAccess, result, false, false );
        addStringSet( CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME, approveAccess, result, false, false );
        addStringSet( CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME, administrateAccess, result, false, false );
    }
}

