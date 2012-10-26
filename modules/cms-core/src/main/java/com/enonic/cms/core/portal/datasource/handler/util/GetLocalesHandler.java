package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.Locale;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.locale.LocaleService;
import com.enonic.cms.core.locale.LocaleXmlCreator;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

// @Component
public final class GetLocalesHandler
    extends ParamDataSourceHandler
{
    private LocaleService localeService;

    public GetLocalesHandler()
    {
        super( "getLocales" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Locale[] locales = localeService.getLocales();
        final LocaleXmlCreator localeXmlCreator = new LocaleXmlCreator();
        return localeXmlCreator.createLocalesDocument( locales );
    }

    @Autowired
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
