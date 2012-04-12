/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentBaseXMLBuilder;

public class ContentFormHandlerServlet
    extends ContentBaseHandlerServlet
{
    public ContentFormHandlerServlet()
    {
        super();

        alwaysDisabled = true;
        FORM_XSL = "formbuilder_form.xsl";
    }

    @Autowired
    public void setContentBaseXMLBuilder(final ContentBaseXMLBuilder builder)
    {
        setContentXMLBuilder( builder );
    }
}
