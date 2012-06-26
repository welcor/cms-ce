package com.enonic.cms.core.search.builder;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.UserDefinedField;
import com.enonic.cms.core.search.query.SimpleText;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 10:24 AM
 */
public class ContentIndexDataCustomDataFactoryBuilderTest
    extends ContentIndexDataTestBase
{
    private final ContentIndexDataCustomDataFactory customDataBuilder = new ContentIndexDataCustomDataFactory();

    @Test
    public void testNumericValuesOnlyToNumberField()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "3" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "4" ) ) );

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test.number", 4 );
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

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test", 5 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test.number", 2 );
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
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "2" ) ) );

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test", 4 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test.number", 2 );
    }

    @Test
    public void testSingleValues()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test", new SimpleText( "test" ) ) );
        userDefinedFields.add( new UserDefinedField( "test2", new SimpleText( "2" ) ) );


        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test", 1 );
        verifyElementDoesNotExist( contentDataElements, "test.number" );
        verifyElementDoesNotExist( contentDataElements, "test.date" );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test2", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test2.number", 1 );
        verifyElementDoesNotExist( contentDataElements, "test5.date" );
    }


    @Test
    public void testAllUserdataValue()
        throws Exception
    {
        List<UserDefinedField> userDefinedFields = Lists.newArrayList();

        userDefinedFields.add( new UserDefinedField( "test1", new SimpleText( "1" ) ) );
        userDefinedFields.add( new UserDefinedField( "test2", new SimpleText( "2" ) ) );
        userDefinedFields.add( new UserDefinedField( "test3", new SimpleText( "3" ) ) );
        userDefinedFields.add( new UserDefinedField( "test4", new SimpleText( "4" ) ) );

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentIndexDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test1.orderby", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "_all_userdata", 4 );
        verifyElementDoesNotExist( contentDataElements, "_all_userdata.orderby" );
    }


}
