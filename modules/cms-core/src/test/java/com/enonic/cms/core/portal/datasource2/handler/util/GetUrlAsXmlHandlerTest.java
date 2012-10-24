package com.enonic.cms.core.portal.datasource2.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.http.HTTPService;

public class GetUrlAsXmlHandlerTest
    extends AbstractDataSourceHandlerTest<GetUrlAsXmlHandler>
{
    private HTTPService httpService;

    public GetUrlAsXmlHandlerTest()
    {
        super( GetUrlAsXmlHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.httpService = Mockito.mock( HTTPService.class );
        Mockito.when( this.httpService.getURLAsBytes( Mockito.anyString(), Mockito.anyInt() ) ).thenReturn( "<dummy/>".getBytes() );
        this.handler.setHttpService( this.httpService );
    }

    @Test(expected = DataSourceException.class)
    public void testUrlNotSet()
        throws Exception
    {
        this.handler.handle( this.request );
    }

    @Test
    public void testDefaultParams()
        throws Exception
    {
        this.request.addParam( "url", "http://www.enonic.com" );
        testHandle( "getUrlAsXml_result" );
        Mockito.verify( this.httpService, Mockito.times( 1 ) ).getURLAsBytes( "http://www.enonic.com", 5000 );
    }

    @Test
    public void testSetParams()
        throws Exception
    {
        this.request.addParam( "url", "http://www.enonic.com" );
        this.request.addParam( "timeout", "1000" );
        testHandle( "getUrlAsXml_result" );
        Mockito.verify( this.httpService, Mockito.times( 1 ) ).getURLAsBytes( "http://www.enonic.com", 1000 );
    }

    @Test(expected = DataSourceException.class)
    public void testIllegalTimeout()
        throws Exception
    {
        this.request.addParam( "url", "http://www.enonic.com" );
        this.request.addParam( "timeout", "abc" );
        this.handler.handle( this.request );
    }
}
