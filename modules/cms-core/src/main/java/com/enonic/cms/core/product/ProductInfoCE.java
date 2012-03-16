package com.enonic.cms.core.product;

import org.joda.time.DateTime;

final class ProductInfoCE
    extends ProductInfo
{
    final class LicenseInfoImpl
        implements ProductLicense
    {
        public boolean isValid()
        {
            return true;
        }

        public DateTime getExpireDate()
        {
            return null;
        }
    }

    private final ProductLicense license;

    public ProductInfoCE()
    {
        this.license = new LicenseInfoImpl();
    }

    @Override
    public ProductEdition getEdition()
    {
        return ProductEdition.COMMUNITY;
    }

    @Override
    public ProductLicense getLicense()
    {
        return this.license;
    }
}
