package com.enonic.cms.core.xslt.functions;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

public abstract class AbstractXsltFunction
    extends ExtensionFunctionDefinition
{
    private final StructuredQName qName;

    private int minArgs = 0;

    private SequenceType resultType;

    private SequenceType[] argTypes = new SequenceType[0];

    private final List<ExtensionFunctionDefinition> aliases;

    public AbstractXsltFunction( final String namespacePrefix, final String namespaceUri, final String localName )
    {
        this.qName = new StructuredQName( namespacePrefix, namespaceUri, localName );
        this.aliases = Lists.newArrayList();
    }

    @Override
    public final StructuredQName getFunctionQName()
    {
        return this.qName;
    }

    @Override
    public final SequenceType[] getArgumentTypes()
    {
        return this.argTypes;
    }

    @Override
    public final SequenceType getResultType( final SequenceType[] sequenceTypes )
    {
        return this.resultType;
    }

    protected final void setMinimumNumberOfArguments( final int args )
    {
        this.minArgs = args;
    }

    protected final void setResultType( final SequenceType resultType )
    {
        this.resultType = resultType;
    }

    protected final void setArgumentTypes( final SequenceType... types )
    {
        this.argTypes = types;
    }

    @Override
    public final int getMinimumNumberOfArguments()
    {
        return this.minArgs;
    }

    @Override
    public final int getMaximumNumberOfArguments()
    {
        return this.argTypes.length;
    }

    protected final void registerAlias( final StructuredQName qName )
    {
        this.aliases.add( new AliasFunctionWrapper( qName, this ) );
    }

    public final ExtensionFunctionDefinition[] getAliases()
    {
        return this.aliases.toArray( new ExtensionFunctionDefinition[this.aliases.size()] );
    }
}
