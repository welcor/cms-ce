package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.Locale;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.locale.LocaleService;

public class GetLocalesHandlerTest
    extends AbstractDataSourceHandlerTest<GetLocalesHandler>
{
    private LocaleService localeService;

    public GetLocalesHandlerTest()
    {
        super( GetLocalesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.localeService = Mockito.mock( LocaleService.class );
        this.handler.setLocaleService( this.localeService );
    }

    @Test
    public void testEmpty()
        throws Exception
    {
        Mockito.when( this.localeService.getLocales() ).thenReturn( new Locale[0] );
        testHandle( "getLocales_empty" );
    }

    @Test
    public void testList()
        throws Exception
    {
        Mockito.when( this.localeService.getLocales() ).thenReturn( new Locale[]{Locale.US, Locale.CANADA} );
        testHandle( "getLocales_list" );
    }
}
