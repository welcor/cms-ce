package com.enonic.cms.itest.content.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.content.imports.ImportResult;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class ImportServiceImplTest_importJobThreadSafe
        extends AbstractSpringTest
{
    @Autowired
    private DomainFixture fixture;

    @Autowired
    private ImportJobFactory importJobFactory;

    private String personContentTypeXml;


    @Before
    public void setUp()
            throws IOException
    {
        personContentTypeXml = resourceToString( new ClassPathResource( "com/enonic/cms/itest/content/imports/personContentType.xml" ) );

        DomainFactory factory = fixture.getFactory();

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "PersonCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 XMLDocumentFactory.create( personContentTypeXml ).getAsJDOMDocument() ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "Persons", null, "PersonCty", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Persons", "testuser", "read, create, approve" ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );

        ImportJobFactory.setExecuteInOneTransaction( true );
    }

    @Test
    public void check_thread_safe_import_job()
            throws UnsupportedEncodingException, InterruptedException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='importer'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1'>";
        importData += "     <name>USER-1</name>";
        importData += "  </person>";
        importData += "  <person id='2'>";
        importData += "     <name>USER-2</name>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "importer";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );

        Thread one = new ImportSimulator( job );
        Thread two = new ImportSimulator( job );
        Thread three = new ImportSimulator( job );

        one.start();
        two.start();
        three.start();

        one.join();
        two.join();
        three.join();

        fixture.flushAndClearHibernateSesssion();
    }

    private void updateContentType( String contentTypeName, String contentTypeXml )
    {
        ContentTypeEntity contentType = fixture.findContentTypeByName( contentTypeName );
        contentType.setData( XMLDocumentFactory.create( contentTypeXml ).getAsJDOMDocument() );
        fixture.flushAndClearHibernateSesssion();
    }

    private String resourceToString( Resource resource )
            throws IOException
    {
        return IOUtils.toString( resource.getInputStream() );
    }

    private class ImportSimulator
            extends Thread
    {

        private ImportJob job;

        private ImportSimulator( ImportJob job )
        {
            this.job = job;
        }

        public void run()
        {
            ImportResult result = job.start();

            System.out.println( "Run " + Thread.currentThread().getName() );

            assertEquals( 2, fixture.countAllContent() );
            assertEquals( 1, fixture.countContentVersionsByTitle( "USER-1" ) );
            assertEquals( 1, fixture.countContentVersionsByTitle( "USER-2" ) );
        }
    }
}
