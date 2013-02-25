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
