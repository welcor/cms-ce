package com.enonic.cms.core.portal.datasource2.upgrade;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.collect.Lists;

public final class DataSourceUpgrade
{
    private final List<MethodConverter> converters;

    public DataSourceUpgrade()
    {
        this.converters = Lists.newArrayList();

        addSimple( "getCalendar", "relative", "year", "month", "count", "includeWeeks", "includeDays", "language", "country" );
        addSimple( "getCountries", "countryCodes", "includeRegions" );
        addSimple( "getFormattedDate", "offset", "format", "language", "country" );
        addSimple( "getUrlAsText", "url", "encoding", "timeout" );
        addSimple( "getUrlAsXml", "url", "timeout" );
        addSimple( "getUserStore", "name" );

        addSimple( "getPreferences", "scope", "keyPattern", "uniqueMatch" );

        addSimple( "getMenuData", "menuKey" );
        addSimple( "getMenu", "menuKey", "menuItemKey", "levels" );
        addSimple( "getMenuBranch", "menuItemKey", "includeTopLevel", "startLevel", "levels" );
        addSimple( "getSubMenu", "menuItemKey", "tagItem", "levels" );
        addSimple( "getMenuItem", "menuItemKey", "withParents", "details" );

        addSimple( "getCategories", "categoryKey", "levels", "includeContentCount", "includeTopCategory" );
        addSimple( "getContent", "key", "query", "orderBy", "index", "count", "includeData", "childrenLevel", "parentLevel" );
        addSimple( "getContentByCategory", "categoryKeys", "levels", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                   "parentLevel" );
        addSimple( "getContentByQuery", "query", "orderBy", "index", "count", "includeData", "childrenLevel", "parentLevel" );
        addSimple( "getContentBySection", "key", "levels", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                   "parentLevel" );
        addSimple( "getContentVersion", "versionKeys", "childrenLevel" );
        addSimple( "getRandomContentByCategory", "category", "levels", "query", "count", "includeData", "childrenLevel", "parentLevel" );
        addSimple( "getRandomContentBySection", "key", "levels", "query", "count", "includeData", "childrenLevel", "parentLevel" );
        addSimple( "getRelatedContent", "key", "relation", "query", "orderBy", "index", "count", "includeData", "childrenLevel",
                   "parentLevel" );

        addSimple( "findContentByCategory", "search", "operator", "categories", "recursive", "orderBy", "index", "count", "titlesOnly",
                   "childrenLevel", "parentLevel", "parentChildrenLevel", "relatedTitlesOnly", "includeTotalCount", "includeUserRights",
                   "contentTypes" );
        addSimple( "getAggregatedIndexValues", "path", "categories", "recursive", "contentTypes" );
        addSimple( "getIndexValues", "path", "categories", "recursive", "contentTypes", "index", "count", "distinct", "order" );
        addSimple( "getMyContentByCategory", "query", "categories", "recursive", "orderBy", "index", "count", "titlesOnly", "childrenLevel",
                   "parentLevel", "parentChildrenLevel", "relatedTitlesOnly", "includeTotalCount", "includeUserRights", "contentTypes" );
        addSimple( "getRandomContent", "count", "categories", "recursive", "minPriority", "childrenLevel", "parentLevel",
                   "parentChildrenLevel" );
        addSimple( "getSuperCategoryNames", "category", "includeContentCount", "includeCurrent" );

        this.converters.add( new ExtensionMethodConverter() );
    }

    private void addSimple( final String name, final String... paramNames )
    {
        this.converters.add( new SimpleMethodConverter( name, paramNames ) );
    }

    public Document upgrade( final Document source )
        throws Exception
    {
        final Element result = convertDataSources( source.getRootElement() );
        return new Document( result );
    }

    private MethodConverter findConverter( final String methodName )
    {
        for ( final MethodConverter converter : this.converters )
        {
            if ( converter.canHandle( methodName ) )
            {
                return converter;
            }
        }

        return new FallbackMethodConverter();
    }

    private Element convertDataSources( final Element elem )
    {
        final Element result = new Element( "data-sources" );
        copyAttributeIfExists( elem, result, "result-element" );
        copyAttributeIfExists( elem, result, "httpcontext", "http-context" );
        copyAttributeIfExists( elem, result, "sessioncontext", "session-context" );
        copyAttributeIfExists( elem, result, "cookiecontext", "cookie-context" );

        for ( final Element child : JDOMDocumentHelper.findElements( elem, "dataSource" ) )
        {
            result.addContent( convertDataSource( child ) );
        }

        return result;
    }

    private Element convertDataSource( final Element elem )
    {
        final String methodName = JDOMDocumentHelper.getTextNode( JDOMDocumentHelper.findElement( elem, "methodName" ) );
        final MethodConverter converter = findConverter( methodName );
        final Element result = converter.convert( elem, methodName );

        copyAttributeIfExists( elem, result, "result-element" );
        copyAttributeIfExists( elem, result, "cache" );

        final String conditionAttr = elem.getAttributeValue( "condition" );
        if ( conditionAttr != null )
        {
            result.setAttribute( "condition", trimCondition( conditionAttr ) );
        }

        return result;
    }

    private void copyAttributeIfExists( final Element source, final Element target, final String name )
    {
        copyAttributeIfExists( source, target, name, name );
    }

    private void copyAttributeIfExists( final Element source, final Element target, final String name, final String newName )
    {
        final String value = source.getAttributeValue( name );
        if ( value != null )
        {
            target.setAttribute( newName, value );
        }
    }

    private String trimCondition( final String condition )
    {
        String str = condition.trim();
        if ( str.startsWith( "${" ) )
        {
            str = str.substring( 2 );
        }

        if ( str.endsWith( "}" ) )
        {
            str = str.substring( 0, str.length() - 1 );
        }

        return str.trim();
    }
}
