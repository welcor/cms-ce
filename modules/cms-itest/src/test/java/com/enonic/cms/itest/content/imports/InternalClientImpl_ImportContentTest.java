package com.enonic.cms.itest.content.imports;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.model.ImportContentsParams;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;


public class InternalClientImpl_ImportContentTest
    extends AbstractSpringTest
{
    @Autowired
    protected HibernateTemplate hibernateTemplate;

    protected DomainFactory factory;

    @Autowired
    protected DomainFixture fixture;

    @Autowired
    protected ContentService contentService;

    @Autowired
    @Qualifier("localClient")
    protected InternalClient client;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    private GroupDao groupDao;

    @Before
    public void setUp()
        throws IOException
    {
        factory = fixture.getFactory();

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), null ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", null, "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );

        ImportJobFactory.setExecuteInOneTransaction( true );
    }

    @Test
    public void given_string_based_input_field_that_is_not_mapped_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myText", "text", "contentdata/myText", "Text", false );
        ctyconf.addInput( "myUrl", "url", "contentdata/myUrl", "URL", false );
        ctyconf.addInput( "myRadiobutton", "radiobutton", "contentdata/myRadiobutton", "Radiobutton", false );
        ctyconf.addInput( "myDropdown", "dropdown", "contentdata/myDropdown", "Dropdown", false );
        ctyconf.addInput( "myTextarea", "textarea", "contentdata/myTextarea", "Textarea", false );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (TextDataEntry) contentData.getEntry( "myText" ) ).getValue() );
        assertEquals( null, ( (UrlDataEntry) contentData.getEntry( "myUrl" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myRadiobutton" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myDropdown" ) ).getValue() );
        assertEquals( null, ( (TextAreaDataEntry) contentData.getEntry( "myTextarea" ) ).getValue() );
        assertEquals( null, ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_string_based_input_field_that_is_not_mapped_when_importing_from_csv_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myText", "text", "contentdata/myText", "Text", false );
        ctyconf.addInput( "myUrl", "url", "contentdata/myUrl", "URL", false );
        ctyconf.addInput( "myRadiobutton", "radiobutton", "contentdata/myRadiobutton", "Radiobutton", false );
        ctyconf.addInput( "myDropdown", "dropdown", "contentdata/myDropdown", "Dropdown", false );
        ctyconf.addInput( "myTextarea", "textarea", "contentdata/myTextarea", "Textarea", false );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (TextDataEntry) contentData.getEntry( "myText" ) ).getValue() );
        assertEquals( null, ( (UrlDataEntry) contentData.getEntry( "myUrl" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myRadiobutton" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myDropdown" ) ).getValue() );
        assertEquals( null, ( (TextAreaDataEntry) contentData.getEntry( "myTextarea" ) ).getValue() );
        assertEquals( null, ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_xml_input_field_that_is_not_mapped_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myXml", "xml", "contentdata/myXml", "XML", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (XmlDataEntry) contentData.getEntry( "myXml" ) ).getValue() );
    }

    @Test
    public void given_xml_input_field_that_is_not_mapped_when_importing_from_csv_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myXml", "xml", "contentdata/myXml", "XML", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (XmlDataEntry) contentData.getEntry( "myXml" ) ).getValue() );
    }

    @Test
    public void given_date_input_field_that_is_not_mapped_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myDate", "date", "contentdata/myDate", "Date", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (DateDataEntry) contentData.getEntry( "myDate" ) ).getValue() );
    }

    @Test
    public void given_date_input_field_that_is_not_mapped_when_importing_from_csv_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myDate", "date", "contentdata/myDate", "Date", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (DateDataEntry) contentData.getEntry( "myDate" ) ).getValue() );
    }

    @Test
    public void given_string_based_input_field_that_is_mapped_but_xpath_does_not_exist_in_source_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myText", "text", "contentdata/myText", "Text", false );
        ctyconf.addInput( "myUrl", "url", "contentdata/myUrl", "URL", false );
        ctyconf.addInput( "myRadiobutton", "radiobutton", "contentdata/myRadiobutton", "Radiobutton", false );
        ctyconf.addInput( "myDropdown", "dropdown", "contentdata/myDropdown", "Dropdown", false );
        ctyconf.addInput( "myTextarea", "textarea", "contentdata/myTextarea", "Textarea", false );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "text", "myText" );
        ctyconf.addImportMapping( "url", "myUrl" );
        ctyconf.addImportMapping( "radiobutton", "myRadiobutton" );
        ctyconf.addImportMapping( "dropdown", "myDropdown" );
        ctyconf.addImportMapping( "textarea", "myTextarea" );
        ctyconf.addImportMapping( "htmlarea", "myHtmlarea" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (TextDataEntry) contentData.getEntry( "myText" ) ).getValue() );
        assertEquals( null, ( (UrlDataEntry) contentData.getEntry( "myUrl" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myRadiobutton" ) ).getValue() );
        assertEquals( null, ( (SelectorDataEntry) contentData.getEntry( "myDropdown" ) ).getValue() );
        assertEquals( null, ( (TextAreaDataEntry) contentData.getEntry( "myTextarea" ) ).getValue() );
        assertEquals( null, ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_string_based_input_field_that_is_mapped_but_xpath_exist_in_source_but_has_empty_value_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "htmlarea", "myHtmlarea" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += " <htmlarea></htmlarea>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_xml_field_that_is_mapped_but_xpath_does_not_exist_in_source_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myXml", "xml", "contentdata/myXml", "XML", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "xml", "myXml" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (XmlDataEntry) contentData.getEntry( "myXml" ) ).getValue() );
    }

    @Test
    public void given_date_field_that_is_mapped_but_xpath_does_not_exist_in_source_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myDate", "date", "contentdata/myDate", "Date", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "date", "myDate" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (DateDataEntry) contentData.getEntry( "myDate" ) ).getValue() );
    }

    @Test
    public void given_string_based_input_field_that_is_mapped_and_xpath_returns_empty_string_when_importing_from_xml_then_value_of_field_is_empty()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myText", "text", "contentdata/myText", "Text", false );
        ctyconf.addInput( "myUrl", "url", "contentdata/myUrl", "URL", false );
        ctyconf.addInput( "myRadiobutton", "radiobutton", "contentdata/myRadiobutton", "Radiobutton", false );
        ctyconf.addInput( "myDropdown", "dropdown", "contentdata/myDropdown", "Dropdown", false );
        ctyconf.addInput( "myTextarea", "textarea", "contentdata/myTextarea", "Textarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "text", "myText" );
        ctyconf.addImportMapping( "url", "myUrl" );
        ctyconf.addImportMapping( "radiobutton", "myRadiobutton" );
        ctyconf.addImportMapping( "dropdown", "myDropdown" );
        ctyconf.addImportMapping( "textarea", "myTextarea" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += " <text/>";
        source += " <url/>";
        source += " <radiobutton/>";
        source += " <dropdown/>";
        source += " <textarea></textarea>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "myText" ) ).getValue() );
        assertEquals( "", ( (UrlDataEntry) contentData.getEntry( "myUrl" ) ).getValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "myRadiobutton" ) ).getValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "myDropdown" ) ).getValue() );
        assertEquals( "", ( (TextAreaDataEntry) contentData.getEntry( "myTextarea" ) ).getValue() );
    }

    @Test
    public void given_htmlarea_input_field_that_is_mapped_and_xpath_returns_empty_string_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "htmlarea", "myHtmlarea" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += " <htmlarea/>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_string_based_input_field_that_is_mapped_and_position_returns_empty_string_when_importing_from_csv_then_value_of_field_is_empty()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myText", "text", "contentdata/myText", "Text", false );
        ctyconf.addInput( "myUrl", "url", "contentdata/myUrl", "URL", false );
        ctyconf.addInput( "myRadiobutton", "radiobutton", "contentdata/myRadiobutton", "Radiobutton", false );
        ctyconf.addInput( "myDropdown", "dropdown", "contentdata/myDropdown", "Dropdown", false );
        ctyconf.addInput( "myTextarea", "textarea", "contentdata/myTextarea", "Textarea", false );
        ctyconf.addInput( "myHtmlarea", "htmlarea", "contentdata/myHtmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.addImportMapping( "2", "myText" );
        ctyconf.addImportMapping( "3", "myUrl" );
        ctyconf.addImportMapping( "4", "myRadiobutton" );
        ctyconf.addImportMapping( "5", "myDropdown" );
        ctyconf.addImportMapping( "6", "myTextarea" );
        ctyconf.addImportMapping( "7", "myHtmlarea" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;;;;;;;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "myText" ) ).getValue() );
        assertEquals( "", ( (UrlDataEntry) contentData.getEntry( "myUrl" ) ).getValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "myRadiobutton" ) ).getValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "myDropdown" ) ).getValue() );
        assertEquals( "", ( (TextAreaDataEntry) contentData.getEntry( "myTextarea" ) ).getValue() );
        assertEquals( "", ( (HtmlAreaDataEntry) contentData.getEntry( "myHtmlarea" ) ).getValue() );
    }

    @Test
    public void given_xml_field_that_is_mapped_and_xpath_returns_empty_string_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myXml", "xml", "contentdata/myXml", "XML", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "xml", "myXml" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += " <xml/>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (XmlDataEntry) contentData.getEntry( "myXml" ) ).getValue() );
    }

    @Test
    public void given_xml_field_that_is_mapped_and_position_returns_empty_string_when_importing_from_csv_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myXml", "xml", "contentdata/myXml", "XML", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.addImportMapping( "2", "myXml" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (XmlDataEntry) contentData.getEntry( "myXml" ) ).getValue() );
    }

    @Test
    public void given_date_field_that_is_mapped_and_xpath_returns_empty_string_when_importing_from_xml_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myDate", "date", "contentdata/myDate", "Date", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForXmlMode( "test-import", "/entries/entry", "0", null, null );
        ctyconf.addImportMapping( "@title", "myTitle" );
        ctyconf.addImportMapping( "date", "myDate" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "";
        source += "<entries>";
        source += "<entry title='entry1'>";
        source += " <date/>";
        source += "</entry>";
        source += "</entries>";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (DateDataEntry) contentData.getEntry( "myDate" ) ).getValue() );
    }

    @Test
    public void given_date_field_that_is_mapped_and_position_returns_empty_string_when_importing_from_csv_then_value_of_field_is_null()
    {
        // setup
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/myTitle", "Title", true );
        ctyconf.addInput( "myDate", "date", "contentdata/myDate", "Date", false );
        ctyconf.endBlock();
        ctyconf.startImportConfigForCSVMode( "test-import", ";", "0", null, null );
        ctyconf.addImportMapping( "1", "myTitle" );
        ctyconf.addImportMapping( "2", "myDate" );
        ctyconf.endImportConfig();
        updateContentType( "MyContentType", ctyconf.toString() );

        // exercise
        String source = "entry1;;";
        ImportContentsParams params = new ImportContentsParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.importName = "test-import";
        params.data = source;
        client.importContents( params );

        // verify
        CustomContentData contentData = (CustomContentData) fixture.findContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).get(
            0 ).getMainVersion().getContentData();
        assertEquals( null, ( (DateDataEntry) contentData.getEntry( "myDate" ) ).getValue() );
    }

    private void updateContentType( String contentTypeName, String contentTypeXml )
    {
        ContentTypeEntity contentType = fixture.findContentTypeByName( contentTypeName );
        contentType.setData( XMLDocumentFactory.create( contentTypeXml ).getAsJDOMDocument() );
        fixture.flushAndClearHibernateSession();
    }
}
