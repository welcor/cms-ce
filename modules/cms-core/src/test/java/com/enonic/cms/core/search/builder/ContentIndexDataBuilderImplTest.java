package com.enonic.cms.core.search.builder;

import java.util.Calendar;
import java.util.Date;

import org.elasticsearch.common.jackson.JsonParser;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContentParser;
import org.elasticsearch.common.xcontent.support.XContentMapConverter;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.ImagesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DateDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImageDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
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

    private Element standardConfigEl;

    private Document standardConfigDoc;

    private ImageDataEntryConfig imagesConfig = new ImageDataEntryConfig( "myImages", false, "My images", "contentdata/myimages" );

    private TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" );

    private DateDataEntryConfig dateConfig = new DateDataEntryConfig( "myDate", false, "My date", "contentdata/mydate" );

    private ContentTypeConfig config;

    @Before
    public void setUp()
        throws Exception
    {
        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );
        standardConfigXml.append( "         <title name=\"myTitle\"/>" );
        standardConfigXml.append( "         <block name=\"TestBlock1\">" );
        standardConfigXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "             <input name=\"myDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>My date</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "             <input name=\"myImages\" type=\"images\">" );
        standardConfigXml.append( "                 <display>My images</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimages</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );

        standardConfigDoc = JDOMUtil.parseDocument( standardConfigXml.toString() );
        standardConfigEl = standardConfigDoc.getRootElement();

        config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );


    }

    @Test
    public void testMetadata()
        throws Exception
    {
        final Date now = Calendar.getInstance().getTime();

        ContentEntity content = createTestContent( now );

        ContentIndexDataBuilderSpecification spec = ContentIndexDataBuilderSpecification.createBuildAllConfig();

        ContentIndexData indexData = indexDataBuilder.build( content, spec );

        ContentBuilderTestMetaDataHolder metadata = ContentBuilderTestMetaDataHolder.createMetaDataHolder( indexData.getMetadataJson() );

        assertEquals( metadata.key, 1.0 );
        assertEquals( metadata.title, CONTENT_TITLE );
        assertEquals( metadata.status, new Integer( 2 ) );
    }


    private ContentEntity createTestContent( Date now )
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();
        contentVersion.setTitle( "testtitle" );
        contentVersion.setStatus( ContentStatus.APPROVED );

        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( 1 ) );
        content.setName( "testcontentname" );
        content.setMainVersion( contentVersion );
        CategoryEntity cat = new CategoryEntity();

        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );
        contentType.setData( standardConfigDoc );

        cat.setContentType( contentType );

        content.setCategory( cat );
        content.setAvailableFrom( now );
        content.setCreatedAt( now );

        CustomContentData contentData = new CustomContentData( config );
        contentData.add( new TextDataEntry( titleConfig, "contentDataTestTitle" ) );
        contentData.add( new DateDataEntry( dateConfig, new DateTime( 2009, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        contentData.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 1 ) ) ) );

        contentVersion.setContentData( contentData );
        return content;
    }
}
