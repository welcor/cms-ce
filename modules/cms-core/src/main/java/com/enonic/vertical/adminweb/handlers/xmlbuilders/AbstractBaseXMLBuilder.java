/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.service.AdminService;

public abstract class AbstractBaseXMLBuilder
{
    protected AdminService admin;

    @Autowired
    public void setAdminService( AdminService value )
    {
        this.admin = value;
    }
}
