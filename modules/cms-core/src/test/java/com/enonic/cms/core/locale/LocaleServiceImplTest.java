/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.locale;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocaleServiceImplTest
{
    @Test
    public void getLocales()
    {
        final Locale[] locales = new LocaleServiceImpl().getLocales();
        assertNotNull( locales );
    }
}
