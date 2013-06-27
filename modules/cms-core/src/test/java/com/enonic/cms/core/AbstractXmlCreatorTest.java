/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.transform.JDOMSource;
import org.junit.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import junit.framework.TestCase;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public abstract class AbstractXmlCreatorTest
    extends TestCase
{

    protected String getFormattedXmlString( Document doc )
    {
        return JDOMUtil.prettyPrintDocument( doc, "  ", true );
    }

    protected String getFormattedXmlString( XMLDocument doc )
    {
        return getFormattedXmlString( doc.getAsJDOMDocument() );
    }

    protected String getXml( String path )
        throws JDOMException, IOException
    {
        Resource resource = new ClassPathResource( path );
        final Document document = JDOMUtil.parseDocument( resource.getInputStream() );
        XMLDocument xmlDocument = XMLDocumentFactory.create( document );
        return getFormattedXmlString( xmlDocument.getAsJDOMDocument() );
    }

    protected ContentTypeEntity createContentType( String key, String name )
    {

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( Integer.valueOf( key ) );
        contentType.setName( name );
        return contentType;
    }

    protected ContentEntity createContent( String key )
    {

        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );
        return content;
    }

    // TODO : refactoring required. these method are copied from AbstractPersistContentTest
    protected void assertXPathEquals( String xpathString, Document doc, String[] expectedValues )
    {
        try
        {
            XPathEvaluator xpathEvaluator = new XPathEvaluator();
            XPathExpression expr = xpathEvaluator.createExpression( xpathString );

            final JDOMSource docAsDomSource = new JDOMSource( doc );

            List nodes = expr.evaluate( docAsDomSource );

            if ( nodes.size() != expectedValues.length )
            {
                Assert.fail( "expected " + expectedValues.length + " values at xpath" );
            }

            for ( int i = 0; i < expectedValues.length; i++ )
            {
                Object node = nodes.get( i );
                if ( node instanceof NodeInfo )
                {
                    NodeInfo nodeInfo = (NodeInfo) node;
                    Assert.assertEquals( xpathString, expectedValues[i], nodeInfo.getStringValue() );
                }
                else
                {
                    Assert.assertEquals( xpathString, expectedValues[i], node );
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    protected void assertXPathEquals( String xpathString, Document doc, String expectedValue )
    {
        // TODO: rename method to: assertSingleXPathValueEquals
        String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, expectedValue, actualValue );
    }
}