/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.xslt.XsltProcessorException;

public interface PortalXsltProcessorFactory
{
    public PortalXsltProcessor createProcessor( FileResourceName name )
        throws XsltProcessorException;
}
