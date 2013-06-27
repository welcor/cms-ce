/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

public class ContentIndexDataAccessRightsBuilderTest
    extends ContentIndexDataTestBase
{
    private ContentIndexDataAccessRightsFactory accessRightsBuilder = new ContentIndexDataAccessRightsFactory();

    @Test
    public void testContentAccessRightsFilter()
    {
        List<ContentAccessEntity> contentAccessRights = Lists.newArrayList();
        ContentAccessEntity accessRight1 = createContentAccessEntity( "c", "g1", true, true, true );
        ContentAccessEntity accessRight2 = createContentAccessEntity( "c", "g2", true, false, true );
        ContentAccessEntity accessRight3 = createContentAccessEntity( "c", "g3", true, true, false );
        ContentAccessEntity accessRight4 = createContentAccessEntity( "c", "g4", false, false, false );

        contentAccessRights.add( accessRight1 );
        contentAccessRights.add( accessRight2 );
        contentAccessRights.add( accessRight3 );
        contentAccessRights.add( accessRight4 );

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        accessRightsBuilder.create( contentIndexData, contentAccessRights, Collections.<GroupKey, CategoryAccessEntity>emptyMap() );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_ACCESS_READ_FIELDNAME, 3 );
        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_ACCESS_DELETE_FIELDNAME, 2 );
        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_ACCESS_UPDATE_FIELDNAME, 2 );
    }

    @Test
    public void testCategoryAccessRightsFilter()
        throws Exception
    {
        final List<ContentAccessEntity> contentAccessRights = Lists.newArrayList();
        ContentAccessEntity contentAccessRights1 = createContentAccessEntity( "c", "g1", false, false, false );
        ContentAccessEntity contentAccessRights2 = createContentAccessEntity( "c", "g2", false, false, false );
        ContentAccessEntity contentAccessRights3 = createContentAccessEntity( "c", "g3", false, false, false );
        ContentAccessEntity contentAccessRights4 = createContentAccessEntity( "c", "g4", false, false, false );

        contentAccessRights.add( contentAccessRights1 );
        contentAccessRights.add( contentAccessRights2 );
        contentAccessRights.add( contentAccessRights3 );
        contentAccessRights.add( contentAccessRights4 );

        final Map<GroupKey, CategoryAccessEntity> categoryAccessRights = Maps.newHashMap();

        CategoryAccessEntity catAccessAdmin = createCategoryAccessEntity( "g1", false, false, false, false, true );
        CategoryAccessEntity catAccessRead = createCategoryAccessEntity( "g2", true, false, false, false, false );
        CategoryAccessEntity catAccessReadBrowsePublish = createCategoryAccessEntity( "g3", true, true, true, false, false );
        CategoryAccessEntity catAccessReadCreate = createCategoryAccessEntity( "g4", true, false, false, true, false );
        CategoryAccessEntity catAccessNoRights = createCategoryAccessEntity( "g5", false, false, false, false, false );
        CategoryAccessEntity catAccessReadPublish = createCategoryAccessEntity( "g6", true, false, true, false, false );

        final CategoryAccessEntity[] categoryAccessEntities =
            {catAccessAdmin, catAccessRead, catAccessReadBrowsePublish, catAccessReadCreate, catAccessNoRights, catAccessReadPublish};
        for ( CategoryAccessEntity cae : categoryAccessEntities )
        {
            categoryAccessRights.put( cae.getGroup().getGroupKey(), cae );
        }

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );

        accessRightsBuilder.create( contentIndexData, contentAccessRights, categoryAccessRights );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME,
                                              1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME, 3 );
        verifyElementExistsAndNumberOfValues( contentDataElements, IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME, 2 );
    }

    private ContentAccessEntity createContentAccessEntity( String contentKey, String groupKey, boolean readAccess, boolean updateAccess,
                                                           boolean deleteAccess )
    {
        final ContentAccessEntity accessRights = new ContentAccessEntity();
        accessRights.setKey( contentKey );
        accessRights.setDeleteAccess( deleteAccess );
        accessRights.setUpdateAccess( updateAccess );
        accessRights.setReadAccess( readAccess );

        final GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( groupKey ) );
        accessRights.setGroup( group );

        return accessRights;
    }

    private CategoryAccessEntity createCategoryAccessEntity( String groupKey, boolean readAccess, boolean adminBrowseAccess,
                                                             boolean publishAccess, boolean createAccess, boolean adminAccess )
    {
        final CategoryAccessEntity accessRights = new CategoryAccessEntity();
        accessRights.setReadAccess( readAccess );
        accessRights.setCreateAccess( createAccess );
        accessRights.setAdminAccess( adminAccess );
        accessRights.setAdminBrowseAccess( adminBrowseAccess );
        accessRights.setPublishAccess( publishAccess );

        final GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( groupKey ) );
        accessRights.setGroup( group );
        return accessRights;
    }

}
