/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentDocumentXMLBuilder;

public class ContentDocumentHandlerServlet
    extends ContentBaseHandlerServlet
{
    public ContentDocumentHandlerServlet()
    {
        super();

        FORM_XSL = "document_form.xsl";
    }

    @Autowired
    public void setContentDocumentXMLBuilder( final ContentDocumentXMLBuilder builder )
    {
        setContentXMLBuilder( builder );
    }
}
