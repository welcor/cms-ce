/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.filter;

import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import junit.framework.TestCase;

public class ASCIICharactersFilterTest
        extends TestCase
{

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockFilterChain chain = new MockFilterChain();

    public void testASCIICharacters()
          throws Exception
  {
      request.setProtocol( "http" );
      request.setServerName( "localhost" );
      request.setCharacterEncoding( "UTF-8" );
      request.setParameter( "param1", "12345" );
      request.setParameter( "param2", "ABCd" );
      request.setParameter( "param3", "%00" );
      request.setParameter( "param4", "?key1=%00" );
      request.setParameter( "param5", "?key1=%00&key2=555" );
      request.setParameter( "param6", "page=2&op=browse&selecteddomainkey=&selectedmenukey=&loadmainstartpage=true" );
      request.setParameter( "param7", "adminpage?page=2&op=browse&selecteddomainkey=%0p&selectedmenukey=&loadmainstartpage=true" );
      request.setParameter( "param8", "adminpage?page=2&page=%00&page=ABc" );
      request.setParameter( "param9", "a?key1=%00" );
      request.setParameter( "param10", "\u0000" );
      request.setParameter( "param11", "English-speaking world" );
      request.setParameter( "param12", "100%" );
      request.setParameter( "param13", "rya@enonic.com" );
      request.setParameter( "param14", "\u001F" );
      request.setParameter( "param15", "\u0020" );
      request.setParameter( "param16", "\u0011" );
      request.setParameter( "param17", "\u007F" );
      request.setParameter( "param18", "\u007E" );
      request.setParameter( "param19", "\t" );
      request.setParameter( "param20", "\r" );
      request.setParameter( "param22", "Bl\u00e5b\u00e6r" );  // Blueberry in Norwegian
      request.setParameter( "param23", "\u0447\u0435\u0440\u043D\u0438\u043A\u0431" ); // Blueberry in Russian

      request.setParameter( "param24", "x&gt;10" );     // CMS-156 case
      request.setParameter( "param25", "x&gt;=10" );    // CMS-156 case

      ASCIICharactersFilter filter = new ASCIICharactersFilter();

      filter.doFilter( request, response, chain );

      HttpServletRequestWrapper result = (HttpServletRequestWrapper) chain.getRequest();

      // verify
      assertEquals( "12345", result.getParameter( "param1" ) );
      assertEquals( "ABCd", result.getParameter( "param2" ) );
      assertEquals( "%00", result.getParameter( "param3" ) );
      assertEquals( "?key1=%00", result.getParameter( "param4" ) );
      assertEquals( "?key1=%00&key2=555", result.getParameter( "param5" ) );
      assertEquals( "page=2&op=browse&selecteddomainkey=&selectedmenukey=&loadmainstartpage=true", result.getParameter( "param6" ) );
      assertEquals( "adminpage?page=2&op=browse&selecteddomainkey=%0p&selectedmenukey=&loadmainstartpage=true", result.getParameter( "param7" ) );
      assertEquals( "adminpage?page=2&page=%00&page=ABc", result.getParameter( "param8" ) );
      assertEquals( "a?key1=%00", result.getParameter( "param9" ) );
      assertEquals( "", result.getParameter( "param10" ) );
      assertEquals( "English-speaking world", result.getParameter( "param11" ) );
      assertEquals( "100%", result.getParameter( "param12" ) );
      assertEquals( "rya@enonic.com", result.getParameter( "param13" ) );
      assertEquals( "", result.getParameter( "param14" ) );
      assertEquals( "\u0020", result.getParameter( "param15" ) );
      assertEquals( "", result.getParameter( "param16" ) );
      assertEquals( "", result.getParameter( "param17" ) );
      assertEquals( "\u007E", result.getParameter( "param18" ) );
      assertEquals( "\t", result.getParameter( "param19" ) );
      assertEquals( "\r", result.getParameter( "param20" ) );
      assertEquals( "Bl\u00e5b\u00e6r", result.getParameter( "param22" ) );
      assertEquals( "\u0447\u0435\u0440\u043D\u0438\u043A\u0431", result.getParameter( "param23" ) );

      assertEquals( "x&gt;10", result.getParameter( "param24" ) );
      assertEquals( "x&gt;=10", result.getParameter( "param25" ) );
  }
}
