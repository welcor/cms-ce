/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions.admin;

import java.util.UUID;

final class UniqueIdGeneratorImpl
    implements UniqueIdGenerator
{
    public String generateUniqueId()
    {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }
}
