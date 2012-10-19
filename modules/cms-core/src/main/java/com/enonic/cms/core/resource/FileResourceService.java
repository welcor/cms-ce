/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceData;
import com.enonic.cms.core.resource.FileResourceName;

public interface FileResourceService
{
    FileResource getResource( FileResourceName name );

    boolean createFolder( FileResourceName name );

    boolean createFile( FileResourceName name, FileResourceData data );

    boolean deleteResource( FileResourceName name );

    List<FileResourceName> getChildren( FileResourceName name );

    FileResourceData getResourceData( FileResourceName name );

    boolean setResourceData( FileResourceName name, FileResourceData data );

    boolean moveResource( FileResourceName from, FileResourceName to );

    boolean copyResource( FileResourceName from, FileResourceName to );
}
