package com.enonic.cms.core.xslt.functions.admin;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.XPathContextMajor;
import net.sf.saxon.expr.parser.ExpressionTool;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

class EvaluateFunction
    extends AbstractAdminFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        private StaticContext staticContext;

        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            final String exprStr = toSingleString( args[0] );

            final XPathContextMajor newContext = context.newContext();
            final Expression expr = prepareExpression( exprStr );
            newContext.setCurrentIterator( context.getCurrentIterator() );

            return expr.iterate( newContext );
        }

        private Expression prepareExpression( final String exprStr )
            throws XPathException
        {
            return ExpressionTool.make( exprStr, this.staticContext, getContainer(), 0, 0, 1, null );
        }

        @Override
        public void supplyStaticContext( final StaticContext context, final int locationId, final Expression[] arguments )
            throws XPathException
        {
            super.supplyStaticContext( context, locationId, arguments );
            this.staticContext = context;
        }
    }

    public EvaluateFunction()
    {
        super( "evaluate" );
        setArgumentTypes( SequenceType.SINGLE_STRING );
        setResultType( SequenceType.ANY_SEQUENCE );
        setMinimumNumberOfArguments( 1 );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
