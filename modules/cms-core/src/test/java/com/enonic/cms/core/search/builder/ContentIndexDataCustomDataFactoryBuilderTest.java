package com.enonic.cms.core.search.builder;

import java.util.List;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 10:24 AM
 */
public class ContentIndexDataCustomDataFactoryBuilderTest
{

    private XContentBuilder result;

    private ContentIndexDataCustomDataFactory customDataBuilder = new ContentIndexDataCustomDataFactory();


    @Before
    public void setUp()
        throws Exception
    {
        result = XContentFactory.jsonBuilder();
    }


    @Test
    public void testNumericValuesOnlyToNumberField()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "3" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "4" ) ) );

        final String jsonString = result.string();

        JSONObject resultObject = new JSONObject( jsonString );

        assertEquals( true, resultObject.has( "test" ) );

        assertTrue( resultObject.has( "test.number" ) );
        JSONArray testNumericValues = resultObject.getJSONArray( "test.number" );

        assertTrue( testNumericValues.length() == 4 );

        assertTrue( containsValue( testNumericValues, 1.0 ) );
        assertTrue( containsValue( testNumericValues, 2.0 ) );
        assertTrue( containsValue( testNumericValues, 3.0 ) );
        assertTrue( containsValue( testNumericValues, 4.0 ) );
    }


    @Test
    public void testBothStringAndNumericValuesToNumberField()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test3" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "4" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "5" ) ) );

        final String jsonString = result.string();

        JSONObject resultObject = new JSONObject( jsonString );

        assertTrue( resultObject.has( "test" ) );
        JSONArray testStringValues = resultObject.getJSONArray( "test" );
        assertTrue( resultObject.has( "test.number" ) );
        JSONArray testNumericValues = resultObject.getJSONArray( "test.number" );

        assertEquals( 5, testStringValues.length() );
        assertEquals( 2, testNumericValues.length() );

        assertTrue( containsValue( testNumericValues, 4.0 ) );
        assertTrue( containsValue( testNumericValues, 5.0 ) );

    }


    @Test
    public void testOnlyDistinctValues()
        throws Exception
    {

        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test1" ) ) );

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "1" ) ) );
        ;
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );

        final String jsonString = result.string();

        JSONObject resultObject = new JSONObject( jsonString );

        assertTrue( resultObject.has( "test" ) );
        JSONArray testValueArray = resultObject.getJSONArray( "test" );
        assertEquals( 4, testValueArray.length() );
        containsValue( testValueArray, "test1" );
        containsValue( testValueArray, "test2" );
        containsValue( testValueArray, "1" );
        containsValue( testValueArray, "2" );

        assertTrue( resultObject.has( "test.number" ) );
        JSONArray testNumberValueArray = resultObject.getJSONArray( "test.number" );
        assertEquals( 2, testNumberValueArray.length() );
        containsValue( testNumberValueArray, 1.0 );
        containsValue( testNumberValueArray, 2.0 );
    }

    @Test
    public void testSingleValues()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test" ) ) );
        userDefinedFields.add( new UserDefinedField( "test1", new SimpleText( "test1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test2", new SimpleText( "test2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test3", new SimpleText( "3" ) ) );
        userDefinedFields.add( new UserDefinedField( "test4", new SimpleText( "4" ) ) );
        userDefinedFields.add( new UserDefinedField( "test5", new SimpleText( "5" ) ) );

        final String jsonString = result.string();

        JSONObject resultObject = new JSONObject( jsonString );

        assertTrue( resultObject.has( "test" ) );
        assertTrue( resultObject.has( "test1" ) );
        assertTrue( resultObject.has( "test2" ) );
        assertTrue( resultObject.has( "test3" ) );
        assertTrue( resultObject.has( "test4" ) );
        assertTrue( resultObject.has( "test5" ) );

    }

    public boolean containsValue( JSONArray valueArray, Double doubleValue )
        throws Exception
    {
        for ( int i = 0; i < valueArray.length(); i++ )
        {
            if ( valueArray.get( i ).equals( doubleValue ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue( JSONArray valueArray, String stringValue )
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
