/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;

import com.enonic.cms.core.localization.LocalizationResourceBundle;
import com.enonic.cms.core.structure.SiteEntity;

public interface LocalizationResourceBundleService
{
    public LocalizationResourceBundle getResourceBundle( SiteEntity site, Locale locale );
}
