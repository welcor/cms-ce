/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.extension;

import java.util.Arrays;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;

public class InvokeExtensionHandlerTest
    extends AbstractDataSourceHandlerTest<InvokeExtensionHandler>
{
    public class Extension1
    {
        // Return the document as it is
        public Document method1()
        {
            return new Document( new Element( "dummy" ) );
        }

        // Return the document as jdom document
        public org.w3c.dom.Document method2()
        {
            return XMLTool.createDocument( "dummy" );
        }

        // Return <value>dummy</value>
        public String method3()
        {
            return "dummy";
        }

        // Return <value>3</value>
        public int method4()
        {
            return 3;
        }

        // Return <value>en</value>
        public Object method5()
        {
            return Locale.ENGLISH;
        }
    }

    public class Extension2
    {
        public String method1()
        {
            return "method1";
        }

        public String method1( final int param1 )
        {
            return "method1: " + param1;
        }

        public String method1( final int param1, final String param2 )
        {
            return "method1: " + param1 + ", " + param2;
        }

        public String method2( final int[] param1 )
        {
            return "method2: " + Arrays.toString( param1 );
        }

        public String method3( final boolean param1 )
        {
            return "method3: " + param1;
        }

        public String method4( final int param1, final String param2 )
        {
            return "method4: " + param1 + ", " + param2;
        }

        public String method4( final int param1, final int param2 )
        {
            return "method4: " + param1 + " - " + param2;
        }
    }

    public InvokeExtensionHandlerTest()
    {
        super( InvokeExtensionHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        final FunctionLibrary lib1 = new FunctionLibrary();
        lib1.setName( "lib1" );
        lib1.setTarget( new Extension1() );
        lib1.setTargetClass( Extension1.class );

        final FunctionLibrary lib2 = new FunctionLibrary();
        lib2.setName( "lib2" );
        lib2.setTarget( new Extension2() );
        lib2.setTargetClass( Extension2.class );

        final ExtensionSet extensions = Mockito.mock( ExtensionSet.class );
        Mockito.when( extensions.findFunctionLibrary( "lib1" ) ).thenReturn( lib1 );
        Mockito.when( extensions.findFunctionLibrary( "lib2" ) ).thenReturn( lib2 );

        final PluginManager manager = Mockito.mock( PluginManager.class );
        Mockito.when( manager.getExtensions() ).thenReturn( extensions );

        this.handler.setPluginManager( manager );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_no_lib()
        throws Exception
    {
        this.request.addParam( "name", "other.noSuchMethod" );
        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_no_method()
        throws Exception
    {
        this.request.addParam( "name", "lib1.noSuchMethod" );
        this.handler.handle( this.request );
    }

    @Test
    public void testHandler_extension_returns_jdom_document()
        throws Exception
    {
        this.request.addParam( "name", "lib1.method1" );

        this.testHandle( "invokeExtension_document_result" );
    }

    @Test
    public void testHandler_extension_returns_w3c_document()
        throws Exception
    {
        this.request.addParam( "name", "lib1.method2" );

        this.testHandle( "invokeExtension_document_result" );
    }

    @Test
    public void testHandler_extension_returns_string()
        throws Exception
    {
        this.request.addParam( "name", "lib1.method3" );

        this.testHandle( "invokeExtension_string_result" );
    }

    @Test
    public void testHandler_extension_returns_int()
        throws Exception
    {
        this.request.addParam( "name", "lib1.method4" );

        this.testHandle( "invokeExtension_int_result" );
    }

    @Test
    public void testHandler_extension_returns_object()
        throws Exception
    {
        this.request.addParam( "name", "lib1.method5" );

        this.testHandle( "invokeExtension_object_result" );
    }

    @Test
    public void testHandler_extension_no_parameters()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method1" );

        this.testHandle( "invokeExtension_no_parameters" );
    }

    @Test
    public void testHandler_extension_one_parameter()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method1" );
        this.request.addParam( "param1", "33" );

        this.testHandle( "invokeExtension_one_parameter" );
    }

    @Test
    public void testHandler_extension_two_parameters()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method1" );
        this.request.addParam( "param1", "33" );
        this.request.addParam( "param2", "text" );

        this.testHandle( "invokeExtension_two_parameters" );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_extension_too_many_params()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method1" );
        this.request.addParam( "param1", "33" );
        this.request.addParam( "param2", "text" );
        this.request.addParam( "param3", "one-too-many" );

        this.handler.handle( this.request );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_extension_non_unique_method()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method4" );
        this.request.addParam( "param1", "33" );
        this.request.addParam( "param2", "text" );

        this.handler.handle( this.request );
    }

    @Test
    public void testHandler_extension_array_parameters()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method2" );
        this.request.addParam( "param1", "1,2,3,5,8,13,21,34" );

        this.testHandle( "invokeExtension_array_parameter" );
    }

    @Test
    public void testHandler_extension_boolean_parameter()
        throws Exception
    {
        this.request.addParam( "name", "lib2.method3" );
        this.request.addParam( "param1", "true" );

        this.testHandle( "invokeExtension_boolean_parameter" );
    }

}
