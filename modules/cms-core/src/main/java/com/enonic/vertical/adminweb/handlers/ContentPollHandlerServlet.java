/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentPollXMLBuilder;

public class ContentPollHandlerServlet
    extends ContentBaseHandlerServlet
{
    public ContentPollHandlerServlet()
    {
        super();
        FORM_XSL = "poll_form.xsl";
    }

    @Autowired
    public void setContentPollXMLBuilder( final ContentPollXMLBuilder builder )
    {
        setContentXMLBuilder( builder );
    }
}
