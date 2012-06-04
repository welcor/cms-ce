package com.enonic.cms.core.search.builder;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.SimpleText;
import com.enonic.cms.core.content.index.UserDefinedField;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/2/12
 * Time: 10:24 AM
 */
public class ContentIndexDataCustomDataFactoryBuilderTest
    extends ContentIndexDataTestBase
{
    private ContentIndexDataCustomDataFactory customDataBuilder = new ContentIndexDataCustomDataFactory();

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

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentDataElements();

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

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentDataElements();

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

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test", 4 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test.number", 2 );
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

        final ContentIndexData contentIndexData = new ContentIndexData( new ContentKey( 1 ) );
        customDataBuilder.create( contentIndexData, userDefinedFields );

        final Set<ContentIndexDataElement> contentDataElements = contentIndexData.getContentDataElements();

        verifyElementExistsAndNumberOfValues( contentDataElements, "test", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test1", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test2", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test3.number", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test4.number", 1 );
        verifyElementExistsAndNumberOfValues( contentDataElements, "test5.number", 1 );
    }
}
