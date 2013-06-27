/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.portal;

import java.util.Map;

import com.google.common.collect.Maps;

import net.sf.saxon.Controller;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StringLiteral;
import net.sf.saxon.instruct.DocumentInstr;
import net.sf.saxon.instruct.FixedElement;
import net.sf.saxon.instruct.GlobalParam;
import net.sf.saxon.instruct.ValueOf;
import net.sf.saxon.om.NamePool;

final class ParamTypeExtractor
{
    private final Controller xsl;

    private final NamePool namePool;

    private final Map<String, String> map;

    private ParamTypeExtractor( final Controller xsl )
    {
        this.xsl = xsl;
        this.namePool = this.xsl.getNamePool();
        this.map = Maps.newHashMap();
        doExtract();
    }

    private void doExtract()
    {
        final Map<?, ?> variables = this.xsl.getExecutable().getCompiledGlobalVariables();
        if ( variables == null )
        {
            return;
        }

        for ( final Object variable : variables.values() )
        {
            if ( variable instanceof GlobalParam )
            {
                doExtract( (GlobalParam) variable );
            }
        }
    }

    private void doExtract( final GlobalParam param )
    {
        final String name = param.getVariableQName().toString();
        final String type = findCustomType( param.getSelectExpression() );
        this.map.put( name, type );
    }

    private String findCustomType( final Expression expr )
    {
        if ( !( expr instanceof DocumentInstr ) )
        {
            return null;
        }

        return findCustomType( (DocumentInstr) expr );
    }

    private String findCustomType( final DocumentInstr expr )
    {
        final Expression content = expr.getContentExpression();
        if ( !( content instanceof FixedElement ) )
        {
            return null;
        }

        return findCustomType( (FixedElement) content );
    }

    private String findCustomType( final FixedElement expr )
    {
        final Expression content = expr.getContentExpression();
        if ( !( content instanceof ValueOf ) )
        {
            return null;
        }

        final int nameCode = expr.getNameCode( null );
        final String localName = this.namePool.getLocalName( nameCode );
        if ( !"type".equals( localName ) )
        {
            return null;
        }

        return findCustomType( (ValueOf) content );
    }

    private String findCustomType( final ValueOf expr )
    {
        final Expression select = expr.getSelect();
        if ( !( select instanceof StringLiteral ) )
        {
            return null;
        }

        return ( (StringLiteral) select ).getStringValue();
    }

    public static Map<String, String> extract( final Controller xsl )
    {
        return new ParamTypeExtractor( xsl ).map;
    }
}
