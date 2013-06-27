/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.product;

public final class NopLicenseChecker
    implements LicenseChecker
{
    @Override
    public boolean isError()
    {
        return false;
    }

    @Override
    public String getMessage()
    {
        return null;
    }
}
