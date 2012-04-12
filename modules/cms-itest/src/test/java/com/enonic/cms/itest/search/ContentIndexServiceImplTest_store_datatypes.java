package com.enonic.cms.itest.search;

import java.util.Map;

import org.elasticsearch.search.SearchHitField;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class ContentIndexServiceImplTest_store_datatypes
    extends ContentIndexServiceTestBase
{
    @Test
    public void testStoreUserdefinedFields()
    {
        setUpStandardTestValues();

        //printAllIndexContent();

        final ContentKey contentKey = new ContentKey( 1322 );
        final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( contentKey );

        assertNotNull( fieldMapForId.get( "data_person_age" ) );
        final SearchHitField searchHitField = fieldMapForId.get( "data_person_age.number" );
        assertNotNull( searchHitField );
        Assert.assertEquals( 2, searchHitField.values().size() );

        final SearchHitField data_person_gender = fieldMapForId.get( "data_person_gender" );
        assertNotNull( data_person_gender );
        Assert.assertEquals( 2, data_person_gender.values().size() );
        assertNull( fieldMapForId.get( "data_person_gender.number" ) );

        final SearchHitField data_person_description = fieldMapForId.get( "data_person_description" );
        assertNotNull( data_person_description );
        Assert.assertEquals( 2, data_person_description.values().size() );
        assertNull( fieldMapForId.get( "data_person_description.number" ) );

        final SearchHitField data_person_birthdate = fieldMapForId.get( "data_person_birthdate" );
        assertNotNull( data_person_birthdate );
        Assert.assertEquals( 2, data_person_birthdate.values().size() );
        final SearchHitField object = fieldMapForId.get( "data_person_birthdate.date" );
        assertNotNull( object );

    }


}
