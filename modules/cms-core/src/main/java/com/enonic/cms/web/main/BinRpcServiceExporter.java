/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.client.binrpc.BinRpcInvocation;
import com.enonic.cms.api.client.binrpc.BinRpcInvocationResult;

/**
 * This class implements the service exporter.
 */
@Controller
public final class BinRpcServiceExporter
{
    private final static String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    private LocalClient client;

    @Value("${cms.security.rpc.enabled}")
    private boolean rpcEnabled;

    @Autowired
    @Qualifier("remoteClient")
    public void setLocalClient( final LocalClient client )
    {
        this.client = client;
    }

    @RequestMapping(value = "/rpc/bin", method = RequestMethod.POST)
    public ModelAndView handleRequest( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !this.rpcEnabled )
        {
            BinRpcInvocationResult result = new BinRpcInvocationResult( new ClientException( "RPC service is turned off" ) );
            writeInvocationResult( res, result );
            return null;
        }

        try
        {
            BinRpcInvocation invocation = readInvocation( req );
            BinRpcInvocationResult result = invokeAndCreateResult( invocation, this.client );
            writeInvocationResult( res, result );
        }
        catch ( ClassNotFoundException ex )
        {
            throw new NestedServletException( "Class not found during deserialization", ex );
        }

        return null;
    }

    private BinRpcInvocation readInvocation( final HttpServletRequest req )
        throws IOException, ClassNotFoundException
    {
        InputStream in = req.getInputStream();
        ObjectInputStream ois = new ObjectInputStream( in );

        try
        {
            Object obj = ois.readObject();
            if ( !( obj instanceof BinRpcInvocation ) )
            {
                throw new IOException(
                    "Deserialized object needs to be assignable to type [" + BinRpcInvocation.class.getName() + "]: " + obj );
            }
            else
            {
                return (BinRpcInvocation) obj;
            }
        }
        finally
        {
            ois.close();
        }
    }

    private void writeInvocationResult( final HttpServletResponse res, final BinRpcInvocationResult result )
        throws IOException
    {
        res.setContentType( BinRpcServiceExporter.CONTENT_TYPE_SERIALIZED_OBJECT );
        OutputStream out = res.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( out );

        try
        {
            oos.writeObject( result );
            oos.flush();
        }
        finally
        {
            oos.close();
        }
    }

    private BinRpcInvocationResult invokeAndCreateResult( final BinRpcInvocation invocation, final Object targetObject )
    {
        try
        {
            Object value = invocation.invoke( targetObject );
            return new BinRpcInvocationResult( value );
        }
        catch ( Throwable ex )
        {
            return new BinRpcInvocationResult( ex );
        }
    }
}
