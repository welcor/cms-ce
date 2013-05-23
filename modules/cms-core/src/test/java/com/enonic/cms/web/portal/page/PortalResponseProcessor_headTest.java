package com.enonic.cms.web.portal.page;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;
import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.server.DeploymentAndRequestSetup;
import com.enonic.cms.store.dao.SiteDao;

import static org.junit.Assert.*;

/**
 * unit tests for HEAD functionality
 */
public class PortalResponseProcessor_headTest
{
    public static final String CONTENT_VALUE = "content text";

    public static final String ETAG_VALUE = "content_F98393E248D02CFD7C597B8E640EED1D8F684824";

    public static final String ETAG_VALUE_INCORRECT = "content_F98393E248D02CFD7C597B8E640EED1D8F684824_";

    public static final String ETAG_HEADER_NAME = "Etag";

    private PortalResponseProcessor portalResponseProcessor = new PortalResponseProcessor();

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

    private SiteDao siteDao = Mockito.mock( SiteDao.class );

    private SitePath sitePath = new SitePath( new SiteKey( 0 ), new Path( "/news/local politics" ) );

    private PortalRequest portalRequest = new PortalRequest();

    private PortalResponse portalResponse = new PortalResponse();


    @Before
    public void before()
    {
        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( new SiteKey( 0 ), SitePropertyNames.PAGE_CACHE_HEADERS_ENABLED, "true" );

        httpServletRequest.setServerPort( 80 );

        ServletRequestAccessor.setRequest( httpServletRequest );
        portalResponseProcessor.setHttpRequest( httpServletRequest );
        portalResponseProcessor.setHttpResponse( httpServletResponse );
        portalResponseProcessor.setRequest( portalRequest );
        portalResponseProcessor.setResponse( portalResponse );

        portalResponseProcessor.setCacheHeadersEnabledForSite( true );

        portalResponseProcessor.setCurrentPortalRequestTrace( null );
        portalResponseProcessor.setResponseFilters( new ArrayList<HttpResponseFilter>() );

        new DeploymentAndRequestSetup().
            appDeployedAtRoot().
            originalRequest( "localhost", "/admin/site/0/political news shortcut" ).
            requestedSite( 0, "political news shortcut" ).
            requestedAdminDebugAt().
            setupAtDefaultPath().
            back().
            setup( httpServletRequest );

        httpServletRequest.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );

        portalRequest.setOriginalUrl( "http://localhost/admin/site/0/political news shortcut?" );
        portalRequest.setRequestTime( new DateTime() );
        portalRequest.setSitePath( sitePath );
        portalRequest.setRequester( new UserKey( "1" ) );

        portalResponse.setContent( CONTENT_VALUE );
    }


    @Test
    public void testServeResponse_check_modified_GET()
        throws Exception
    {
        httpServletRequest.setMethod( "GET" );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is equal to content and content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_modified_HEAD()
        throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is equal to content but no content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_HEAD()
        throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is equal to content but content does not exist
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );  // most important test
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_GET()
        throws Exception
    {
        httpServletRequest.setMethod( "GET" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is zero and no content exists
        assertEquals( 0, httpServletResponse.getContentLength() );
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_HEAD_non_matching_etag()
        throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE_INCORRECT );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is equal to content but content does not exist
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );  // most important test
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_GET_non_matching_etag()
        throws Exception
    {
        httpServletRequest.setMethod( "GET" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE_INCORRECT );

        // exercise
        portalResponseProcessor.serveResponse();

        // verify that length is zero and no content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );

    }

}
