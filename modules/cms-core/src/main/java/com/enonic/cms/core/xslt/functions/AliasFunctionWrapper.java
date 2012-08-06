package com.enonic.cms.core.xslt.functions;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

final class AliasFunctionWrapper
    extends ExtensionFunctionDefinition
{
    private final StructuredQName qName;

    private final ExtensionFunctionDefinition wrapped;

    public AliasFunctionWrapper(final StructuredQName qName, final ExtensionFunctionDefinition wrapped)
    {
        this.qName = qName;
        this.wrapped = wrapped;
    }

    @Override
    public StructuredQName getFunctionQName()
    {
        return this.qName;
    }

    @Override
    public SequenceType[] getArgumentTypes()
    {
        return this.wrapped.getArgumentTypes();
    }

    @Override
    public SequenceType getResultType( final SequenceType[] args )
    {
        return this.wrapped.getResultType( args );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return this.wrapped.makeCallExpression();
    }

    @Override
    public int getMinimumNumberOfArguments()
    {
        return this.wrapped.getMinimumNumberOfArguments();
    }

    @Override
    public int getMaximumNumberOfArguments()
    {
        return this.wrapped.getMaximumNumberOfArguments();
    }

    @Override
    public boolean trustResultType()
    {
        return this.wrapped.trustResultType();
    }

    @Override
    public boolean dependsOnFocus()
    {
        return this.wrapped.dependsOnFocus();
    }

    @Override
    public boolean hasSideEffects()
    {
        return this.wrapped.hasSideEffects();
    }
}
