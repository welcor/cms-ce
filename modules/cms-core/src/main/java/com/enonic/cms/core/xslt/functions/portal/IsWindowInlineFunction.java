package com.enonic.cms.core.xslt.functions.portal;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import com.enonic.cms.core.xslt.functions.AbstractXsltFunctionCall;

final class IsWindowInlineFunction
    extends AbstractPortalFunction
{
    private final class Call
        extends AbstractXsltFunctionCall
    {
        @Override
        public SequenceIterator<? extends Item> call( final SequenceIterator<? extends Item>[] args, final XPathContext context )
            throws XPathException
        {
            return createValue( getPortalFunctions().isWindowInline() );
        }
    }

    public IsWindowInlineFunction()
    {
        super( "isWindowInline" );
        setResultType( SequenceType.SINGLE_BOOLEAN );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
