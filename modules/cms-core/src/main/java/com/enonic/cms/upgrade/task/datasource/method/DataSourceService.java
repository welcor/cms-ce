/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource.method;

import com.enonic.cms.framework.xml.XMLDocument;

// Only used as a placeholder for not yet converted datasources. Delete when empty.
interface DataSourceService
{
    // SKIP
    public XMLDocument getUser( String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields );
}
