/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/29/11
 * Time: 3:13 PM
 */
public class ContentTestDataConfigHolder
{
    private ContentTypeConfig config;

    private Document standardConfigDoc;

    public ContentTypeConfig getConfig()
    {
        return config;
    }

    public void setConfig( ContentTypeConfig config )
    {
        this.config = config;
    }

    public Element getStandardConfigElement()
    {
        return standardConfigDoc.getRootElement();
    }

    public Document getStandardConfigDoc()
    {
        return standardConfigDoc;
    }

    public void setStandardConfigDoc( Document standardConfigDoc )
    {
        this.standardConfigDoc = standardConfigDoc;
    }

    public DataEntryConfig getInputConfig( String inputName )
    {
        return config.getInputConfig( inputName );
    }

}
