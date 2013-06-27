/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

public interface ImportDataReader
{
    ImportDataEntry getNextEntry();

    boolean hasMoreEntries();
}
