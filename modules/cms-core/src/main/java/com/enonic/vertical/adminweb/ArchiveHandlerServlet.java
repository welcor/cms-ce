/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.filters.UnitFilter;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.DeploymentPathResolver;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.DeleteCategoryCommand;
import com.enonic.cms.core.content.category.ModifyCategoryACLCommand;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.category.SynchronizeCategoryACLCommand;
import com.enonic.cms.core.content.category.UpdateCategoryCommand;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;

public class ArchiveHandlerServlet
    extends AdminHandlerBaseServlet
{

    protected StoreNewCategoryCommand createStoreNewCategoryCommand( User user, ExtendedMap formItems )
    {
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setCreator( user.getKey() );
        command.setName( formItems.getString( "name" ) );
        command.setAutoApprove( formItems.getBoolean( "autoApprove" ) );

        if ( formItems.containsKey( "categorycontenttypekey" ) )
        {
            command.setContentType( new ContentTypeKey( formItems.getString( "categorycontenttypekey" ) ) );
        }
        if ( formItems.containsKey( "supercategorykey" ) )
        {
            command.setParentCategory( new CategoryKey( formItems.getString( "supercategorykey" ) ) );
        }
        if ( formItems.containsKey( "description" ) )
        {
            command.setDescription( formItems.getString( "description" ) );
        }
        if ( formItems.containsKey( "languagekey" ) )
        {
            command.setLanguage( new LanguageKey( formItems.getString( "languagekey" ) ) );
        }
        String[] contentTypeKeys = formItems.getStringArray( "contenttypekey" );
        for ( String contentTypeKey : contentTypeKeys )
        {
            command.addAllowedContentType( new ContentTypeKey( contentTypeKey ) );
        }

        List<CategoryAccessControl> accessRights = parseCategoryAccessRights( formItems );
        command.addAccessRights( accessRights );
        return command;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String subop = formItems.getString( "subop", "" );
        String contentTypeString = formItems.getString( "contenttypestring", "" );

        if ( !"browse".equals( subop ) )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, ContentBaseHandlerServlet.getPopupCookieName( contentTypeString ), "-1",
                                  ContentBaseHandlerServlet.COOKIE_TIMEOUT, deploymentPath );
        }

        UnitFilter uf = new UnitFilter( user );
        Document doc = admin.getUnitNamesXML( uf ).getAsDOMDocument();

        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "contenttypestring", contentTypeString );
        xslParams.put( "page", formItems.getString( "page" ) );
        xslParams.put( "subop", subop );
        xslParams.put( "fieldname", formItems.getString( "fieldname", "" ) );
        xslParams.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        xslParams.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        xslParams.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );
        xslParams.put( "contenthandler", formItems.getString( "contenthandler", "" ) );
        if ( formItems.containsKey( "reload" ) )
        {
            xslParams.put( "reload", formItems.getString( "reload" ) );
        }
        addCommonParameters( admin, user, request, xslParams, -1, -1 );

        addAccessLevelParameters( user, xslParams );

        transformXML( request, response, doc, "repository_browse.xsl", xslParams );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        ExtendedMap xslParams = new ExtendedMap();

        Document doc;
        if ( !formItems.containsKey( "categorykey" ) )
        {
            doc = XMLTool.createDocument( "categories" );

            Document xmlDefaultAC = admin.getDefaultAccessRights( user, AccessRight.CATEGORY, -1 ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, xmlDefaultAC, true );
        }
        else
        {
            int categoryKey = formItems.getInt( "categorykey" );
            doc = admin.getCategory( user, categoryKey ).getAsDOMDocument();
            int categoryCount = admin.getContentCount( categoryKey, false );
            Element categoryElem = XMLTool.getElement( doc.getDocumentElement(), "category" );
            categoryElem.setAttribute( "contentcount", String.valueOf( categoryCount ) );

            int unitKey = formItems.getInt( "key" );
            Document unitXML = admin.getUnit( unitKey ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, unitXML, false );
        }

        Document xmlLanguages = admin.getLanguages().getAsDOMDocument();
        XMLTool.mergeDocuments( doc, xmlLanguages, true );

        // Get content types for this site
        XMLTool.mergeDocuments( doc, admin.getContentTypes( false ).getAsDOMDocument(), true );

        xslParams.put( "page", formItems.getString( "page" ) );

        if ( !formItems.containsKey( "categorykey" ) )
        {
            xslParams.put( "create", "1" );
        }

        if ( formItems.containsKey( "returnpage" ) )
        {
            xslParams.put( "returnpage", formItems.get( "returnpage" ) );
        }

        if ( formItems.containsKey( "minoccurrence" ) )
        {
            xslParams.put( "minoccurrence", formItems.get( "minoccurrence" ) );
        }

        if ( formItems.containsKey( "maxoccurrence" ) )
        {
            xslParams.put( "maxoccurrence", formItems.get( "maxoccurrence" ) );
        }

        addAccessLevelParameters( user, xslParams );
        addCommonParameters( admin, user, request, xslParams, -1, -1 );

        transformXML( request, response, doc, "archive_form.xsl", xslParams );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {
        final UserEntity user = securityService.getLoggedInAdminConsoleUserAsEntity();

        final StoreNewCategoryCommand command = createStoreNewCategoryCommand( user, formItems );
        final CategoryKey categoryKey = categoryService.storeNewCategory( command );
        final CategoryEntity category = categoryDao.findByKey( categoryKey );

        formItems.put( "selectedunitkey", category.getUnit().getKey().toString() );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        final User user = securityService.getLoggedInAdminConsoleUser();

        final UpdateCategoryCommand updateCategoryCommand = new UpdateCategoryCommand();

        final CategoryKey categoryKey = new CategoryKey( formItems.getInt( "key" ) );
        final String categorycontenttypekeyAsString = formItems.getString( "categorycontenttypekey", null );
        final String autoApproveAsString = formItems.getString( "autoApprove", null );
        final String name = formItems.getString( "name", null );
        final String description = formItems.getString( "description", null );
        final LanguageKey language = new LanguageKey( formItems.getString( "languagekey" ) );
        for ( String contentTypeKey : formItems.getStringArray( "contenttypekey" ) )
        {
            updateCategoryCommand.addAllowedContentType( new ContentTypeKey( contentTypeKey ) );
        }

        updateCategoryCommand.setUpdater( user.getKey() );
        updateCategoryCommand.setCategory( categoryKey );
        if ( StringUtils.isNotEmpty( categorycontenttypekeyAsString ) )
        {
            updateCategoryCommand.setContentType( new ContentTypeKey( categorycontenttypekeyAsString ) );
        }
        updateCategoryCommand.setName( name );
        updateCategoryCommand.setDescription( description );
        updateCategoryCommand.setLanguage( language );
        if ( StringUtils.isNotEmpty( autoApproveAsString ) )
        {
            updateCategoryCommand.setAutoApprove( Boolean.valueOf( autoApproveAsString ) );
        }
        categoryService.updateCategory( updateCategoryCommand );

        // Oppdaterer kategorien med rettigheter bare hvis brukeren ikke har valgt å propagere
        if ( formItems.containsKey( "updateaccessrights" ) && !formItems.getString( "propagate", "" ).equals( "true" ) )
        {
            SynchronizeCategoryACLCommand synchronizeCategoryACLCommand = new SynchronizeCategoryACLCommand();
            synchronizeCategoryACLCommand.setUpdater( user.getKey() );
            synchronizeCategoryACLCommand.setCategory( categoryKey );
            synchronizeCategoryACLCommand.addAccessControlList( parseCategoryAccessRights( formItems ) );
            categoryService.synchronizeCategoryACLInNewTX( synchronizeCategoryACLCommand );
        }

        // Redirect to propagate page
        if ( "true".equals( formItems.getString( "propagate" ) ) )
        {
            handlerPropagateAccessRightsPage( request, response, session, admin, formItems );
        }
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "op", "browse" );
            if ( formItems.containsKey( "returnpage" ) )
            {
                queryParams.put( "page", formItems.get( "returnpage" ) );
            }
            else
            {
                int cctk = formItems.getInt( "categorycontenttypekey", -1 );
                if ( cctk > -1 )
                {
                    queryParams.put( "page", cctk + 999 );
                }
                else
                {
                    queryParams.put( "page", "991" );
                }
            }

            queryParams.put( "cat", String.valueOf( categoryKey ) );
            queryParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
            queryParams.put( "reload", "true" );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    private void handlerPropagateAccessRights( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                               AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        final User user = securityService.getLoggedInAdminConsoleUser();
        final CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );

        // Propagate
        final String subop = formItems.getString( "subop", "" );
        if ( "propagate".equals( subop ) )
        {
            final String includeContents = formItems.getString( "includecontents", "off" );
            final String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            if ( "on".equals( applyOnlyChanges ) )
            {
                final ModifyCategoryACLCommand modifyCategoryACLCommand = new ModifyCategoryACLCommand();
                modifyCategoryACLCommand.setUpdater( user.getKey() );

                for ( Object o : formItems.keySet() )
                {
                    final String paramName = (String) o;
                    if ( paramName.startsWith( "arc[key=" ) )
                    {
                        final ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        final String paramValue = formItems.getString( paramName );
                        final ExtendedMap categoryAccessRight = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );
                        final String diffinfo = categoryAccessRight.getString( "diffinfo" );

                        if ( "removed".equals( diffinfo ) )
                        {
                            modifyCategoryACLCommand.addToBeRemoved( new GroupKey( paramsInName.getString( "key" ) ) );
                        }
                        else if ( "added".equals( diffinfo ) )
                        {
                            CategoryAccessControl categoryAccessControl = parseCategoryAccessControl( categoryAccessRight );
                            categoryAccessControl.setGroupKey( new GroupKey( paramsInName.getString( "key" ) ) );
                            modifyCategoryACLCommand.addToBeAdded( categoryAccessControl );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            CategoryAccessControl categoryAccessControl = parseCategoryAccessControl( categoryAccessRight );
                            categoryAccessControl.setGroupKey( new GroupKey( paramsInName.getString( "key" ) ) );
                            modifyCategoryACLCommand.addToBeModified( categoryAccessControl );
                        }
                    }
                }

                // Run through each (selected) category...
                for ( Object o : formItems.keySet() )
                {
                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {
                        final ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        final CategoryKey curCategoryKey = new CategoryKey( paramsInName.getString( "key" ) );

                        modifyCategoryACLCommand.addCategory( curCategoryKey );

                        if ( "on".equals( includeContents ) )
                        {
                            modifyCategoryACLCommand.includeContent();
                        }
                    }
                }

                modifyCategoryACLCommand.executeInBatches( categoryService, contentDao );
            }
            // Apply accessright as whole
            else
            {
                //("applying as whole");
                final List<CategoryAccessControl> accessRights = parseCategoryAccessRights( formItems );
                final SynchronizeCategoryACLCommand synchronizeCategoryACLCommand = new SynchronizeCategoryACLCommand();
                synchronizeCategoryACLCommand.setUpdater( user.getKey() );
                synchronizeCategoryACLCommand.addAccessControlList( accessRights );

                // Run through each (selected) category...
                for ( Object o : formItems.keySet() )
                {
                    final String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {
                        final ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        final CategoryKey curCategoryKey = new CategoryKey( paramsInName.getString( "key" ) );

                        // Apply on current category
                        synchronizeCategoryACLCommand.addCategory( curCategoryKey );

                        // Apply on contents in current category too...
                        if ( "on".equals( includeContents ) )
                        {
                            synchronizeCategoryACLCommand.includeContent();
                        }
                    }
                }
                synchronizeCategoryACLCommand.executeInBatches( categoryService, contentDao );
            }
        }
        // Ikke propager, bare lagre accessrights p� valgte categori
        else
        {
            final List<CategoryAccessControl> accessRights = parseCategoryAccessRights( formItems );
            final SynchronizeCategoryACLCommand synchronizeCategoryACLCommand = new SynchronizeCategoryACLCommand();
            synchronizeCategoryACLCommand.setUpdater( user.getKey() );
            synchronizeCategoryACLCommand.setCategory( categoryKey );
            synchronizeCategoryACLCommand.addAccessControlList( accessRights );
            categoryService.synchronizeCategoryACLInNewTX( synchronizeCategoryACLCommand );
        }

        // Redirect
        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "op", "browse" );
        if ( formItems.containsKey( "returnpage" ) )
        {
            queryParams.put( "page", formItems.get( "returnpage" ) );
            queryParams.put( "cat", String.valueOf( categoryKey ) );
            queryParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
        }
        else
        {
            queryParams.put( "page", formItems.get( "page" ) );
        }
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void handlerPropagateAccessRightsPage( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                   AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int unitKey = formItems.getInt( "selectedunitkey", -1 );
        int categoryKey = formItems.getInt( "key", -1 );

        Document doc = XMLTool.createDocument( "data" );

        Document categories = admin.getCategoryMenu( user, categoryKey, null, true ).getAsDOMDocument();
        //Don't seam to be in use (JAM 27.10.2008)
        //Document categoryNames = XMLTool.domparse(admin.getSuperCategoryNames(user, categoryKey, false, true));
        Document changedAccessRights = buildChangedAccessRightsXML( formItems );
        Document currentAccessRights = XMLTool.domparse( buildAccessRightsXML( formItems ) );
        XMLTool.mergeDocuments( doc, categories, true );
        //XMLTool.mergeDocuments(doc, categoryNames, true);
        XMLTool.mergeDocuments( doc, changedAccessRights, true );
        XMLTool.mergeDocuments( doc, currentAccessRights, true );

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, unitKey, -1 );
        addAccessLevelParameters( user, parameters );
        parameters.putInt( "cat", categoryKey );
        parameters.put( "page", formItems.get( "page" ) );
        parameters.put( "contenttypekey", formItems.get( "contenttypekey", "" ) );
        parameters.putString( "categoryname", formItems.getString( "name", "" ) );

        transformXML( request, response, doc, "category_propagateaccessrights.xsl", parameters );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );

        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( user.getKey() );
        command.setCategoryKey( categoryKey );
        command.setIncludeContent( false );
        command.setRecursive( false );
        categoryService.deleteCategory( command );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "propagateaccessrights".equals( operation ) )
        {
            handlerPropagateAccessRights( request, response, session, admin, formItems );
        }
        else if ( "popup".equals( operation ) )
        {
            handlerPopup( request, response, admin, formItems );
        }
        else if ( "emptycategory".equals( operation ) )
        {
            handlerEmptyCategory( request, response, session, formItems );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    public boolean handlerPopup( HttpServletRequest request, HttpServletResponse response, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Display the frameset for selecting content:
        Document docDummy = XMLTool.createDocument( "foo" );

        String contentTypeString = null;
        if ( formItems.containsKey( "handler" ) )
        {
            String handler = formItems.getString( "handler" );
            int[] contentTypes = admin.getContentTypesByHandlerClass( handler );
            if ( contentTypes == null || contentTypes.length == 0 )
            {
                contentTypeString = "";
            }
            else
            {
                contentTypeString = StringUtil.mergeInts( contentTypes, "," );
            }

        }
        else if ( formItems.containsKey( "contenttypekey" ) || formItems.containsKey( "contenttypename" ) )
        {
            TIntArrayList contentTypes = new TIntArrayList();

            String[] contentTypeKeys = getArrayFormItem( formItems, "contenttypekey" );
            if ( contentTypeKeys != null )
            {
                for ( String contentTypeKey : contentTypeKeys )
                {
                    contentTypes.add( Integer.parseInt( contentTypeKey ) );
                }
            }

            String[] contentTypeNames = getArrayFormItem( formItems, "contenttypename" );
            if ( contentTypeNames != null )
            {
                for ( String contentTypeName : contentTypeNames )
                {
                    int contentTypeKey = admin.getContentTypeKeyByName( contentTypeName );
                    if ( contentTypeKey >= 0 )
                    {
                        contentTypes.add( contentTypeKey );
                    }
                }
            }

            contentTypeString = StringUtil.mergeInts( contentTypes.toArray(), "," );
        }

        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "page", formItems.getString( "page" ) );
        xslParams.put( "contenttypestring", contentTypeString );
        xslParams.put( "fieldname", formItems.getString( "fieldname", "" ) );
        xslParams.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        xslParams.put( "selectedunitkey", formItems.getString( "selectedunitkey", "" ) );
        xslParams.put( "cat", formItems.getString( "cat", null ) );
        xslParams.put( "subop", formItems.getString( "subop", "" ) );

        xslParams.put( "unitfiltercontenttype", formItems.getString( "unitfiltercontenttype", null ) );
        xslParams.put( "requirecategoryadmin", formItems.getString( "requirecategoryadmin", null ) );
        xslParams.put( "excludecategorykey", formItems.getString( "excludecategorykey", null ) );
        xslParams.put( "excludecategorykey_withchildren", formItems.getString( "excludecategorykey_withchildren", null ) );
        xslParams.put( "contenthandler", formItems.getString( "contenthandler", null ) );
        xslParams.put( "user-agent", request.getHeader( "user-agent" ) );
        xslParams.put( "minoccurrence", formItems.getString( "minoccurrence", null ) );
        xslParams.put( "maxoccurrence", formItems.getString( "maxoccurrence", null ) );

        transformXML( request, response, docDummy, "content_selector_frameset.xsl", xslParams );
        return true;
    }

    public void handlerEmptyCategory( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User oldUser = securityService.getLoggedInAdminConsoleUser();
        UserEntity user = securityService.getUser( oldUser );
        CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        contentService.deleteByCategory( user, category );
        String referer = request.getHeader( "referer" );
        URL url = new URL( referer );
        url.setParameter( "feedback", 8 );
        url.setParameter( "index", 0 );
        redirectClientToURL( url, response );
    }
}