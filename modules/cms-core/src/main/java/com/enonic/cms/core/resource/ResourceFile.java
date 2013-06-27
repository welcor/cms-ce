/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.InputStream;

import com.enonic.cms.framework.xml.XMLDocument;

public interface ResourceFile
    extends ResourceBase
{
    String getMimeType();

    long getSize();

    XMLDocument getDataAsXml();

    String getDataAsString();

    byte[] getDataAsByteArray();

    InputStream getDataAsInputStream();
}
