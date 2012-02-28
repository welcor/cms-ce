package com.enonic.cms.core.search.builder;

import java.util.List;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ContentIndexDataAccessRightsBuilderTest
{

    private XContentBuilder result;

    private ContentIndexDataAccessRightsBuilder accessRightsBuilder = new ContentIndexDataAccessRightsBuilder();


    @Before
    public void setUp()
        throws Exception
    {
        result = XContentFactory.jsonBuilder();
    }


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

        result.startObject();
        accessRightsBuilder.build( result, contentAccessRights );
        result.endObject();

        final String jsonString = result.string();
        System.out.println( jsonString );
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
