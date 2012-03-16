package com.enonic.cms.core.product;

import org.joda.time.DateTime;

public interface ProductLicense
{
    public boolean isValid();

    public DateTime getExpireDate();
}
