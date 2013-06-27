/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.context;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.cms.framework.xml.XMLDocumentHelper;

import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

public class GetSessionContextHandlerTest
    extends AbstractDataSourceHandlerTest<GetSessionContextHandler>
{
    public GetSessionContextHandlerTest()
    {
        super( GetSessionContextHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.handler.setDataSourceService( this.dataSourceService );
    }

    @Test
    public void testHandler_get_session_context()
        throws Exception
    {
        final VerticalSession verticalSession = new VerticalSession();
        verticalSession.setAttribute( "value-string", "data" );
        verticalSession.setAttribute( "value-set", Sets.newHashSet( 1, 2, 3 ) );
        verticalSession.setAttribute( "value-xml", XMLDocumentHelper.convertToW3CDocument( "<xml-value>data</xml-value>" ) );
        this.request.setVerticalSession( verticalSession );

        testHandle( "getSessionContext_result" );
    }

    @Test
    public void testHandler_empty_session()
        throws Exception
    {
        final VerticalSession verticalSession = new VerticalSession();
        this.request.setVerticalSession( verticalSession );

        testHandle( "getSessionContext_empty" );
    }
}
