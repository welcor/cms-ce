/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.contenttype;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

import static org.junit.Assert.assertEquals;


/**
 *
 * default value supports :
 *
 * TextDataEntryConfig, TextAreaDataEntryConfig, UrlDataEntryConfig
 * RadioButtonDataEntryConfig, DropdownDataEntryConfig
 * XmlDataEntryConfig, HtmlAreaDataEntryConfig
 *
 */
public class InputConfigParserTest_ParserInputConfigElement
{
    private StringBuilder xml;

    private DataEntryConfig dataEntryConfig;

    @Before
    public void setUp()
        throws Exception
    {
        xml = new StringBuilder();
    }

    @Test
    public void testInputTextWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='text'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputTextWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='text'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>default text</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "default text", dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputUrlWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='url'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputUrlWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='url'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>default text</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "default text", dataEntryConfig.getDefaultValue() );
    }



    @Test
    public void testInputTextareaWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='textarea'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputTextareaWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='textarea'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>default text</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "default text", dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputDropdownWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='sex' required='false' type='dropdown'>".replace( '\'', '"' ) );
        xml.append( "  <display>Sex</display>" );
        xml.append( "  <xpath>contentdata/sex</xpath>" );
        xml.append( "  <options>" );
        xml.append( "    <option value='M'>Male</option>" );
        xml.append( "    <option value='F'>Female</option>" );
        xml.append( "  </options>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputDropdownWithDefault()
        throws Exception
    {
        xml.append( "<input name='sex' required='false' type='dropdown'>".replace( '\'', '"' ) );
        xml.append( "  <display>Sex</display>" );
        xml.append( "  <xpath>contentdata/sex</xpath>" );
        xml.append( "  <options>" );
        xml.append( "    <option value='M'>Male</option>" );
        xml.append( "    <option value='F'>Female</option>" );
        xml.append( "  </options>" );
        xml.append( "  <default>F</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "F", dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputRadioWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='sex' required='false' type='radiobutton'>".replace( '\'', '"' ) );
        xml.append( "  <display>Sex</display>" );
        xml.append( "  <xpath>contentdata/sex</xpath>" );
        xml.append( "  <options>" );
        xml.append( "    <option value='M'>Male</option>" );
        xml.append( "    <option value='F'>Female</option>" );
        xml.append( "  </options>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputRadioWithDefault()
        throws Exception
    {
        xml.append( "<input name='sex' required='false' type='radiobutton'>".replace( '\'', '"' ) );
        xml.append( "  <display>Sex</display>" );
        xml.append( "  <xpath>contentdata/sex</xpath>" );
        xml.append( "  <options>" );
        xml.append( "    <option value='M'>Male</option>" );
        xml.append( "    <option value='F'>Female</option>" );
        xml.append( "  </options>" );
        xml.append( "  <default>F</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "F", dataEntryConfig.getDefaultValue() );
    }


    @Test
    public void testInputHtmlareaWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='htmlarea'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputHtmlareaWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='htmlarea'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>default text</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputHtmlarea2WithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='htmlarea'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default><html>html</html></default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "<html>html</html>", dataEntryConfig.getDefaultValue() );
    }


    @Test
    public void testInputXmlWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='xml'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputXmlWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='xml'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>xml</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputXml2WithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='xml'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default><xml/></default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( "<xml />", dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputDateWithoutDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='date'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    @Test
    public void testInputDateWithDefault()
        throws Exception
    {
        xml.append( "<input name='heading' required='true' type='date'>".replace( '\'', '"' ) );
        xml.append( "  <display>Heading</display>" );
        xml.append( "  <xpath>contentdata/heading</xpath>" );
        xml.append( "  <default>default text</default>" );
        xml.append( "</input>" );

        executeXMLParsing();

        assertEquals( null, dataEntryConfig.getDefaultValue() );
    }

    private void executeXMLParsing()
        throws IOException, JDOMException
    {
        InputConfigParser inputConfigParser = new InputConfigParser( 0 );
        final Document doc = JDOMUtil.parseDocument( xml.toString() );
        dataEntryConfig = inputConfigParser.
            parserInputConfigElement( doc.getRootElement() );
    }
}
