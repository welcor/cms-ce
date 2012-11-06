/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.Calendar;

public interface ResourceBase
{
    String getName();

    String getPath();

    ResourceKey getResourceKey();

    Calendar getLastModified();

    boolean isHidden();
}
