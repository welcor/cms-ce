/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsFactory;
import com.enonic.cms.core.portal.datasource.xml.DataSourceElement;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserImpl;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.SiteEntity;

public class DataSourceExecutorTest
{
    private DataSourceExecutorContext context;

    @Before
    public void setup()
    {
        context = new DataSourceExecutorContext();

        MockHttpServletRequest request = new MockHttpServletRequest();
        LanguageEntity languageEntity = new LanguageEntity();
        languageEntity.setCode( "no" );

        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createSite( new SiteKey( 0 ) );

        UserStoreEntity userStoreEntity = new UserStoreEntity();
        userStoreEntity.setName( "demousers" );

        // looks strange, but is needed
        new ExpressionFunctionsFactory();

        UserEntity userEntity = new UserEntity();
        userEntity.setName( "elvis" );
        userEntity.setKey( new UserKey( "userkey" ) );
        userEntity.setDisplayName( "Elvis Presley" );
        userEntity.setEmail( "elvis@graceland.com" );
        userEntity.setUserStore( userStoreEntity );

        User oldUser = new UserImpl();
        oldUser.setSelectedLanguageCode( "no" );

        context.setDeviceClass( "default" );
        context.setHttpRequest( request );
        context.setLanguage( languageEntity );
        context.setPortalInstanceKey( portalInstanceKey );
        SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        context.setSite( site );
        context.setUser( userEntity );
        context.setVerticalSession( null );
    }

    @Test
    public void testNullCondition()
    {
        final DataSourceExecutor executor = new DataSourceExecutor( this.context );
        final DataSourceElement element = new DataSourceElement( "test" );
        assertTrue( executor.isRunnableByCondition( element ) );
    }

    @Test
    public void testEmptyCondition()
    {
        final DataSourceExecutor executor = new DataSourceExecutor( this.context );
        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "" );

        assertTrue( executor.isRunnableByCondition( element ) );
    }

    @Test
    public void testConditionWithoutBraces()
    {
        final DataSourceExecutor executor = new DataSourceExecutor( this.context );
        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "1==1" );

        assertFalse( executor.isRunnableByCondition( element ) );
    }

    @Test
    public void testTrueUserUidCondition()
    {
        final DataSourceExecutor executor = new DataSourceExecutor( this.context );
        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${user.uid == 'elvis'}" );

        assertTrue( executor.isRunnableByCondition( element ) );
    }

    @Test
    public void testFalseUserUidCondition()
    {
        final DataSourceExecutor executor = new DataSourceExecutor( this.context );
        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${user.uid == 'aron'}" );

        assertFalse( executor.isRunnableByCondition( element ) );
    }

    @Test
    public void testIsblank()
    {
        final RequestParameters requestParameters = new RequestParameters();
        context.setRequestParameters( requestParameters );

        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${ isblank( param.myParam ) }" );

        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.removeParameter( "myParam" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );
    }

    @Test
    public void testIsnotblank()
    {
        final RequestParameters requestParameters = new RequestParameters();
        context.setRequestParameters( requestParameters );

        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${ isnotblank( param.myParam ) }" );

        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.removeParameter( "myParam" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );
    }

    @Test
    public void testIsempty()
    {
        final RequestParameters requestParameters = new RequestParameters();
        context.setRequestParameters( requestParameters );

        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${ isempty( param.myParam ) }" );

        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.removeParameter( "myParam" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );
    }

    @Test
    public void testIsnotempty()
    {
        final RequestParameters requestParameters = new RequestParameters();
        context.setRequestParameters( requestParameters );

        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${ isnotempty( param.myParam ) }" );

        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "value" );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", " " );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.setParameterValue( "myParam", "" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        requestParameters.removeParameter( "myParam" );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );
    }

    @Test
    public void testIsWindowInline()
    {
        final DataSourceElement element = new DataSourceElement( "test" );
        element.setCondition( "${ portal.isWindowInline == true }" );

        context.setPortletWindowRenderedInline( true );
        assertTrue( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        context.setPortletWindowRenderedInline( false );
        assertFalse( new DataSourceExecutor( context ).isRunnableByCondition( element ) );

        element.setCondition( "${ portal.isWindowInline }" );
        context.setPortletWindowRenderedInline( true );
        assertEquals( new DataSourceExecutor( context ).isRunnableByCondition( element ), true );

        element.setCondition( "${ portal.isWindowInline }" );
        context.setPortletWindowRenderedInline( false );
        assertEquals( new DataSourceExecutor( context ).isRunnableByCondition( element ), false );

    }
}
