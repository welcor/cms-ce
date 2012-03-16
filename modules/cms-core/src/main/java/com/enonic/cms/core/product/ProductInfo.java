package com.enonic.cms.core.product;

import com.enonic.cms.api.Version;

public abstract class ProductInfo
{
    public final String getTitle()
    {
        return Version.getTitle();
    }

    public final String getVersion()
    {
        return Version.getVersion();
    }
    
    public final String getCopyright()
    {
        return Version.getCopyright();
    }

    public abstract ProductEdition getEdition();

    public abstract ProductLicense getLicense();
}
