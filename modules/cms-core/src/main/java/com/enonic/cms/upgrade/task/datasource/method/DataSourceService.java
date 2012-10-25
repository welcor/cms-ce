package com.enonic.cms.upgrade.task.datasource.method;

import com.enonic.cms.framework.xml.XMLDocument;

// Only used as a placeholder for not yet converted datasources. Delete when empty.
interface DataSourceService
{

    public XMLDocument getContentByQuery( String query, String orderBy, int index, int count, boolean includeData, int childrenLevel,
                                          int parentLevel );

    public XMLDocument getRelatedContent( int[] contentKeys, int relation, String query, String orderBy, int index, int count,
                                          boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getContentBySection( int[] menuItemKeys, int levels, String query, String orderBy, int index, int count,
                                            boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getRandomContentBySection( int[] menuItemKeys, int levels, String query, int count, boolean includeData,
                                                  int childrenLevel, int parentLevel );

    public XMLDocument getContentByCategory( int[] categoryKeys, int levels, String query, String orderBy, int index, int count,
                                             boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getRandomContentByCategory( int[] categoryKeys, int levels, String query, int count, boolean includeData,
                                                   int childrenLevel, int parentLevel );

    public XMLDocument getContent( int[] contentKeys, String query, String orderBy, int index, int count, boolean includeData,
                                   int childrenLevel, int parentLevel );

    public XMLDocument getContent( int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics );

    public XMLDocument getContent( int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel, boolean updateStatistics,
                                   boolean includeUserRights );

    public XMLDocument getContent( int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel, boolean updateStatistics,
                                   boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                   int[] filterByContentTypes );

    public XMLDocument getContent( int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel, boolean updateStatistics,
                                   boolean relatedTitlesOnly, boolean includeUserRights, int[] filterByCategories,
                                   boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getContentBySection( String query, int[] menuItemKeys, int levels, String orderBy, int fromIndex, int count,
                                            boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                            boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                            int[] filterByContentType );

    public XMLDocument getContentBySection( int[] menuItemKeys, int levels, String orderBy, int fromIndex, int count, boolean titlesOnly,
                                            int parentLevel, int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                            boolean includeTotalCount, boolean includeUserRights, int[] filterByContentTypes );

    public XMLDocument getRandomContentByParent( int count, int contentKey, boolean includeUserRights );

    public XMLDocument getRandomContentBySections( String query, int[] menuItemKeys, int levels, int count, boolean titlesOnly,
                                                   int parentLevel, int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                                   boolean includeUserRights );

    public XMLDocument getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory );

    public XMLDocument getRelatedContents( int relation, int[] contentKeys, String orderBy, boolean requireAll, int fromIndex, int count,
                                           int parentLevel, int childrenLevel, int parentChildrenLevel, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes );


    public XMLDocument getRelatedContents( int relation, int[] contentKeys, String orderBy, boolean requireAll, int fromIndex, int count,
                                           boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                           boolean relatedTitlesOnly, boolean includeTotalCount, int[] filterByCategories,
                                           boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getRelatedContents( int relation, int[] contentKeys, String query, String orderBy, boolean requireAll, int fromIndex,
                                           int count, boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                           boolean relatedTitlesOnly, boolean includeTotalCount, int[] filterByCategories,
                                           boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getContentByCategory( String query, int[] categories, boolean includeSubCategories, String orderBy, int index,
                                             int count, boolean titlesOnly, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                             boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                             int[] contentTypes );

    public XMLDocument getMyContentByCategory( String query, int[] categories, boolean includeSubCategories, String orderBy, int index,
                                               int count, boolean titlesOnly, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                               boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                               int[] contentTypes );

    public XMLDocument findContentByCategory( String search, String operator, int[] categories, boolean includeSubCategories,
                                              String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                              int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                              boolean includeUserRights, int[] contentTypes );

    public XMLDocument getCategories( int superCategoryKey, int level, boolean withContentCount, boolean includeCategory );

    public XMLDocument getCategories( int key, int levels, boolean topLevel, boolean details, boolean catCount, boolean contentCount );

    public XMLDocument getUser( String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields );
}
