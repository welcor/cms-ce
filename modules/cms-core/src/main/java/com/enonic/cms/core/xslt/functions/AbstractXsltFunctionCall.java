/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.expr.RoleLocator;
import net.sf.saxon.expr.TypeChecker;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

public abstract class AbstractXsltFunctionCall
    extends FunctionCall
{
    protected SequenceType[] argumentTypes;

    protected int minArguments;

    protected int maxArguments;

    protected ItemType resultType;

    protected int resultCardinality;

    @Override
    protected final void checkArguments( final ExpressionVisitor visitor )
        throws XPathException
    {
        checkArgumentCount( this.minArguments, this.maxArguments, visitor );
        for ( int i = 0; i < this.argument.length; i++ )
        {
            checkArgument( visitor, i );
        }
    }

    private void checkArgument( final ExpressionVisitor visitor, final int arg )
        throws XPathException
    {
        final RoleLocator role = new RoleLocator( RoleLocator.FUNCTION, getFunctionName(), arg );
        final SequenceType requiredType = getRequiredType( arg );
        final boolean backwardCompatibleMode = visitor.getStaticContext().isInBackwardsCompatibleMode();
        this.argument[arg] = TypeChecker.staticTypeCheck( this.argument[arg], requiredType, backwardCompatibleMode, role, visitor );
    }

    private SequenceType getRequiredType( final int arg )
    {
        if ( this.argumentTypes == null )
        {
            return SequenceType.ANY_SEQUENCE;
        }

        return this.argumentTypes[arg];
    }

    @Override
    public final ItemType getItemType( final TypeHierarchy th )
    {
        return this.resultType;
    }

    @Override
    protected final int computeCardinality()
    {
        return this.resultCardinality;
    }

    @Override
    public final Expression preEvaluate( final ExpressionVisitor visitor )
        throws XPathException
    {
        return this;
    }

    @Override
    public final Item evaluateItem( final XPathContext context )
        throws XPathException
    {
        final SequenceIterator[] items = new SequenceIterator[this.argument.length];
        for ( int i = 0; i < this.argument.length; i++ )
        {
            items[i] = this.argument[i].iterate( context );
        }

        return call( context, items );
    }

    protected abstract Item call( final XPathContext context, final SequenceIterator[] args )
        throws XPathException;

    @Override
    public final Expression copy()
    {
        throw new UnsupportedOperationException();
    }

    protected final Item createValue( final String value )
    {
        return new StringValue( value );
    }

    protected final Item createValue( final boolean value )
    {
        return BooleanValue.get( value );
    }

    protected final String toSingleString( final SequenceIterator it )
        throws XPathException
    {
        final Item item = it.next();
        if ( item == null )
        {
            return null;
        }
        else
        {
            return item.getStringValue();
        }
    }

    protected final String[] toStringArray( final SequenceIterator it )
        throws XPathException
    {
        final List<String> list = Lists.newArrayList();

        while ( true )
        {
            final Item current = it.next();
            if ( current == null )
            {
                break;
            }

            list.add( current.getStringValue() );
        }

        return list.toArray( new String[list.size()] );
    }

    protected final Long toSingleInteger( final SequenceIterator it )
        throws XPathException
    {
        final Item item = it.next();
        if ( item == null )
        {
            return null;
        }
        else if ( item instanceof NumericValue )
        {
            return ( (NumericValue) item ).getDecimalValue().longValue();
        }
        else
        {
            return Long.parseLong( item.getStringValue() );
        }
    }
}
