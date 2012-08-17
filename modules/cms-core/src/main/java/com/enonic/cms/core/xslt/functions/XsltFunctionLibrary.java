package com.enonic.cms.core.xslt.functions;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;

public class XsltFunctionLibrary
    implements FunctionLibrary
{
    private final Map<StructuredQName, AbstractXsltFunction> map;

    public XsltFunctionLibrary()
    {
        this.map = Maps.newHashMap();
    }

    protected final void add( final AbstractXsltFunction function )
    {
        this.map.put( function.getName(), function );
        for ( final StructuredQName alias : function.getAliases() )
        {
            this.map.put( alias, function );
        }
    }

    @Override
    public boolean isAvailable( final StructuredQName name, final int arity )
    {
        final AbstractXsltFunction function = this.map.get( name );
        return function != null && function.checkArgCount( arity );
    }

    @Override
    public Expression bind( final StructuredQName name, final Expression[] args, final StaticContext env )
        throws XPathException
    {
        final AbstractXsltFunction function = this.map.get( name );
        if ( function == null )
        {
            return null;
        }

        return function.createCall( args );
    }

    @Override
    public final FunctionLibrary copy()
    {
        final XsltFunctionLibrary lib = new XsltFunctionLibrary();
        lib.map.putAll( this.map );
        return lib;
    }

    private FunctionLibraryList getLibraryList( final Configuration config )
    {
        final FunctionLibrary library = config.getExtensionBinder( "java" );

        if (library instanceof FunctionLibraryList) {
            return (FunctionLibraryList)library;
        } else {
            final FunctionLibraryList list = new FunctionLibraryList();
            list.addFunctionLibrary( library );
            config.setExtensionBinder( "java", list );
            return list;
        }
    }

    public void register( final Configuration config )
    {
        getLibraryList(config).addFunctionLibrary( this );
    }
}
