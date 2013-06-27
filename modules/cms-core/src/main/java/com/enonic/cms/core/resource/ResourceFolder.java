/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.List;

public interface ResourceFolder
    extends ResourceBase
{
    ResourceFolder getFolder( String name );

    ResourceFile getFile( String name );

    List<ResourceFolder> getFolders();

    List<ResourceFile> getFiles();
}
