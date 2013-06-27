/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuilder;

public abstract class AbstractContentHandlerServlet
    extends AdminHandlerBaseServlet
{
    protected ContentXMLBuilder contentXMLBuilder;

    public void setContentXMLBuilder( ContentXMLBuilder contentXMLBuilder )
    {
        this.contentXMLBuilder = contentXMLBuilder;
    }

    public ContentXMLBuilder getContentXMLBuilder()
    {
        return contentXMLBuilder;
    }
}
