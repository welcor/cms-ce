/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.preference;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

public class GetPreferencesHandlerTest
    extends AbstractDataSourceHandlerTest<GetPreferencesHandler>
{
    public GetPreferencesHandlerTest()
    {
        super( GetPreferencesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        Mockito.when(
            this.dataSourceService.getPreferences( Mockito.any( DataSourceContext.class ), Mockito.anyString(), Mockito.anyString(),
                                                   Mockito.anyBoolean() ) ).thenReturn( this.dummyDoc );
        this.handler.setDataSourceService( this.dataSourceService );
    }

    @Test
    public void testHandler_defaultParams()
        throws Exception
    {
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getPreferences( this.request, "*", "*", true );
    }

    @Test
    public void testHandler_params()
        throws Exception
    {
        this.request.addParam( "scope", "WINDOW" );
        this.request.addParam( "keyPattern", "dummy*" );
        this.request.addParam( "uniqueMatch", "false" );
        this.handler.handle( this.request );
        Mockito.verify( this.dataSourceService, Mockito.times( 1 ) ).getPreferences( this.request, "WINDOW", "dummy*", false );
    }
}
