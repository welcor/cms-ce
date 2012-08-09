package com.enonic.cms.core.content;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.store.dao.GroupDao;

public class ContentACLSynchronizer
{
    private GroupDao groupDao;

    public ContentACLSynchronizer( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    public boolean synchronize( final ContentEntity content, Map<String, ContentAccessEntity> bluePrint )
    {
        final ContentACL contentACLBluePrint = new ContentACL();
        for ( Map.Entry<String, ContentAccessEntity> contentAccessEntry : bluePrint.entrySet() )
        {
            contentACLBluePrint.add( ContentAccessControl.create( contentAccessEntry.getValue() ) );
        }
        return synchronize( content, contentACLBluePrint );
    }

    public boolean synchronize( final ContentEntity content, final ContentACL bluePrint )
    {
        final boolean anyRemoved = remove( content, bluePrint );
        final boolean anyAddedOrModified = addOrModify( content, bluePrint );

        return anyRemoved || anyAddedOrModified;
    }

    private boolean remove( final ContentEntity content, final ContentACL bluePrint )
    {
        final List<ContentAccessEntity> accessRightsToRemove = new ArrayList<ContentAccessEntity>();

        // remove content access rights that is no longer there
        final Collection<ContentAccessEntity> existingAccessRights = content.getContentAccessRights();
        for ( ContentAccessEntity existingAccessRight : existingAccessRights )
        {
            boolean remove = !bluePrint.containsKey( existingAccessRight.getGroup().getGroupKey() );
            if ( remove )
            {
                accessRightsToRemove.add( existingAccessRight );
            }
        }
        for ( ContentAccessEntity accessRight : accessRightsToRemove )
        {
            content.removeContentAccessRightByGroup( accessRight.getGroup().getGroupKey() );
        }

        return accessRightsToRemove.size() > 0;
    }

    private boolean addOrModify( final ContentEntity content, final ContentACL bluePrint )
    {
        boolean modified = false;

        for ( ContentAccessControl givenContentAccess : bluePrint )
        {
            final ContentAccessEntity persistedContentAccess = content.getContentAccessRight( givenContentAccess.getGroup() );
            if ( persistedContentAccess != null )
            {
                boolean modifiedByUpdate = persistedContentAccess.overwriteRightsFrom( givenContentAccess );
                if ( modifiedByUpdate )
                {
                    modified = true;
                }
            }
            else
            {
                final ContentAccessEntity newContentAccess = new ContentAccessEntity();
                final GroupEntity group = groupDao.findByKey( givenContentAccess.getGroup() );
                newContentAccess.setGroup( group );
                newContentAccess.overwriteRightsFrom( givenContentAccess );
                newContentAccess.setContent( content );
                content.addContentAccessRight( newContentAccess );
                modified = true;
            }
        }

        return modified;
    }
}
