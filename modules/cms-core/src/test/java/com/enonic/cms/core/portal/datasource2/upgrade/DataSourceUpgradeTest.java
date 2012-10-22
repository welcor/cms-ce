package com.enonic.cms.core.portal.datasource2.upgrade;

import java.net.URL;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceUpgradeTest
{
    @Test
    public void testUpgrade()
        throws Exception
    {
        testUpgrade( "none" );
        testUpgrade( "simple" );
        testUpgrade( "lowercase" );
        testUpgrade( "override" );
    }

    @Test
    public void testUpgrade_util()
        throws Exception
    {
        testUpgrade( "util/getCalendar" );
        testUpgrade( "util/getCountries" );
        testUpgrade( "util/getFormattedDate" );
        testUpgrade( "util/getLocales" );
        testUpgrade( "util/getTimeZones" );
        testUpgrade( "util/getUrlAsText" );
        testUpgrade( "util/getUrlAsXml" );
        testUpgrade( "util/getUserStore" );
    }

    @Test
    public void testUpgrade_preference()
        throws Exception
    {
        testUpgrade( "preference/getPreferences" );
    }

    @Test
    public void testUpgrade_menu()
        throws Exception
    {
        testUpgrade( "menu/getMenuData" );
        testUpgrade( "menu/getMenu" );
        testUpgrade( "menu/getMenuBranch" );
        testUpgrade( "menu/getSubMenu" );
        testUpgrade( "menu/getMenuItem" );
    }

    @Test
    public void testUpgrade_content()
        throws Exception
    {
        testUpgrade( "content/getCategories" );
        testUpgrade( "content/getContent" );
        testUpgrade( "content/getContentByCategory" );
        testUpgrade( "content/getContentByQuery" );
        testUpgrade( "content/getContentBySection" );
        testUpgrade( "content/getContentVersion" );
        testUpgrade( "content/getRandomContentByCategory" );
        testUpgrade( "content/getRandomContentBySection" );
        testUpgrade( "content/getRelatedContent" );
    }

    @Test
    public void testUpgrade_legacy()
        throws Exception
    {
        testUpgrade( "legacy/getSuperCategoryNames" );
        testUpgrade( "legacy/getIndexValues" );
        testUpgrade( "legacy/getAggregatedIndexValues" );
        testUpgrade( "legacy/getMyContentByCategory" );
        testUpgrade( "legacy/findContentByCategory" );
        testUpgrade( "legacy/getRandomContent" );
    }

    @Test
    public void testUpgrade_extension()
        throws Exception
    {
        testUpgrade( "extension/extension1" );
        testUpgrade( "extension/extension2" );
        testUpgrade( "extension/extension3" );
    }

    @Test
    public void testUpgrade_context()
        throws Exception
    {
        testUpgrade( "context/all" );
        testUpgrade( "context/cookie" );
        testUpgrade( "context/http" );
        testUpgrade( "context/session" );
        testUpgrade( "context/mixed" );
    }

    private void testUpgrade( final String name )
        throws Exception
    {
        testUpgrade( name + ".xml", name + "_result.xml" );
    }

    private void testUpgrade( final String source, final String result )
        throws Exception
    {
        final Document sourceDoc = readDoc( source );
        final Document resultDoc = readDoc( result );
        final Document transformedDoc = new DataSourceUpgrade().upgrade( sourceDoc );

        final String resultStr = toString( resultDoc );
        final String transformedStr = toString( transformedDoc );

        assertEquals( resultStr, transformedStr );
    }

    private Document readDoc( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name );
        assertNotNull( "Document [" + name + "] not found", url );

        final SAXBuilder builder = new SAXBuilder();
        return builder.build( url );
    }

    private String toString( final Document doc )
        throws Exception
    {
        final XMLOutputter out = new XMLOutputter();
        out.setFormat( Format.getPrettyFormat() );
        return out.outputString( doc );
    }
}
