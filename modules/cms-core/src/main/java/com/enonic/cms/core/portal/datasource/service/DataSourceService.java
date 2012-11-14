/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.service;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.DataSourceContext;

/**
 * This interface defines all "old" datasource methods that is available in the presentation layer.
 */
public interface DataSourceService
{

    public XMLDocument getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
                                          boolean includeData, int childrenLevel, int parentLevel, final String facetsDefinition );

    /**
     * Get content, specified by the one of more given content keys. with the possibility to restrict it by filters, so it is only returned
     * if it belongs to a certain category or content type.  Full information about related content is not included.
     *
     *
     * @param context       the Vertical Site context
     * @param contentKeys   The content keys
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel the level of children to include
     * @param parentLevel   the level of parents to include
     * @param facets
     * @return An XML document with the result of the search
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                   boolean includeData, int childrenLevel, int parentLevel, final String facets );

    /**
     * Finds all the different versions of the keys presented, and includes possible children as related content, if specified. This method
     * is typically used after doing a search for getContent, to retrieve one or more of the versions of that content. If parents are needed
     * for the final presentation, they should be retrieved together with the content, while children should be retrieved with each
     * version.
     *
     * @param context       The Vertical Site context.
     * @param versionKeys   The Version Keys of the specific versions that are to be included in the response.
     * @param childrenLevel The number of levels below each version to search for children.
     * @return An XML document listing all the content versions as complete content, and their children.
     */
    public XMLDocument getContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel );


    /**
     * Finds either parents or children of the specified contents.  This set of parents or children is considered the main result of this
     * method.  Related content, as specified by <code>parentLevel</code> or <code>childrenLevel</code> are the related content of the root
     * set of children or parents, and may include the content that was passed in as content keys to the method.
     *
     *
     * @param context       the Vertical Site context
     * @param contentKeys   The content keys
     * @param relation      the allowed values are -1 for parents and 1 for children
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel the level of children to include
     * @param parentLevel   the level of parents to include
     * @param requireAll
     * @return An XML document with the result of the search
     */
    public XMLDocument getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query, String orderBy,
                                          int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                          final boolean requireAll );

    /**
     * Find content in a section specified by the given menu item keys.
     *
     *
     * @param context       The Vertical Site context
     * @param menuItemKeys  The menu items to search for the content within.
     * @param levels        Include sub menus below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @param facets
     * @return An XML document with the result of the search
     */
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, String orderBy,
                                            int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                            final String facets );

    /**
     * Retrieve random content in a section specified by the given menu item keys.
     *
     * @param context       The Vertical Site context.
     * @param menuItemKeys  The menu items to search for the content within.
     * @param levels        Include sub menus below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                  boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Find content in the specified categories.
     *
     *
     * @param context       The Vertical Site context
     * @param categoryKeys  The keys of the categories to search in.
     * @param levels        Include sub categories below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param orderBy       A String list of one or more data items to sort the result by, like an SQL Order by clause.
     * @param index         The starting index of the search result, from which to return data.  Should be set to zero in a fresh search.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @param filterOnUser
     * @return An XML document with the result of the search
     */
    public XMLDocument getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, String orderBy,
                                             int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                             final boolean filterOnUser );

    /**
     * Find content randomly in the specified categories.
     *
     * @param context       The Vertical Site context
     * @param categoryKeys  The keys of the categories to search in.
     * @param levels        Include sub categories below the specified menu item keys, this number of levels down.
     * @param query         The string query, specifying detailed filters and rules for which content to fetch, in and SQL like maner.
     * @param count         The number of documents to return in the resulting content XML.
     * @param includeData   Whether to include detailed data in the resulting XML, this is the opposite of the old <code>titlesOnly</code>
     *                      parameter.
     * @param childrenLevel The number of levels of children to include.
     * @param parentLevel   The number of levels of parents to include.
     * @return An XML document with the result of the search
     */
    public XMLDocument getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, int count,
                                                   boolean includeData, int childrenLevel, int parentLevel );

    /**
     * Get one content with the possibility to include information about the user rights. Filter for categories and/or content types may be
     * applied. May also specify if full related contents should be fetched (relatedTitlesOnly == false) or not.
     *
     * @param context              the Vertical Site context
     * @param contentKey           the content key
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param updateStatistics     update the read statistics for this content
     * @param relatedTitlesOnly    if true, return only titles of related contents
     * @param includeUserRights    include the user access rights in content
     * @param filterByCategories   filter by zero or more category keys
     * @param categoryRecursive    include subcategories of the category keys above
     * @param filterByContentTypes filter by zero or more content keys
     * @return a content xml
     */
    public XMLDocument getContent( DataSourceContext context, int[] contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                   boolean updateStatistics, boolean relatedTitlesOnly, boolean includeUserRights, int[] filterByCategories,
                                   boolean categoryRecursive, int[] filterByContentTypes );
    /**
     * Returns a menu tree. If levels is 0, entire menu are return. If levels is non-negative number, that number of tree levels are
     * returned or the entire tree if levels is greater than or equal to the number of levels in the menu tree. Only menu items marked as
     * 'show in menu' will be included in the result.
     *
     * @param context Site context
     * @param menuKey Menu key
     * @param tagItem Menu item to tag
     * @param levels  Number of levels to return
     * @return An XML Document with all details about the requested menu.
     */
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels );

    /**
     * Get the settings defined for a menu.
     *
     * @param context the Vertical Site context
     * @param menuId  a menu key
     * @return menu data xml
     */
    public XMLDocument getMenuData( DataSourceContext context, int menuId );

    XMLDocument getMenuBranch( DataSourceContext context, int menuItem, boolean topLevel, int startLevel, int levels );

    XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents );

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     * @return An XML Document with all information about the sub menus.
     */
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels );

    /**
     * Get a list of category forming a path to a category.
     *
     * @param context          the Vertical Site context
     * @param categoryKey      a category key
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return category xml
     */
    public XMLDocument getSuperCategoryNames( DataSourceContext context, int categoryKey, boolean withContentCount,
                                              boolean includeCategory );

    /**
     * Return index values for a specified path.
     *
     * @param context              the Vertical Site Context
     * @param path                 path to an indexed value
     * @param categories           one or more category keys
     * @param includeSubCategories include sub categories
     * @param contentTypes         filter by one or more content types
     * @param index                start from this index
     * @param count                maximum number of index values to get
     * @param distinct             return only distinct index values
     * @param order="info"
     * @return index values xml
     */
    public XMLDocument getIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                       int[] contentTypes, int index, int count, boolean distinct, String order );

    /**
     * Return aggregated index values for a specified path.
     *
     * @param context              the Vertical Site Context
     * @param path                 path to an indexed value
     * @param categories           one or more category keys
     * @param includeSubCategories include sub categories
     * @param contentTypes         filter by one or more content types
     * @return index values xml
     */
    public XMLDocument getAggregatedIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                                 int[] contentTypes );

    /**
     * Return content by category.
     *
     * @param context              the Vertical Site context
     * @param query                a search query (refer to the Administrator Guide for the syntax)
     * @param categories           one or more categories to search in
     * @param includeSubCategories include sub-categories of the categories before
     * @param orderBy              an order by string (refer to the Administrator Guide for the syntax)
     * @param index                start from this index
     * @param count                maximum number of contents to get
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    if true, include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    if true, include the current user's access rights to the content
     * @param contentTypes         filter by zero or more content types
     * @return contents xml
     */
    public XMLDocument getContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                             String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                             int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                             boolean includeUserRights, int[] contentTypes );

    /**
     * Return content by category for the logged in user.
     *
     * @param context              the Vertical Site context
     * @param query                a search query (refer to the Administrator Guide for the syntax)
     * @param categories           one or more categories to search in
     * @param includeSubCategories include sub-categories of the categories before
     * @param orderBy              an order by string (refer to the Administrator Guide for the syntax)
     * @param index                start from this index
     * @param count                maximum number of contents to get
     * @param titlesOnly           if true, return only content titles
     * @param parentLevel          the level of parents to include
     * @param childrenLevel        the level of children to include
     * @param parentChildrenLevel  the level of children for parents to include
     * @param relatedTitlesOnly    if true, return only related content titles
     * @param includeTotalCount    if true, include total count of contents returned excluding fromIndex and count
     * @param includeUserRights    if true, include the current user's access rights to the content
     * @param contentTypes         filter by zero or more content types
     * @return contents xml
     */
    public XMLDocument getMyContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                               String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                               int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                               boolean includeUserRights, int[] contentTypes );

    /**
     * Get a sub tree of categories.
     *
     * @param context          the Vertical Site context
     * @param superCategoryKey root category of the category sub tree
     * @param level            how many sub tree levels to return, 0 is all
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return categories xml
     */
    public XMLDocument getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                      boolean includeCategory );

    /**
     * Get a category tree specified by a level indicator and a super-category key. The super-category may be included. If levels is
     * positive, returns categories up to and including this level. A value of 0 returns all categories.
     *
     * @param context      the Vertical Site context
     * @param key          a super-section key
     * @param levels       if > 0, return up to and including this level. if 0, return all
     * @param topLevel     include the super-section in the results
     * @param details      if true, return all category details
     * @param catCount     if true, include category count
     * @param contentCount if true, include content count
     * @return categories xml
     */
    public XMLDocument getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                      boolean contentCount );

    public XMLDocument getUserstore( final DataSourceContext context, final String userstore );

    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch );
}
