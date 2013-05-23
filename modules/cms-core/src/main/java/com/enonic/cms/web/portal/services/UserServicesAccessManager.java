/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.services;

import com.enonic.cms.core.structure.SiteKey;


public interface UserServicesAccessManager
{
    boolean isOperationAllowed( SiteKey site, String service, String operation );
}
