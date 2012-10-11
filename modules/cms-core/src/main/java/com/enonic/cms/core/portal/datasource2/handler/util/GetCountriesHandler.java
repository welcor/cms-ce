package com.enonic.cms.core.portal.datasource2.handler.util;

import java.util.List;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.CountryXmlCreator;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetCountriesHandler
    extends DataSourceHandler
{
    private CountryService countryService;

    public GetCountriesHandler()
    {
        super( "getCountries" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String[] countryCodes = req.param( "countryCodes" ).asStringArray();
        final boolean includeRegions = req.param( "includeRegions" ).asBoolean( false );

        final List<Country> countriesList = Lists.newArrayList();

        if ( countryCodes.length == 0 )
        {
            countriesList.addAll( this.countryService.getCountries() );
        }
        else
        {
            for ( final String countryCodeStr : countryCodes )
            {
                final Country country = this.countryService.getCountry( new CountryCode( countryCodeStr ) );
                if ( country != null )
                {
                    countriesList.add( country );
                }
            }
        }

        final CountryXmlCreator countryXmlCreator = new CountryXmlCreator();
        countryXmlCreator.setIncludeRegionsInfo( includeRegions );
        return countryXmlCreator.createCountriesDocument( countriesList );
    }

    @Autowired
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
