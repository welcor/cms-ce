/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.List;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.core.country.Country;
import com.enonic.cms.core.country.CountryCode;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.country.CountryXmlCreator;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetCountriesHandler")
public final class GetCountriesHandler
    extends ParamsDataSourceHandler<GetCountriesParams>
{
    private CountryService countryService;

    public GetCountriesHandler()
    {
        super( "getCountries", GetCountriesParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetCountriesParams params )
        throws Exception
    {
        final List<Country> countriesList = Lists.newArrayList();

        if ( params.countryCodes == null )
        {
            countriesList.addAll( this.countryService.getCountries() );
        }
        else
        {
            for ( final String countryCodeStr : params.countryCodes )
            {
                final Country country = this.countryService.getCountry( new CountryCode( countryCodeStr ) );
                if ( country != null )
                {
                    countriesList.add( country );
                }
            }
        }

        final CountryXmlCreator countryXmlCreator = new CountryXmlCreator();
        countryXmlCreator.setIncludeRegionsInfo( params.includeRegions );
        return countryXmlCreator.createCountriesDocument( countriesList );
    }

    @Autowired
    public void setCountryService( final CountryService countryService )
    {
        this.countryService = countryService;
    }
}
