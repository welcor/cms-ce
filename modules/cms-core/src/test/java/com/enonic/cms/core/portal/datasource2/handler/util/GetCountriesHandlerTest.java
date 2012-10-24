package com.enonic.cms.core.portal.datasource2.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.Region;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;

public class GetCountriesHandlerTest
    extends AbstractDataSourceHandlerTest<GetCountriesHandler>
{
    public GetCountriesHandlerTest()
    {
        super( GetCountriesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        final Country c1 = new Country( new CountryCode( "SE" ), "SWEDEN", "SVERIGE", "46" );

        final Country c2 = new Country( new CountryCode( "NO" ), "NORWAY", "NORGE", "47" );
        c2.setRegionsEnglishName( "County" );
        c2.setRegionsLocalName( "Fylke" );
        c2.addRegion( new Region( "02", "Akershus", "Akershus" ) );

        final CountryService countryService = Mockito.mock( CountryService.class );
        Mockito.when( countryService.getCountries() ).thenReturn( Lists.newArrayList( c1, c2 ) );
        Mockito.when( countryService.getCountry( new CountryCode( "SE" ) ) ).thenReturn( c1 );
        Mockito.when( countryService.getCountry( new CountryCode( "NO" ) ) ).thenReturn( c2 );

        this.handler.setCountryService( countryService );
    }

    @Test
    public void testHandler_all()
        throws Exception
    {
        testHandle( "getCountries_all" );
    }

    @Test
    public void testHandler_NO()
        throws Exception
    {
        this.request.addParam( "countryCodes", "NO" );
        testHandle( "getCountries_NO" );
    }

    @Test
    public void testHandler_NO_Unknown()
        throws Exception
    {
        this.request.addParam( "countryCodes", "NO,XX" );
        testHandle( "getCountries_NO" );
    }

    @Test
    public void testHandler_NO_SE()
        throws Exception
    {
        this.request.addParam( "countryCodes", "NO,SE" );
        testHandle( "getCountries_NO_SE" );
    }

    @Test
    public void testHandler_NO_includeRegions()
        throws Exception
    {
        this.request.addParam( "countryCodes", "NO" );
        this.request.addParam( "includeRegions", "true" );
        testHandle( "getCountries_NO_regions" );
    }
}
