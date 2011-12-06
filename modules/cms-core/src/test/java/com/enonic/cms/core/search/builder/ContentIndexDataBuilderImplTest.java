package com.enonic.cms.core.search.builder;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStatus;

import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.ContentTestDataBuilder;
import com.enonic.cms.core.search.index.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 11:54 AM
 */
public class ContentIndexDataBuilderImplTest
    extends TestCase
{

    protected static final String CONTENT_TITLE = "contentdatatesttitle";

    ContentIndexDataBuilder indexDataBuilder = new ContentIndexDataBuilderImpl();

    byte[] binary1 = "This is binary1".getBytes();


    public String createContentXML()
        throws Exception
    {
        StringBuffer configXML = new StringBuffer();
        configXML.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXML.append( "     <form>" );
        configXML.append( "         <title name=\"myTitle\"/>" );
        configXML.append( "         <block name=\"TestBlock1\">" );
        configXML.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        configXML.append( "                 <display>My title</display>" );
        configXML.append( "                 <xpath>contentdata/mytitle</xpath>" );
        configXML.append( "             </input>" );
        configXML.append( "             <input name=\"myDate\" type=\"date\">" );
        configXML.append( "                 <display>My date</display>" );
        configXML.append( "                 <xpath>contentdata/mydate</xpath>" );
        configXML.append( "             </input>" );
        configXML.append( "             <input name=\"myBinary\" type=\"uploadfile\">" );
        configXML.append( "                 <display>My Binary</display>" );
        configXML.append( "                 <xpath>contentdata/myBinary</xpath>" );
        configXML.append( "             </input>" );
        configXML.append( "         </block>" );
        configXML.append( "     </form>" );
        configXML.append( "</config>" );

        return configXML.toString();
    }

    @Test
    public void testMetadata()
        throws Exception
    {

        ContentEntity content = createTestContent();

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createMetadataConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        ContentBuilderTestMetaDataHolder metadata = ContentBuilderTestMetaDataHolder.createMetaDataHolder( indexData.getMetadataJson() );

        assertEquals( metadata.key, 1.0 );
        assertEquals( metadata.title, CONTENT_TITLE );
        assertEquals( metadata.status, new Integer( 2 ) );
        //TODO: Test all meta-fields
    }

    @Test
    public void testCustomData()
        throws Exception
    {

    }


    private ContentEntity createTestContent()
        throws Exception
    {
        ContentTestDataBuilder contentBuilder;
        contentBuilder = new ContentTestDataBuilder();
        contentBuilder.buildConfig( createContentXML() );

        Map<String, Object> customDataMap = new HashMap<String, Object>();
        customDataMap.put( "myTitle", "contentdatatesttitle" );
        customDataMap.put( "myDate", new DateTime( 2009, 1, 1, 1, 1, 1, 1 ).toDate() );
        customDataMap.put( "myBinary", "%0" );

        ContentEntity content = contentBuilder.createContent( 1, "testcontent" )
            .addMainVersion( 1, ContentStatus.APPROVED )
            .addCustomContent( customDataMap )
            .addBinaryData( 1, "ACABACAB" )
            .build();

        return content;
    }


}
