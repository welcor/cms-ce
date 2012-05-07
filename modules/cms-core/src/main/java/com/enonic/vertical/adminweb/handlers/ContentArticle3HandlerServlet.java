/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentArticle3XMLBuilder;

public class ContentArticle3HandlerServlet
    extends ContentBaseHandlerServlet
{
    public ContentArticle3HandlerServlet()
    {
        super();
        FORM_XSL = "article3_form.xsl";
    }

    @Autowired
    public void setContentArticle3XMLBuilder( final ContentArticle3XMLBuilder builder )
    {
        setContentXMLBuilder( builder );
    }
}
