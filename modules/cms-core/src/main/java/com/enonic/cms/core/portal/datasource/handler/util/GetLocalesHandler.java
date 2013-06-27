/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.Locale;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.locale.LocaleService;
import com.enonic.cms.core.locale.LocaleXmlCreator;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetLocalesHandler")
public final class GetLocalesHandler
    extends SimpleDataSourceHandler
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
