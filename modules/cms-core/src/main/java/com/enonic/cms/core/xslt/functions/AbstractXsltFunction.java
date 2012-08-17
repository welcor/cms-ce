package com.enonic.cms.core.xslt.functions;

import java.util.List;

import com.google.common.collect.Lists;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.value.SequenceType;

public abstract class AbstractXsltFunction
{
    private final StructuredQName name;

    private final List<StructuredQName> aliases;

    private int minArguments = 0;

    private int maxArguments = 0;

    private ItemType resultType = AnyItemType.getInstance();

    private int resultCardinality = StaticProperty.ALLOWS_ZERO_OR_MORE;

    private SequenceType[] argumentTypes;

    public AbstractXsltFunction( final String namespacePrefix, final String namespaceUri, final String localName )
    {
        this.name = new StructuredQName( namespacePrefix, namespaceUri, localName );
        this.aliases = Lists.newArrayList();
    }

    public final StructuredQName getName()
    {
        return this.name;
    }

    public final List<StructuredQName> getAliases()
    {
        return this.aliases;
    }

    public final boolean checkArgCount( final int numArgs )
    {
        return ( numArgs == -1 ) || ( numArgs >= this.minArguments && numArgs <= this.maxArguments );
    }

    protected final void registerAlias( final StructuredQName name )
    {
        this.aliases.add( name );
    }

    public final AbstractXsltFunctionCall createCall( final Expression[] args )
        throws XPathException
    {
        final AbstractXsltFunctionCall call = createCall();
        call.setFunctionName( this.name );
        call.setArguments( args );
        call.minArguments = this.minArguments;
        call.maxArguments = this.maxArguments;
        call.resultType = this.resultType;
        call.resultCardinality = this.resultCardinality;
        call.argumentTypes = this.argumentTypes;
        return call;
    }

    protected final void setResultType( final SequenceType type )
    {
        this.resultType = type.getPrimaryType();
        this.resultCardinality = type.getCardinality();
    }

    protected final void setArgumentTypes( final SequenceType... types )
    {
        this.argumentTypes = types;
        this.maxArguments = types.length;
    }

    protected final void setMinimumNumberOfArguments( final int num )
    {
        this.minArguments = num;
    }

    protected final void setMaximumNumberOfArguments( final int args )
    {
        final SequenceType[] types = new SequenceType[args];
        for ( int i = 0; i < types.length; i++ )
        {
            types[i] = SequenceType.ANY_SEQUENCE;
        }

        setArgumentTypes( types );
    }

    protected abstract AbstractXsltFunctionCall createCall();

    /*

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
    public final StructuredQName getName()
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

    public abstract AbstractXsltFunctionCall makeFunctionCall();
    */
}
