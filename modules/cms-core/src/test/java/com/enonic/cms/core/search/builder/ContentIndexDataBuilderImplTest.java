package com.enonic.cms.core.search.builder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexFieldSet;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 11:54 AM
 */
public class ContentIndexDataBuilderImplTest
    extends TestCase
{

    ContentIndexDataBuilder indexDataBuilder = new ContentIndexDataBuilderImpl();

    @Test
    public void testMetadata()
        throws Exception
    {
        ContentDocument content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        ContentBuilderTestMetaDataHolder metadata = ContentBuilderTestMetaDataHolder.createMetaDataHolder( indexData.getMetadataJson() );

        assertEquals( 1.0, metadata.key );
        assertEquals( "mytitle", metadata.title );
        assertEquals( new Integer( 2 ), metadata.status );
        assertEquals( "1", metadata.publishto );
        assertEquals( "2011-01-09T23:00:00.000Z", metadata.publishfrom );
        assertEquals( "2011-03-09T23:00:00.000Z", metadata.timestamp );

        //TODO: Test all meta-fields
    }

    @Test
    public void testCustomData()
        throws Exception
    {
    }

    private ContentDocument createTestContent()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2011, Calendar.JANUARY, 10 );

        ContentDocument content = new ContentDocument( new ContentKey( 1 ) );
        content.setCategoryKey( new CategoryKey( 2 ) );
        content.setCategoryName( "MyCategory" );
        content.setContentTypeKey( new ContentTypeKey( 3 ) );
        content.setContentTypeName( "MyContentType" );

        content.setCreated( date.getTime() );

        content.setModifierKey( "10" );
        content.setModifierName( "ModifierName" );
        content.setModifierQualifiedName( "ModifierQName" );

        content.setOwnerKey( "11" );
        content.setOwnerName( "OwnerName" );
        content.setOwnerQualifiedName( "OwnerQName" );

        content.setAssigneeKey( new UserKey( "12" ) );
        content.setAssigneeName( "AssigneeName" );
        content.setAssigneeQualifiedName( "AssigneeQName" );

        content.setAssignerKey( new UserKey( "14" ) );
        content.setAssignerName( "AssignerName" );
        content.setAssignerQualifiedName( "AssignerQName" );

        content.setPublishFrom( date.getTime() );

        date.add( Calendar.MONTH, 1 );
        content.setPublishTo( date.getTime() );

        date.add( Calendar.MONTH, 1 );
        content.setAssignmentDueDate( date.getTime() );

        content.setTimestamp( date.getTime() );

        content.setModified( date.getTime() );

        content.setTitle( "MyTitle" );
        content.setStatus( 2 );
        content.setPriority( 1 );

        //is it enaught for locations? how to set?
        content.setContentLocations( new ContentLocations( new ContentEntity() ) );

        content.addUserDefinedField( "data/person/age", "38" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 38" );

        content.addUserDefinedField( "data/person/age", "28" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 28" );

        content.addUserDefinedField( "data/person/age", "10" );
        content.addUserDefinedField( "data/person/gender", "male" );
        content.addUserDefinedField( "data/person/description", "description 10" );

        int numberOfRowsExpected = 10;
        content.setBinaryExtractedText( new BigText( createStringFillingXRows( numberOfRowsExpected ) ) );

        return content;
    }

    private String createStringFillingXRows( int numberOfRows )
    {
        return createRandomTextOfSize( ContentIndexFieldSet.SPLIT_TRESHOLD * numberOfRows - 5 );
    }

    private String createRandomTextOfSize( int size )
    {
        String str = new String( "ABCDEFGHIJKLMNOPQRSTUVWZYZabcdefghijklmnopqrstuvw " );
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int te = 0;
        for ( int i = 1; i <= size; i++ )
        {
            te = r.nextInt( str.length() - 1 );
            sb.append( str.charAt( te ) );
        }

        return sb.toString();
    }

}
