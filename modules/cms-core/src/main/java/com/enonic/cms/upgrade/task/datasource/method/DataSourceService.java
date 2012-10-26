package com.enonic.cms.upgrade.task.datasource.method;

import com.enonic.cms.framework.xml.XMLDocument;

// Only used as a placeholder for not yet converted datasources. Delete when empty.
interface DataSourceService
{

    // Do not convert this - replaced by getContentByCategory with extended parameters?
    public XMLDocument getMyContentByCategory( String query, int[] categories, boolean includeSubCategories, String orderBy, int index,
                                               int count, boolean titlesOnly, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                               boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                               int[] contentTypes );

    // Try to convert to getContentByCategory
    public XMLDocument findContentByCategory( String search, String operator, int[] categories, boolean includeSubCategories,
                                              String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                              int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                              boolean includeUserRights, int[] contentTypes );

    public XMLDocument getCategories( int superCategoryKey, int level, boolean withContentCount, boolean includeCategory );

    public XMLDocument getCategories( int key, int levels, boolean topLevel, boolean details, boolean catCount, boolean contentCount );


    // SKIP
    public XMLDocument getUser( String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields );
}
