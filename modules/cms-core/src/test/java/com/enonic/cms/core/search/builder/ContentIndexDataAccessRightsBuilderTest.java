package com.enonic.cms.core.search.builder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.search.builder.indexdata.ContentIndexData;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ContentIndexDataAccessRightsBuilderTest
{

    private ContentIndexDataAccessRightsFactory accessRightsBuilder = new ContentIndexDataAccessRightsFactory();


    @Test
    public void testContentAccessRightsFilter()
        throws Exception
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
        final String jsonString = contentIndexData.getContentDataAsJsonString();

        JSONObject resultObject = new JSONObject( jsonString );

        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_ACCESS_READ_FIELDNAME ) );
        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_ACCESS_DELETE_FIELDNAME ) );
        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_ACCESS_UPDATE_FIELDNAME ) );

        JSONArray readAccessValues = resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_ACCESS_READ_FIELDNAME );
        JSONArray deleteAccessValues = resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_ACCESS_DELETE_FIELDNAME );
        JSONArray updateAccessValues = resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_ACCESS_UPDATE_FIELDNAME );

        assertEquals( readAccessValues.length(), 3 );
        assertEquals( deleteAccessValues.length(), 2 );
        assertEquals( updateAccessValues.length(), 2 );

        assertTrue( containsValue( readAccessValues, "g1" ) );
        assertTrue( containsValue( deleteAccessValues, "g1" ) );
        assertTrue( containsValue( updateAccessValues, "g1" ) );

        assertTrue( containsValue( readAccessValues, "g2" ) );
        assertTrue( containsValue( deleteAccessValues, "g2" ) );

        assertTrue( containsValue( readAccessValues, "g3" ) );
        assertTrue( containsValue( updateAccessValues, "g3" ) );

        assertFalse( containsValue( readAccessValues, "g4" ) );
        assertFalse( containsValue( deleteAccessValues, "g4" ) );
        assertFalse( containsValue( updateAccessValues, "g4" ) );
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
        CategoryAccessEntity catAccessReadBrowsePublish = createCategoryAccessEntity( "g3", true, true, true, false,
                                                                                      false );
        CategoryAccessEntity catAccessReadCreate = createCategoryAccessEntity( "g4", true, false, false, true, false );
        CategoryAccessEntity catAccessNoRights = createCategoryAccessEntity( "g5", false, false, false, false, false );
        CategoryAccessEntity catAccessReadPublish = createCategoryAccessEntity( "g6", true, false, true, false, false );

        final CategoryAccessEntity[] categoryAccessEntities =
                {catAccessAdmin, catAccessRead, catAccessReadBrowsePublish, catAccessReadCreate,
                        catAccessNoRights, catAccessReadPublish};
        for ( CategoryAccessEntity cae : categoryAccessEntities )
        {
            categoryAccessRights.put( cae.getGroup().getGroupKey(), cae );
        }

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );

        accessRightsBuilder.create( contentIndexData, contentAccessRights, categoryAccessRights );
        final String jsonString = contentIndexData.getContentDataAsJsonString();

        final JSONObject resultObject = new JSONObject( jsonString );

        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME ) );
        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME ) );
        assertTrue( resultObject.has( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME ) );

        final JSONArray administrateAccessValues =
                resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_ADMINISTRATE_FIELDNAME );
        final JSONArray approveAccessValues =
                resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_APPROVE_FIELDNAME );
        final JSONArray browseAccessValues =
                resultObject.getJSONArray( IndexFieldNameConstants.CONTENT_CATEGORY_ACCESS_BROWSE_FIELDNAME );

        assertEquals( administrateAccessValues.length(), 1 );
        assertEquals( approveAccessValues.length(), 3 );
        assertEquals( browseAccessValues.length(), 2 );

        assertTrue( containsValue( administrateAccessValues, "g1" ) );
        assertTrue( containsValue( approveAccessValues, "g1" ) );
        assertTrue( containsValue( browseAccessValues, "g1" ) );

        assertFalse( containsValue( administrateAccessValues, "g2" ) );
        assertFalse( containsValue( approveAccessValues, "g2" ) );
        assertFalse( containsValue( browseAccessValues, "g2" ) );

        assertFalse( containsValue( administrateAccessValues, "g3" ) );
        assertTrue( containsValue( approveAccessValues, "g3" ) );
        assertTrue( containsValue( browseAccessValues, "g3" ) );

        assertFalse( containsValue( administrateAccessValues, "g4" ) );
        assertFalse( containsValue( approveAccessValues, "g4" ) );
        assertFalse( containsValue( browseAccessValues, "g4" ) );

        assertFalse( containsValue( administrateAccessValues, "g5" ) );
        assertFalse( containsValue( approveAccessValues, "g5" ) );
        assertFalse( containsValue( browseAccessValues, "g5" ) );

        assertFalse( containsValue( administrateAccessValues, "g6" ) );
        assertTrue( containsValue( approveAccessValues, "g6" ) );
        assertFalse( containsValue( browseAccessValues, "g6" ) );
    }

    private ContentAccessEntity createContentAccessEntity( String contentKey, String groupKey, boolean readAccess,
                                                           boolean updateAccess, boolean deleteAccess )
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

    private CategoryAccessEntity createCategoryAccessEntity( String groupKey, boolean readAccess,
                                                             boolean adminBrowseAccess, boolean publishAccess,
                                                             boolean createAccess, boolean adminAccess )
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

    private boolean containsValue( JSONArray valueArray, String stringValue )
        throws Exception
    {
        for ( int i = 0; i < valueArray.length(); i++ )
        {
            if ( valueArray.get( i ).equals( stringValue ) )
            {
                return true;
            }
        }

        return false;
    }

}
