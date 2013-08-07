/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.ext;

import com.enonic.cms.api.plugin.ext.Extension;

public interface ExtensionListener
{
    public void extensionAdded( Extension ext );

    public void extensionRemoved( Extension ext );
}
