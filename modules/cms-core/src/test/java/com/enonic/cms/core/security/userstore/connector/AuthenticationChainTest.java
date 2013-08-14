package com.enonic.cms.core.security.userstore.connector;

import org.junit.Test;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationInterceptor;
import com.enonic.cms.api.plugin.ext.auth.AuthenticationResult;
import com.enonic.cms.api.plugin.ext.auth.AuthenticationToken;
import com.enonic.cms.core.plugin.ext.AuthenticationInterceptorExtensions;

import static org.junit.Assert.*;

public class AuthenticationChainTest
{
    private final class InterceptorImpl
        extends AuthenticationInterceptor
    {
        private final AuthenticationResult result;

        private int invokeCount;

        public InterceptorImpl( final AuthenticationResult result )
        {
            this.result = result;
            this.invokeCount = 0;
        }

        @Override
        public AuthenticationResult authenticate( final AuthenticationToken token )
        {
            this.invokeCount++;
            assertNotNull( token );
            assertEquals( "store", token.getUserStore() );
            assertEquals( "user", token.getUserName() );
            assertEquals( "password", token.getPassword() );
            return this.result;
        }
    }

    @Test
    public void no_interceptors_test()
    {
        final AuthenticationChain chain = create();
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.CONTINUE, result );
    }

    @Test
    public void single_interceptor_ok_test()
    {
        final InterceptorImpl interceptor = new InterceptorImpl( AuthenticationResult.OK );
        final AuthenticationChain chain = create( interceptor );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.OK, result );
        assertEquals( 1, interceptor.invokeCount );
    }

    @Test
    public void single_interceptor_failed_test()
    {
        final InterceptorImpl interceptor = new InterceptorImpl( AuthenticationResult.FAILED );
        final AuthenticationChain chain = create( interceptor );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.FAILED, result );
        assertEquals( 1, interceptor.invokeCount );
    }

    @Test
    public void single_interceptor_continue_test()
    {
        final InterceptorImpl interceptor = new InterceptorImpl( AuthenticationResult.CONTINUE );
        final AuthenticationChain chain = create( interceptor );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.CONTINUE, result );
        assertEquals( 1, interceptor.invokeCount );
    }

    @Test
    public void two_interceptors_continue_test()
    {
        final InterceptorImpl interceptor1 = new InterceptorImpl( AuthenticationResult.CONTINUE );
        final InterceptorImpl interceptor2 = new InterceptorImpl( AuthenticationResult.CONTINUE );

        final AuthenticationChain chain = create( interceptor1, interceptor2 );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.CONTINUE, result );
        assertEquals( 1, interceptor1.invokeCount );
        assertEquals( 1, interceptor2.invokeCount );
    }

    @Test
    public void two_interceptors_continue_ok_test()
    {
        final InterceptorImpl interceptor1 = new InterceptorImpl( AuthenticationResult.CONTINUE );
        final InterceptorImpl interceptor2 = new InterceptorImpl( AuthenticationResult.OK );

        final AuthenticationChain chain = create( interceptor1, interceptor2 );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.OK, result );
        assertEquals( 1, interceptor1.invokeCount );
        assertEquals( 1, interceptor2.invokeCount );
    }

    @Test
    public void two_interceptors_ok_continue_test()
    {
        final InterceptorImpl interceptor1 = new InterceptorImpl( AuthenticationResult.OK );
        final InterceptorImpl interceptor2 = new InterceptorImpl( AuthenticationResult.CONTINUE );

        final AuthenticationChain chain = create( interceptor1, interceptor2 );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.OK, result );
        assertEquals( 1, interceptor1.invokeCount );
        assertEquals( 0, interceptor2.invokeCount );
    }

    @Test
    public void two_interceptors_failed_ok_test()
    {
        final InterceptorImpl interceptor1 = new InterceptorImpl( AuthenticationResult.FAILED );
        final InterceptorImpl interceptor2 = new InterceptorImpl( AuthenticationResult.OK );

        final AuthenticationChain chain = create( interceptor1, interceptor2 );
        final AuthenticationResult result = chain.authenticate( "store", "user", "password" );

        assertNotNull( result );
        assertEquals( AuthenticationResult.FAILED, result );
        assertEquals( 1, interceptor1.invokeCount );
        assertEquals( 0, interceptor2.invokeCount );
    }

    private AuthenticationChain create( final AuthenticationInterceptor... interceptors )
    {
        final AuthenticationInterceptorExtensions extensions = new AuthenticationInterceptorExtensions();
        for ( final AuthenticationInterceptor interceptor : interceptors )
        {
            extensions.extensionAdded( interceptor );
        }

        return new AuthenticationChain( extensions );
    }
}
