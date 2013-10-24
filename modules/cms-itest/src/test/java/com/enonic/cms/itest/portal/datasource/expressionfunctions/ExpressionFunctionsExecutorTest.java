/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.datasource.expressionfunctions;

import java.util.Properties;

import javax.servlet.http.Cookie;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.datasource.el.ExpressionContext;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsFactory;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class ExpressionFunctionsExecutorTest
    extends AbstractSpringTest
{
    @Autowired
    private DomainFixture fixture;

    private MockTimeService timeService;

    private UserEntity defaultUser;

    private ExpressionContext expressionContext;

    private ExpressionFunctionsFactory efFactory;

    private ExpressionFunctionsExecutor efExecutor;


    @Before
    public void before()
    {
        fixture.initSystemData();

        defaultUser = fixture.createAndStoreNormalUserWithUserGroup( "testuser", "testuser", "testuserstore" );
        defaultUser.setEmail( "email@email.com" );

        timeService = new MockTimeService();

        expressionContext = new ExpressionContext();
        expressionContext.setUser( defaultUser );
        SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        expressionContext.setSite( site );

        efFactory = new ExpressionFunctionsFactory();
        efFactory.setTimeService( timeService );
        efFactory.setContext( expressionContext );

        efExecutor = new ExpressionFunctionsExecutor();
        efExecutor.setExpressionContext( expressionContext );

        final Properties siteProperties = new Properties();
        siteProperties.setProperty( "cms.test", "overridden" );
        siteProperties.setProperty( "cms.site.test", "site" );
        efExecutor.setSiteProperties( new SiteProperties( site.getKey(), siteProperties ) );

        final Properties rootProperties = new Properties();
        rootProperties.setProperty( "cms.test", "root" );
        siteProperties.setProperty( "cms.root.test", "root" );
        efExecutor.setRootProperties( rootProperties );
    }

    @Test
    public void testUserGetEmailReturnsLoggedInUserEmail()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${user.email}" );
        assertEquals( "email@email.com", evaluated );
    }

    @Test
    public void testSingleValue()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "brands", "bmw" );

        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        assertEquals( "bmw", efExecutor.evaluate( "${param.brands}" ) );
        assertEquals( "bmw", efExecutor.evaluate( "${param['brands']}" ) );
        assertEquals( "true", efExecutor.evaluate( "${param.brands == 'bmw'}" ) );
    }

    @Test
    public void testCookieValue()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies( new Cookie( "param1", "value1" ), new Cookie( "param2", "value2" ), new Cookie( "param3", "value3" ) );

        efExecutor.setHttpRequest( request );

        assertEquals( "value1", efExecutor.evaluate( "${cookie.param1}" ) );
        assertEquals( "value2", efExecutor.evaluate( "${cookie.param2}" ) );
        assertEquals( "value3", efExecutor.evaluate( "${cookie.param3}" ) );
        assertEquals( null, efExecutor.evaluate( "${cookie.param4}" ) );
    }

    @Test
    public void testSessionValue()
        throws Exception
    {
        final VerticalSession verticalSession = new VerticalSession();
        verticalSession.setAttribute( "param1", "value1" );
        verticalSession.setAttribute( "param2", "value2" );
        verticalSession.setAttribute( "param3", "value3" );
        efExecutor.setVerticalSession( verticalSession );

        assertEquals( "value1", efExecutor.evaluate( "${session.param1}" ) );
        assertEquals( "value2", efExecutor.evaluate( "${session.param2}" ) );
        assertEquals( "value3", efExecutor.evaluate( "${session.param3}" ) );
        assertEquals( null, efExecutor.evaluate( "${session.param4}" ) );
    }

    @Test
    public void testMultipleValue()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "brands", "bmw" );

        request.addParameter( "cars", "skoda" );
        request.addParameter( "cars", "lexus" );
        request.addParameter( "cars", "volvo" );

        request.addParameter( "third", new String[]{"audi", "kia", "opel"} );

        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        assertEquals( "skoda,lexus,volvo", efExecutor.evaluate( "${params.cars}" ) );

        assertEquals( "3", efExecutor.evaluate( "${params.cars.length}" ) );
        assertEquals( "3", efExecutor.evaluate( "${params['cars'].length}" ) );

        assertEquals( "skoda", efExecutor.evaluate( "${params.cars[0]}" ) );
        assertEquals( "skoda", efExecutor.evaluate( "${params['cars'][0]}" ) );

        assertEquals( "volvo", efExecutor.evaluate( "${params.cars[2]}" ) );
        assertEquals( "volvo", efExecutor.evaluate( "${params['cars'][2]}" ) );

        assertEquals( "false", efExecutor.evaluate( "${params.cars == 'skoda'}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params.cars[0] == 'skoda'}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params['cars'][2] == 'volvo'}" ) );

        assertEquals( "3", efExecutor.evaluate( "${params.third.length}" ) );
        assertEquals( "3", efExecutor.evaluate( "${params['third'].length}" ) );

        assertEquals( "audi", efExecutor.evaluate( "${params.third[0]}" ) );
        assertEquals( "audi", efExecutor.evaluate( "${params['third'][0]}" ) );
        assertEquals( "opel", efExecutor.evaluate( "${params.third[2]}" ) );
        assertEquals( "opel", efExecutor.evaluate( "${params['third'][2]}" ) );

        assertEquals( "false", efExecutor.evaluate( "${params.third == 'audi'}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params.third != 'audi'}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params.third[0] == 'audi'}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params.third[2] == 'opel'}" ) );
    }

    @Test
    public void testSingleValue_asParams()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "brands", "bmw" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        assertEquals( "1", efExecutor.evaluate( "${params.brands.length}" ) );
        assertEquals( "1", efExecutor.evaluate( "${params['brands'].length}" ) );
        assertEquals( "bmw", efExecutor.evaluate( "${params.brands[0]}" ) );
        assertEquals( "bmw", efExecutor.evaluate( "${params['brands'][0]}" ) );
        assertEquals( "true", efExecutor.evaluate( "${params.brands[0] == 'bmw'}" ) );
    }

    @Test
    public void testSingleParameterEvaulation()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "subCat", "18" );
        request.addParameter( "sub-cat", "27" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        assertEquals( "18", efExecutor.evaluate( "${param.subCat}" ) );
        assertEquals( "27", efExecutor.evaluate( "${param['sub-cat']}" ) );
    }

    @Test
    public void testArrayOfParametersWithTheSameNameEvaulation()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "brands", "bmw" );
        request.addParameter( "brands", "volvo" );
        request.addParameter( "brands", "skoda" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        String param = efExecutor.evaluate( "${params.brands}" );
        assertEquals( "bmw,volvo,skoda", param );

        param = efExecutor.evaluate( "${params.brands[0]}" );
        assertEquals( "bmw", param );

        param = efExecutor.evaluate( "${params.brands[1]}" );
        assertEquals( "volvo", param );

        param = efExecutor.evaluate( "${params.brands[2]}" );
        assertEquals( "skoda", param );

        String length = efExecutor.evaluate( "${params.brands.length}" );
        assertEquals( "3", length );
    }

    @Test
    public void testArrayOfParametersWithTheSameNameEvaulation_variableWithDash()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "my-brands", "bmw" );
        request.addParameter( "my-brands", "volvo" );
        request.addParameter( "my-brands", "skoda" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        String param = efExecutor.evaluate( "${params['my-brands']}" );
        assertEquals( "bmw,volvo,skoda", param );

        param = efExecutor.evaluate( "${params['my-brands'][0]}" );
        assertEquals( "bmw", param );

        param = efExecutor.evaluate( "${params['my-brands'][1]}" );
        assertEquals( "volvo", param );

        param = efExecutor.evaluate( "${params['my-brands'][2]}" );
        assertEquals( "skoda", param );

        String length = efExecutor.evaluate( "${params['my-brands'].length}" );
        assertEquals( "3", length );
    }


    @Test
    public void testEvaluateCurrentDateWithTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd HH:mm' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 12:30", evaluated );
    }

    @Test
    public void testEvaluateCurrentDateWithoutTime()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDate( 'yyyy.MM.dd' )}" );
        assertEquals( "@publishfrom >= 2010.05.28", evaluated );
    }

    @Test
    public void testEvaluateCurrentDateMinusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated =
            efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluated );

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT2H35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 09:55", evaluated );

        // .. and with negative periods

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -35 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluated );

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDateMinusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-35M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 15:05", evaluated );
    }

    @Test
    public void testEvaluateCurrentDatePlusOffset()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated =
            efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( 2, 5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluated );

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT2H5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 14:35", evaluated );

        // .. and with negative periods

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', periodHoursMinutes( -2, -5 ) )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluated );

        evaluated = efExecutor.evaluate( "@publishfrom >= ${currentDatePlusOffset( 'yyyy.MM.dd HH:mm', 'PT-2H-5M' )}" );
        assertEquals( "@publishfrom >= 2010.05.28 10:25", evaluated );
    }

    @Test
    public void testEvaluatePositiveDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated = efExecutor.evaluate( "${periodHoursMinutes( 2, 5 )}" );
        assertEquals( "PT2H5M", evaluated );
    }

    @Test
    public void testEvaluateNegativeDurationHoursMinutes()
        throws Exception
    {
        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated = efExecutor.evaluate( "${periodHoursMinutes( -2, -5 )}" );
        assertEquals( "PT-2H-5M", evaluated );
    }

    @Test
    public void testPortalSiteKey()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${portal.siteKey}" );
        assertEquals( "0", evaluated );
    }

    @Test
    public void testPortalSiteKeyValueDoesNotExists()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${portal.siteKey1233}" );
        assertEquals( null, evaluated );
    }

    @Test
    public void testUrlEncode()
        throws Exception
    {
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.addParameterValue( "other", "&greeting=Hei ÆØÅ!" );
        efExecutor.setRequestParameters( requestParameters );
        String evaluated = efExecutor.evaluate( "${concat('https://test.test.no/api/', '/bestillKurs?api-key=testuser&amp;api-secret=testuser', urlEncode(param.other))}" );
        assertEquals( "https://test.test.no/api//bestillKurs?api-key=testuser&amp;api-secret=testuser%26greeting%3DHei+%C3%86%C3%98%C3%85%21", evaluated );
    }


    @Test
    public void testUrlEncodeEmpty()
        throws Exception
    {
        String evaluated = efExecutor.evaluate(
            "${concat('https://test.test.no/api/', '/bestillKurs?api-key=testuser&amp;api-secret=testuser', urlEncode(param.other))}" );
        assertEquals( "https://test.test.no/api//bestillKurs?api-key=testuser&amp;api-secret=testuser", evaluated );
    }

    @Test
    public void testPropertyFromSite()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${properties['cms.site.test']}" );
        assertEquals( "site", evaluated );
    }

    @Test()
    public void testBadFunc()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${concat(}" );
        assertEquals( "ERROR: Found closing '}' at position 9 but most recent opening is '(' at position 8", evaluated );
    }

    @Test
    public void testPropertiesFromSite()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${concat(properties['cms.site.test'], properties['cms.site.test'])}" );
        assertEquals( "sitesite", evaluated );
    }

    @Test
    public void testPropertiesFromSite2()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${concat(properties.cms.site.test, properties.cms.site.test)}" );
        assertEquals( "sitesite", evaluated );
    }

    @Test
    public void testPropertiesFromSiteInFunc()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${upper(properties.cms.site.test)}" );
        assertEquals( "SITE", evaluated );
    }

    @Test
    public void testPropertyFromRoot()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${properties['cms.root.test']}" );
        assertEquals( "root", evaluated );
    }

    @Test
    public void testOverriddenProperty()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${properties['cms.test']}" );
        assertEquals( "overridden", evaluated );
    }

    @Test
    public void testMissedProperty()
        throws Exception
    {
        String evaluated = efExecutor.evaluate( "${properties['cms.test.none']}" );
        assertEquals( null, evaluated );
    }

    @Test
    public void testEvaluateNegativeDurationHoursMinutesComplex()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter( "subCat", "-5" );
        request.addParameter( "sub-cat", "-2" );
        efExecutor.setHttpRequest( request );
        efExecutor.setRequestParameters( new RequestParameters( request.getParameterMap() ) );

        timeService.setTimeNow( new DateTime( 2010, 5, 28, 12, 30, 4, 2 ) );

        String evaluated = efExecutor.evaluate( "${periodHoursMinutes( param['sub-cat'], param.subCat )}" );
        assertEquals( "PT-2H-5M", evaluated );
    }

}
