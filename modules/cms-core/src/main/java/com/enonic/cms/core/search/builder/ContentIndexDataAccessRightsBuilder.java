/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.security.group.GroupKey;


public class ContentIndexDataAccessRightsBuilder
    extends AbstractIndexDataBuilder
{

    public void build( final XContentBuilder result, final Collection<ContentAccessEntity> contentAccessRights )
        throws Exception
    {

        if ( contentAccessRights == null || contentAccessRights.isEmpty() )
        {
            return;
        }

        final Set<String> readAccess = Sets.newTreeSet();
        final Set<String> deleteAccess = Sets.newTreeSet();
        final Set<String> updateAccess = Sets.newTreeSet();

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

        addStringSet( CONTENT_ACCESS_READ_FIELDNAME, readAccess, result, false, false );
        addStringSet( CONTENT_ACCESS_UPDATE_FIELDNAME, updateAccess, result, false, false );
        addStringSet( CONTENT_ACCESS_DELETE_FIELDNAME, deleteAccess, result, false, false );
    }
}

