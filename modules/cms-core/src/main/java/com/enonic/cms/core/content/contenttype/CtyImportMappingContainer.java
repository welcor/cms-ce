/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

public interface CtyImportMappingContainer
{
    boolean addMapping( final CtyImportMappingConfig mapping );

    String getName();

    CtyImportConfig getImportConfig();
}
