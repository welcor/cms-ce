package com.enonic.cms.upgrade.task.datasource.method;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.DataSourceContext;

interface DataSourceService
{

    public XMLDocument getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
                                          boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query, String orderBy,
                                          int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, String orderBy,
                                            int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                  boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, String orderBy,
                                             int index, int count, boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, int count,
                                                   boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                   boolean includeData, int childrenLevel, int parentLevel );

    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics );

    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean includeUserRights );

    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean includeUserRights, int[] filterByCategories, boolean categoryRecursive,
                                   int[] filterByContentTypes );

    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean relatedTitlesOnly, boolean includeUserRights, int[] filterByCategories,
                                   boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getContentBySection( DataSourceContext context, String query, int[] menuItemKeys, int levels, String orderBy,
                                            int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                            int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                            boolean includeUserRights, int[] filterByContentType );

    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String orderBy, int fromIndex,
                                            int count, boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                            boolean relatedTitlesOnly, boolean includeTotalCount, boolean includeUserRights,
                                            int[] filterByContentTypes );

    public XMLDocument getMenuData( DataSourceContext context, int menuId );

    public XMLDocument getMenuData( DataSourceContext context );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, int startLevel, int levels );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, boolean details );

    // Not in use.

    XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents );

    public XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents, boolean complete );

    public XMLDocument getRandomContentByParent( DataSourceContext context, int count, int contentKey, boolean includeUserRights );

    public XMLDocument getRandomContentBySections( DataSourceContext context, String query, int[] menuItemKeys, int levels, int count,
                                                   boolean titlesOnly, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                                   boolean relatedTitlesOnly, boolean includeUserRights );

    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem );

    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels );

    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels, boolean details );

    public XMLDocument getSuperCategoryNames( DataSourceContext context, int categoryKey, boolean withContentCount,
                                              boolean includeCategory );

    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy, boolean requireAll,
                                           int fromIndex, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                           boolean includeTotalCount, int[] filterByCategories, boolean categoryRecursive,
                                           int[] filterByContentTypes );


    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String orderBy, boolean requireAll,
                                           int fromIndex, int count, boolean titlesOnly, int parentLevel, int childrenLevel,
                                           int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getRelatedContents( DataSourceContext context, int relation, int[] contentKeys, String query, String orderBy,
                                           boolean requireAll, int fromIndex, int count, boolean titlesOnly, int parentLevel,
                                           int childrenLevel, int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                           int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes );

    public XMLDocument getContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                             String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                             int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                             boolean includeUserRights, int[] contentTypes );

    public XMLDocument getMyContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                               String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                               int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                               boolean includeUserRights, int[] contentTypes );

    public XMLDocument findContentByCategory( DataSourceContext context, String search, String operator, int[] categories,
                                              boolean includeSubCategories, String orderBy, int index, int count, boolean titlesOnly,
                                              int childrenLevel, int parentLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                              boolean includeTotalCount, boolean includeUserRights, int[] contentTypes );

    public XMLDocument getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                      boolean includeCategory );

    public XMLDocument getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                      boolean contentCount );

    public XMLDocument getUser( DataSourceContext context, String qualifiedUsername, boolean includeMemberships, boolean normalizeGroups,
                                boolean includeCustomUserFields );
}
