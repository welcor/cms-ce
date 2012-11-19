/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.InvalidKeyException;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.GetContentExecutor;
import com.enonic.cms.core.content.GetContentResult;
import com.enonic.cms.core.content.GetContentXmlCreator;
import com.enonic.cms.core.content.GetRelatedContentExecutor;
import com.enonic.cms.core.content.GetRelatedContentResult;
import com.enonic.cms.core.content.GetRelatedContentXmlCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.CategoryAccessResolver;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.ContentByQueryQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.InvalidContentBySectionQueryException;
import com.enonic.cms.core.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.preference.PreferenceEntity;
import com.enonic.cms.core.preference.PreferenceKey;
import com.enonic.cms.core.preference.PreferenceScope;
import com.enonic.cms.core.preference.PreferenceScopeResolver;
import com.enonic.cms.core.preference.PreferenceService;
import com.enonic.cms.core.preference.PreferenceSpecification;
import com.enonic.cms.core.preference.PreferenceUniqueMatchResolver;
import com.enonic.cms.core.preference.PreferenceXmlCreator;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.search.result.FacetResultSetXmlCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreParser;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessResolver;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.menuitem.MenuItemXmlCreator;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Service("dataSourceService")
public final class DataSourceServiceImpl
    implements DataSourceService
{
    private ContentService contentService;

    private PreferenceService preferenceService;

    private PresentationEngine presentationEngine;

    @Autowired
    private ContentVersionDao contentVersionDao;

    private ContentDao contentDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private MenuItemDao menuItemDao;

    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreDao userStoreDao;

    private SitePropertiesService sitePropertiesService;

    private TimeService timeService;

    private UserStoreService userStoreService;

    private FacetResultSetXmlCreator facetResultSetXmlCreator = new FacetResultSetXmlCreator();

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContentByQuery( DataSourceContext context, String query, String orderBy, int index, int count,
                                          boolean includeData, int childrenLevel, int parentLevel, final String facetsDefinition )
    {
        final PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        final Date now = timeService.getNowAsDateTime().toDate();

        try
        {
            ContentByQueryQuery spec = new ContentByQueryQuery();
            spec.setQuery( query );
            spec.setOrderBy( orderBy );
            spec.setIndex( index );
            spec.setCount( count );
            spec.setFilterContentOnlineAt( now );
            spec.setUser( user );
            spec.setFacets( facetsDefinition );
            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( includeData );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setIncludeRelatedContentData( includeData );
            xmlCreator.setIncludeUserRightsInfo( false, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForPortal( false );
            xmlCreator.setIncludeAssignment( true );
            XMLDocument xml = xmlCreator.createContentsDocument( user, contents, relatedContents );

            addFacetResultSet( contents, xml );
            addDataTraceInfo( xml.getAsJDOMDocument() );
            return xml;
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private void addFacetResultSet( final ContentResultSet contents, final XMLDocument xml )
    {
        if ( contents.getFacetsResultSet() != null )
        {
            facetResultSetXmlCreator.addFacetResultXml( xml.getAsJDOMDocument(), contents.getFacetsResultSet() );
        }
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                   boolean includeData, int childrenLevel, int parentLevel, final String facets )
    {
        boolean includeUserRights = false;
        boolean categoryRecursive = false;
        return doGetContent( context, contentKeys, query, orderBy, index, count, parentLevel, childrenLevel, 0, includeData, includeData,
                             includeUserRights, null, categoryRecursive, null, facets );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
    {
        return doGetContentVersion( context, versionKeys, childrenLevel );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query, String orderBy,
                                          int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                          final boolean requireAll )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        return doGetRelatedContent( context, contentKeys, relation, query, orderBy, requireAll, index, count, parentLevel, childrenLevel,
                                    includeOwnerAndModifierData, includeData, includeCategoryData, includeData );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, String orderBy,
                                            int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                            final String facets )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetContentBySection( context, menuItemKeys, levels, query, orderBy, index, count, parentLevel, childrenLevel, 0,
                                      includeOwnerAndModifierData, includeData, includeCategoryData, includeData, includeUserRights, null,
                                      facets );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                  boolean includeData, int childrenLevel, int parentLevel )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetRandomContentBySection( context, menuItemKeys, levels, query, count, parentLevel, childrenLevel,
                                            includeOwnerAndModifierData, includeData, includeCategoryData, includeData, includeUserRights );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, String orderBy,
                                             int index, int count, boolean includeData, int childrenLevel, int parentLevel,
                                             final boolean filterOnUser, final String facets )
    {
        boolean includeOwnerAndModifierData = true;
        boolean includeCategoryData = true;
        boolean includeUserRights = false;
        return doGetContentByCategory( context, categoryKeys, levels, query, orderBy, index, count, childrenLevel, parentLevel, 0,
                                       includeOwnerAndModifierData, includeData, includeCategoryData, includeData, includeUserRights, null,
                                       filterOnUser, facets );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getRandomContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, int count,
                                                   boolean includeData, int childrenLevel, int parentLevel )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        Collection<CategoryKey> categoryKeySet = CategoryKey.convertToList( categoryKeys );

        final Date now = new Date();

        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        contentByCategoryQuery.setUser( user );
        contentByCategoryQuery.setCategoryKeyFilter( categoryKeySet, levels );
        contentByCategoryQuery.setIndex( 0 );
        contentByCategoryQuery.setQuery( query );
        contentByCategoryQuery.setCount( Integer.MAX_VALUE );
        contentByCategoryQuery.setFilterContentOnlineAt( now );

        ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
        }
        ContentResultSet randomContents = contents.createRandomizedResult( count );
        RelatedContentResultSet relatedContent;
        if ( parentLevel > 0 || childrenLevel > 0 )
        {
            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( randomContents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }
        }
        else
        {
            relatedContent = new RelatedContentResultSetImpl();
        }

        xmlCreator.setResultIndexing( 0, count );
        xmlCreator.setIncludeContentData( includeData );
        xmlCreator.setIncludeRelatedContentData( includeData );
        xmlCreator.setIncludeVersionsInfoForPortal( false );
        xmlCreator.setIncludeAssignment( true );

        XMLDocument doc = xmlCreator.createContentsDocument( user, randomContents, relatedContent );

        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    /**
     * @inheritDoc
     */
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getContent( DataSourceContext context, int[] contentKeys, int parentLevel, int childrenLevel,
                                   int parentChildrenLevel, boolean updateStatistics, boolean relatedTitlesOnly, boolean includeUserRights,
                                   int[] filterByCategories, boolean categoryRecursive, int[] filterByContentTypes )
    {
        boolean includeContentData = true;
        return doGetContent( context, contentKeys, "", "@key asc", 0, contentKeys.length, parentLevel, childrenLevel, parentChildrenLevel,
                             includeContentData, !relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                             filterByContentTypes, null );
    }


    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getMenu( DataSourceContext context, int menuKey, int tagItem, int levels )
    {
        if ( menuKey < 0 )
        {
            return SiteXmlCreator.createEmptyMenus();
        }
        SiteEntity site = siteDao.findByKey( new SiteKey( menuKey ) );

        if ( site == null )
        {
            return SiteXmlCreator.createEmptyMenus();
        }

        SiteXmlCreator siteXmlCreator =
            new SiteXmlCreator( new MenuItemAccessResolver( groupDao ), context.getPreviewContext().getMenuItemInPreviewOrNull() );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setUser( getUserEntity( context.getUser() ) );
        siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( new MenuItemKey( tagItem ) ) );
        siteXmlCreator.setMenuItemLevels( levels );

        return siteXmlCreator.createLegacyGetMenu( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getMenuData( DataSourceContext context, int menuId )
    {
        if ( menuId < 0 )
        {
            return SiteXmlCreator.createEmptyMenus();
        }
        SiteEntity site = siteDao.findByKey( menuId );

        if ( site == null )
        {
            return SiteXmlCreator.createEmptyMenus();
        }

        SiteXmlCreator siteXmlCreator =
            new SiteXmlCreator( new MenuItemAccessResolver( groupDao ), context.getPreviewContext().getMenuItemInPreviewOrNull() );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setIncludeDeviceClassResolverInfo( true );
        return siteXmlCreator.createLegacyGetMenuData( site, sitePropertiesService.getSiteProperties( site.getKey() ) );
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getMenuBranch( DataSourceContext context, int menuItemKey, boolean topLevel, int startLevel, int levels )
    {
        if ( menuItemKey < 0 )
        {
            return SiteXmlCreator.createEmptyMenuBranch();
        }

        MenuItemEntity menuItem = menuItemDao.findByKey( new MenuItemKey( menuItemKey ) );
        if ( menuItem == null )
        {
            return SiteXmlCreator.createEmptyMenuBranch();
        }

        SiteXmlCreator siteXmlCreator =
            new SiteXmlCreator( new MenuItemAccessResolver( groupDao ), context.getPreviewContext().getMenuItemInPreviewOrNull() );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );

        siteXmlCreator.setMenuItemInBranch( menuItem );
        siteXmlCreator.setActiveMenuItem( menuItem );
        siteXmlCreator.setMenuItemLevels( levels );
        siteXmlCreator.setBranchStartLevel( startLevel );
        siteXmlCreator.setIncludeTopLevel( topLevel );
        siteXmlCreator.setUser( context.getUser() );

        return siteXmlCreator.createLegacyGetMenuBranch( menuItem.getSite() );
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getMenuItem( DataSourceContext context, int key, boolean withParents )
    {
        MenuItemXMLCreatorSetting setting = new MenuItemXMLCreatorSetting();
        setting.user = getUserEntity( context.getUser() );
        setting.includeParents = withParents;
        MenuItemXmlCreator creator = new MenuItemXmlCreator( setting, new MenuItemAccessResolver( groupDao ),
                                                             context.getPreviewContext().getMenuItemInPreviewOrNull() );
        MenuItemEntity menuItem = menuItemDao.findByKey( new MenuItemKey( key ) );
        return creator.createLegacyGetMenuItem( menuItem );
    }

    /**
     * Returns the sub menu that is shown in the menu
     *
     * @param context Site context
     * @param key     Root menu item key
     * @param tagItem Menu item key to tag
     * @param levels  Number of levels to fetch
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getSubMenu( DataSourceContext context, int key, int tagItem, int levels )
    {
        if ( key < 0 )
        {
            return SiteXmlCreator.createEmptyMenuItems();
        }

        MenuItemEntity menuItem = menuItemDao.findByKey( new MenuItemKey( key ) );
        if ( menuItem == null )
        {
            return SiteXmlCreator.createEmptyMenuItems();
        }

        SiteXmlCreator siteXmlCreator =
            new SiteXmlCreator( new MenuItemAccessResolver( groupDao ), context.getPreviewContext().getMenuItemInPreviewOrNull() );
        siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
        siteXmlCreator.setUser( getUserEntity( context.getUser() ) );
        siteXmlCreator.setMenuItemInBranch( menuItem );
        siteXmlCreator.setMenuItemLevels( levels );
        if ( tagItem > -1 )
        {
            siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( new MenuItemKey( tagItem ) ) );
        }

        return siteXmlCreator.createLegacyGetSubMenu( menuItem.getSite() );
    }

    /**
     * Get a list of category forming a path to a category.
     *
     * @param context          the Vertical Site context
     * @param categoryKey      a category key
     * @param withContentCount if true, include content count for each category
     * @param includeCategory  if true, include the root category
     * @return category xml
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getSuperCategoryNames( DataSourceContext context, int categoryKey, boolean withContentCount,
                                              boolean includeCategory )
    {
        return presentationEngine.getSuperCategoryNames( categoryKey, withContentCount, includeCategory );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                       int[] contentTypes, int index, int count, boolean distinct, String order )
    {
        UserEntity user = getUserEntity( context.getUser() );
        boolean descOrder = order != null && order.equalsIgnoreCase( "desc" );
        Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
        Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );
        return contentService.getIndexValues( user, path, categoryFilter, includeSubCategories, contentTypeFilter, index, count,
                                              descOrder );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getAggregatedIndexValues( DataSourceContext context, String path, int[] categories, boolean includeSubCategories,
                                                 int[] contentTypes )
    {
        UserEntity user = getUserEntity( context.getUser() );

        Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
        Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );

        return contentService.getAggregatedIndexValues( user, path, categoryFilter, includeSubCategories, contentTypeFilter );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getMyContentByCategory( DataSourceContext context, String query, int[] categories, boolean includeSubCategories,
                                               String orderBy, int index, int count, boolean titlesOnly, int childrenLevel, int parentLevel,
                                               int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeTotalCount,
                                               boolean includeUserRights, int[] contentTypes )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        if ( ( user != null ) && ( !user.isAnonymous() ) )
        {
            String ownerQuery = "owner/@key = '" + user.getKey() + "'";
            if ( ( query == null ) || ( query.trim().length() == 0 ) )
            {
                query = ownerQuery;
            }
            else
            {
                query = "(" + query + ") AND " + ownerQuery;
            }
        }
        else
        {
            count = 0;
        }

        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        XMLDocument doc;
        if ( count == 0 )
        {
            doc = xmlCreator.createEmptyDocument( "My content is not available for anonymous" );
        }
        else
        {
            Collection<CategoryKey> categoryFilter = CategoryKey.convertToList( categories );
            Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypes );
            final Date now = new Date();

            ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setCategoryKeyFilter( categoryFilter, includeSubCategories ? Integer.MAX_VALUE : 1 );
            contentByCategoryQuery.setQuery( query );
            contentByCategoryQuery.setOrderBy( orderBy );
            contentByCategoryQuery.setContentTypeFilter( contentTypeFilter );
            contentByCategoryQuery.setCount( count );
            contentByCategoryQuery.setIndex( index );
            contentByCategoryQuery.setFilterContentOnlineAt( now );

            ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
            relatedContentQuery.setIncludeOnlyMainVersions( true );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            xmlCreator.setIncludeContentData( !titlesOnly );
            xmlCreator.setIncludeRelatedContentData( !relatedTitlesOnly );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeVersionsInfoForPortal( false );
            xmlCreator.setIncludeAssignment( true );
            doc = xmlCreator.createContentsDocument( user, contents, relatedContents );
        }
        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getCategories( DataSourceContext context, int superCategoryKey, int level, boolean withContentCount,
                                      boolean includeCategory )
    {
        org.w3c.dom.Document doc =
            presentationEngine.getCategories( context.getUser(), superCategoryKey, level, includeCategory, true, true, withContentCount );

        DataSourceServiceCompabilityKeeper.fixCategoriesCompability( doc );
        return XMLDocumentFactory.create( doc );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getCategories( DataSourceContext context, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                      boolean contentCount )
    {
        return XMLDocumentFactory.create(
            presentationEngine.getCategories( context.getUser(), key, levels, topLevel, details, catCount, contentCount ) );
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getUserstore( final DataSourceContext context, final String userstore )
    {
        final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
        Document userstoreDoc;

        try
        {
            if ( Strings.isNullOrEmpty( userstore ) )
            {
                userstoreDoc = userStoreXmlCreator.createUserStoresDocument( userStoreService.getDefaultUserStore() );
            }
            else
            {
                UserStoreEntity userStore = new UserStoreParser( userStoreDao ).parseUserStore( userstore );
                if ( userStore != null )
                {
                    userstoreDoc = userStoreXmlCreator.createUserStoresDocument( userStore );
                }
                else
                {
                    userstoreDoc = userStoreXmlCreator.createUserStoreNotFoundDocument( userstore );
                }
            }
        }
        catch ( UserStoreNotFoundException e )
        {
            userstoreDoc = userStoreXmlCreator.createUserStoreNotFoundDocument( userstore );
        }

        return XMLDocumentFactory.create( userstoreDoc );
    }

    /**
     * @inheritDoc
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public XMLDocument getPreferences( DataSourceContext context, String scope, String wildCardKey, boolean uniqueMatch )
    {
        final UserEntity user = getUserEntity( context.getUser() );

        PreferenceSpecification spec = new PreferenceSpecification( user );
        if ( StringUtils.isEmpty( scope ) || "*".equals( scope ) )
        {
            spec.setPreferenceScopes( PreferenceScopeResolver.resolveAllScopes( context.getPortalInstanceKey(), context.getSiteKey() ) );
        }
        else
        {
            List<PreferenceScope> resolvedScopes =
                PreferenceScopeResolver.resolveScopes( scope, context.getPortalInstanceKey(), context.getSiteKey() );

            if ( resolvedScopes.isEmpty() )
            {
                return PreferenceXmlCreator.createEmptyPreferencesDocument( "Scope " + scope + " is not a valid scope list" );
            }

            spec.setPreferenceScopes( resolvedScopes );
        }
        spec.setWildCardBaseKey( wildCardKey );

        List<PreferenceEntity> preferences = this.preferenceService.getPreferences( spec );

        if ( uniqueMatch )
        {
            preferences = getUniqueMatches( preferences );
        }

        return PreferenceXmlCreator.createPreferencesDocument( preferences );
    }


    private List<PreferenceEntity> getUniqueMatches( List<PreferenceEntity> allPreferences )
    {

        List<PreferenceEntity> uniquePreferences = new ArrayList<PreferenceEntity>();

        PreferenceUniqueMatchResolver uniqueMatchResolver = new PreferenceUniqueMatchResolver();

        for ( PreferenceEntity preference : allPreferences )
        {
            uniqueMatchResolver.addPreferenceKeyIfHigherPriority( preference.getKey() );
        }

        List<PreferenceKey> uniqueKeys = uniqueMatchResolver.getUniquePreferenceKeys();

        for ( PreferenceEntity preference : allPreferences )
        {
            if ( uniqueKeys.contains( preference.getKey() ) )
            {
                uniquePreferences.add( preference );
            }
        }

        return uniquePreferences;
    }

    private XMLDocument doGetRelatedContent( DataSourceContext context, int[] contentKeys, int relation, String query, String orderBy,
                                             boolean requireAll, int index, int count, int parentLevel, int childrenLevel,
                                             boolean includeOwnerAndModifierData, boolean includeContentData, boolean includeCategoryData,
                                             boolean includeRelatedContentData )
    {
        XMLDocument xmlDocument = null;
        try
        {
            final UserEntity user = getUserEntity( context.getUser() );
            final List<ContentKey> contentFilter = ContentKey.convertToList( contentKeys );

            final GetRelatedContentExecutor getRelatedContentExecutor =
                new GetRelatedContentExecutor( contentService, timeService.getNowAsDateTime().toDate(), context.getPreviewContext() );
            getRelatedContentExecutor.user( user );
            getRelatedContentExecutor.requireAll( requireAll );
            getRelatedContentExecutor.relation( relation );
            getRelatedContentExecutor.query( query );
            getRelatedContentExecutor.orderBy( orderBy );
            getRelatedContentExecutor.index( index );
            getRelatedContentExecutor.count( count );
            getRelatedContentExecutor.childrenLevel( childrenLevel );
            getRelatedContentExecutor.parentLevel( parentLevel );
            getRelatedContentExecutor.parentChildrenLevel( 0 );
            if ( contentFilter != null )
            {
                getRelatedContentExecutor.contentFilter( contentFilter );
            }
            final GetRelatedContentResult result = getRelatedContentExecutor.execute();

            final GetRelatedContentXmlCreator getRelatedContentXmlCreator =
                new GetRelatedContentXmlCreator( new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );

            getRelatedContentXmlCreator.user( user );
            getRelatedContentXmlCreator.startingIndex( index );
            getRelatedContentXmlCreator.resultLength( count );
            getRelatedContentXmlCreator.includeContentsContentData( includeContentData );
            getRelatedContentXmlCreator.includeRelatedContentsContentData( includeRelatedContentData );
            getRelatedContentXmlCreator.includeOwnerAndModifierData( includeOwnerAndModifierData );
            getRelatedContentXmlCreator.includeCategoryData( includeCategoryData );
            xmlDocument = getRelatedContentXmlCreator.create( result );
        }
        catch ( InvalidKeyException e )
        {
            xmlDocument = new ContentXMLCreator().createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        finally
        {
            if ( xmlDocument != null )
            {
                addDataTraceInfo( xmlDocument.getAsJDOMDocument() );
            }
        }
        return xmlDocument;
    }

    private XMLDocument doGetContent( DataSourceContext context, int[] contentKeys, String query, String orderBy, int index, int count,
                                      int parentLevel, int childrenLevel, int parentChildrenLevel, boolean includeContentData,
                                      boolean includeRelatedContentData, boolean includeUserRights, int[] filterByCategories,
                                      boolean categoryRecursive, int[] filterByContentTypes, final String facets )
    {

        UserEntity user = getUserEntity( context.getUser() );

        GetContentExecutor executor =
            new GetContentExecutor( contentService, contentDao, userDao, timeService.getNowAsDateTime(), context.getPreviewContext() );
        try
        {
            executor.user( user.getKey() );
            executor.query( query );
            executor.orderBy( orderBy );
            executor.index( index );
            executor.count( count );
            executor.parentLevel( parentLevel );
            executor.childrenLevel( childrenLevel );
            executor.parentChildrenLevel( parentChildrenLevel );
            executor.contentFilter( ContentKey.convertToList( contentKeys ) );
            executor.categoryFilter( CategoryKey.convertToList( filterByCategories ), categoryRecursive ? Integer.MAX_VALUE : 1 );
            executor.contentTypeFilter( ContentTypeKey.convertToList( filterByContentTypes ) );
            executor.facets( facets );

            GetContentResult getContentResult = executor.execute();

            GetContentXmlCreator getContentXmlCreator =
                new GetContentXmlCreator( new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            getContentXmlCreator.user( user );
            getContentXmlCreator.startingIndex( index );
            getContentXmlCreator.resultLength( count );
            getContentXmlCreator.includeContentsContentData( includeContentData );
            getContentXmlCreator.includeRelatedContentsContentData( includeRelatedContentData );
            getContentXmlCreator.includeUserRights( includeUserRights );
            getContentXmlCreator.versionInfoStyle( GetContentXmlCreator.VersionInfoStyle.PORTAL );
            XMLDocument xml = getContentXmlCreator.create( getContentResult );

            addFacetResultSet( getContentResult.getContentResultSet(), xml );
            addDataTraceInfo( xml.getAsJDOMDocument() );
            return xml;
        }
        catch ( InvalidKeyException e )
        {
            ContentXMLCreator xmlCreator = new ContentXMLCreator();
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private XMLDocument doGetContentVersion( DataSourceContext context, int[] versionKeys, int childrenLevel )
    {
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        try
        {
            if ( versionKeys == null || versionKeys.length == 0 )
            {
                throw new IllegalArgumentException( "Missing one or more versionkeys" );
            }
            Date now = new Date();
            List<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>( versionKeys.length );
            UserEntity user = getUserEntity( context.getUser() );
            ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
            for ( int versionKey : versionKeys )
            {
                ContentVersionKey key = new ContentVersionKey( versionKey );
                ContentVersionEntity version = contentVersionDao.findByKey( key );
                if ( version == null )
                {
                    continue;
                }
                final boolean mainVersionOnline = version.getContent().isOnline( now );
                final boolean versionCheckOK = version.isApproved() || version.isArchived() || version.isSnapshot();
                final boolean accessCheckOK = contentAccessResolver.hasReadContentAccess( user, version.getContent() );

                if ( mainVersionOnline && versionCheckOK && accessCheckOK )
                {
                    versions.add( version );
                }
            }

            RelatedChildrenContentQuery spec = new RelatedChildrenContentQuery( now );
            spec.setChildrenLevel( childrenLevel );
            spec.setContentVersions( versions );
            spec.setUser( user );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( spec );

            xmlCreator.setIncludeVersionsInfoForPortal( true );
            xmlCreator.setIncludeAccessRightsInfo( true );
            xmlCreator.setIncludeUserRightsInfo( true, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeOwnerAndModifierData( true );
            xmlCreator.setIncludeContentData( true );
            xmlCreator.setIncludeCategoryData( true );
            xmlCreator.setIncludeAssignment( true );

            return xmlCreator.createContentVersionsDocument( user, versions, relatedContent );

        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private XMLDocument doGetContentByCategory( DataSourceContext context, int[] categoryKeys, int levels, String query, String orderBy,
                                                int index, int count, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                                boolean includeOwnerAndModifierData, boolean includeContentData,
                                                boolean includeCategoryData, boolean includeRelatedContentData, boolean includeUserRights,
                                                int[] contentTypes, final boolean filterOnUser, final String facets )
    {
        final PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );

        if ( filterOnUser )
        {
            query = applyUserFilterToQuery( query, user );
        }

        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        Date now = timeService.getNowAsDateTime().toDate();
        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        try
        {
            contentByCategoryQuery.setCategoryKeyFilter( CategoryKey.convertToList( categoryKeys ), levels );
            contentByCategoryQuery.setContentTypeFilter( ContentTypeKey.convertToList( contentTypes ) );
            contentByCategoryQuery.setUser( user );
            contentByCategoryQuery.setQuery( query );
            contentByCategoryQuery.setOrderBy( orderBy );
            contentByCategoryQuery.setCount( count );
            contentByCategoryQuery.setIndex( index );
            contentByCategoryQuery.setFilterContentOnlineAt( now );
            contentByCategoryQuery.setFacets( facets );

            ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContentQuery.setOnlineCheckDate( now );

            RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            }

            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
            xmlCreator.setIncludeContentData( includeContentData );
            xmlCreator.setIncludeCategoryData( includeCategoryData );
            xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setIncludeVersionsInfoForPortal( false );
            xmlCreator.setIncludeAssignment( true );

            XMLDocument doc = xmlCreator.createContentsDocument( user, contents, relatedContent );
            addFacetResultSet( contents, doc );
            addDataTraceInfo( doc.getAsJDOMDocument() );
            return doc;

        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
    }

    private String applyUserFilterToQuery( String query, final UserEntity user )
    {
        if ( ( user != null ) && ( !user.isAnonymous() ) )
        {
            String ownerQuery = "owner/@key = '" + user.getKey() + "'";
            if ( Strings.isNullOrEmpty( query ) )
            {
                query = ownerQuery;
            }
            else
            {
                query = "(" + query + ") AND " + ownerQuery;
            }
        }
        return query;
    }

    private XMLDocument doGetContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, String orderBy,
                                               int fromIndex, int count, int parentLevel, int childrenLevel, int parentChildrenLevel,
                                               boolean includeOwnerAndModifierData, boolean includeContentData, boolean includeCategoryData,
                                               boolean includeRelatedContentData, boolean includeUserRights, int[] filterByContentTypes,
                                               final String facets )
    {
        PreviewContext previewContext = context.getPreviewContext();
        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();

        ContentBySectionQuery spec = new ContentBySectionQuery();
        final Date now = new Date();

        try
        {
            spec.setMenuItemKeys( MenuItemKey.converToList( menuItemKeys ) );
            spec.setContentTypeFilter( ContentTypeKey.convertToList( filterByContentTypes ) );

            spec.setUser( user );
            //spec.setApprovedSectionContentOnly( true );
            spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
            spec.setLevels( levels );
            spec.setIndex( fromIndex );
            spec.setCount( count );
            spec.setQuery( query );
            spec.setOrderBy( orderBy );
            spec.setFilterContentOnlineAt( now );
            spec.setFacets( facets );

            ContentResultSet contents = contentService.queryContent( spec );

            xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
            xmlCreator.setIncludeContentData( includeContentData );
            xmlCreator.setIncludeCategoryData( includeCategoryData );
            xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setResultIndexing( fromIndex, count );
            xmlCreator.setIncludeVersionsInfoForPortal( false );
            xmlCreator.setIncludeAssignment( true );

            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContentQuery.setFilterContentOnlineAt( now );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            XMLDocument document = xmlCreator.createContentsDocument( user, contents, relatedContents );
            addFacetResultSet( contents, document );
            addDataTraceInfo( document.getAsJDOMDocument() );
            return document;
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        catch ( InvalidContentBySectionQueryException e )
        {
            return xmlCreator.createEmptyDocument( e.getMessage() );
        }
    }

    private XMLDocument doGetRandomContentBySection( DataSourceContext context, int[] menuItemKeys, int levels, String query, int count,
                                                     int parentLevel, int childrenLevel, boolean includeOwnerAndModifierData,
                                                     boolean includeContentData, boolean includeCategoryData,
                                                     boolean includeRelatedContentData, boolean includeUserRights )
    {
        PreviewContext previewContext = context.getPreviewContext();

        UserEntity user = getUserEntity( context.getUser() );
        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        final Date now = new Date();
        ContentBySectionQuery spec = new ContentBySectionQuery();
        try
        {
            spec.setMenuItemKeys( MenuItemKey.converToList( menuItemKeys ) );
        }
        catch ( Exception e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() );
        }
        spec.setUser( user );
        spec.setSectionFilterStatus( SectionFilterStatus.APPROVED_ONLY );
        spec.setLevels( levels );
        spec.setIndex( 0 );
        spec.setCount( Integer.MAX_VALUE );
        spec.setQuery( query );
        spec.setFilterContentOnlineAt( now );

        ContentResultSet contents = contentService.queryContent( spec );
        if ( previewContext.isPreviewingContent() )
        {
            contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
        }
        ContentResultSet randomContent = contents.createRandomizedResult( count );

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( randomContent );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( 0 );
        relatedContentQuery.setIncludeOnlyMainVersions( true );
        relatedContentQuery.setFilterContentOnlineAt( now );

        RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
        }

        xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
        xmlCreator.setIncludeContentData( includeContentData );
        xmlCreator.setIncludeCategoryData( includeCategoryData );
        xmlCreator.setIncludeRelatedContentData( includeRelatedContentData );
        xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        xmlCreator.setResultIndexing( 0, count );
        xmlCreator.setIncludeVersionsInfoForPortal( false );
        xmlCreator.setIncludeAssignment( true );

        XMLDocument doc = xmlCreator.createContentsDocument( user, randomContent, relatedContent );
        addDataTraceInfo( doc.getAsJDOMDocument() );
        return doc;
    }

    /**
     * Adds traceInfo to a content document before it's returned.
     *
     * @param doc A JDom document with all information that needs to be traced.
     */
    @SuppressWarnings("unchecked")
    private void addDataTraceInfo( Document doc )
    {
        DataTraceInfo traceInfo = RenderTrace.getCurrentDataTraceInfo();
        if ( traceInfo != null )
        {
            Element root = doc.getRootElement();
            List<Element> contentNodes = root.getChildren( "content" );
            for ( Element e : contentNodes )
            {
                Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                String title = firstChild.getText();
                traceInfo.addContentInfo( key, title );
            }
            Element relatedContentsNode = root.getChild( "relatedcontents" );

            if ( relatedContentsNode != null )
            {
                List<Element> relatedContentNodes = relatedContentsNode.getChildren( "content" );
                for ( Element e : relatedContentNodes )
                {
                    Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                    Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                    String title = firstChild.getText();
                    traceInfo.addRelatedContentInfo( key, title );
                }
            }
        }
    }

    private UserEntity getUserEntity( User user )
    {
        return userDao.findByKey( user.getKey() );
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setPresentationEngine( PresentationEngine presentationEngine )
    {
        this.presentationEngine = presentationEngine;
    }

    @Autowired
    public void setContentService( ContentService service )
    {
        this.contentService = service;
    }

    @Autowired
    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setUserStoreService( UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }
}