package com.enonic.cms.core.search.query.factory.facet.builder;

import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.QueryFieldNameResolver;
import com.enonic.cms.core.search.query.factory.facet.model.TermsFacetModel;

final class ElasticsearchTermsFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{
    final TermsFacetBuilder build( TermsFacetModel termsFacetXml )
    {
        TermsFacetBuilder builder = new TermsFacetBuilder( termsFacetXml.getName() );

        setField( termsFacetXml, builder );

        setSize( termsFacetXml, builder );

        setFields( termsFacetXml, builder );

        setOrder( termsFacetXml, builder );

        setRegexp( termsFacetXml, builder );

        setExcludes( termsFacetXml, builder );

        return builder;
    }

    protected void setField( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        final String fieldName = termsFacetXml.getField();
        if ( !com.google.common.base.Strings.isNullOrEmpty( fieldName ) )
        {
            final String resolvedQueryFieldName = QueryFieldNameResolver.resolveQueryFieldName( fieldName );

            builder.field( resolvedQueryFieldName );
        }
    }

    protected void setSize( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        if ( termsFacetXml.getSize() != null )
        {
            builder.size( termsFacetXml.getSize() );
        }
    }

    protected void setOrder( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        if ( !com.google.common.base.Strings.isNullOrEmpty( termsFacetXml.getOrder() ) )
        {
            final TermsFacet.ComparatorType comparatorType = getTermsFacetComperatorType( termsFacetXml );

            builder.order( comparatorType );
        }
    }

    private TermsFacet.ComparatorType getTermsFacetComperatorType( final TermsFacetModel termsFacetXml )
    {
        try
        {
            return TermsFacet.ComparatorType.valueOf( termsFacetXml.getOrder().toUpperCase() );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Parameter value '" + termsFacetXml.getOrder() + "' not valid order value", e );
        }
    }


    private void setExcludes( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        final String[] excludes = getCommaDelimitedStringAsArray( termsFacetXml.getExclude() );

        if ( excludes != null && excludes.length > 0 )
        {
            builder.exclude( excludes );
        }
    }

    private void setRegexp( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        if ( !Strings.isNullOrEmpty( termsFacetXml.getRegex() ) )
        {
            if ( !Strings.isNullOrEmpty( termsFacetXml.getRegexFlags() ) )
            {
                builder.regex( termsFacetXml.getRegex(),
                               AbstractElasticsearchFacetBuilder.regExFlags.valueOf( termsFacetXml.getRegexFlags() ).getValue() );
            }
            else
            {
                builder.regex( termsFacetXml.getRegex() );
            }
        }
    }

    private void setFields( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        final String[] fields = getCommaDelimitedStringAsArray( termsFacetXml.getFields() );

        if ( fields != null && fields.length > 0 )
        {
            builder.fields( fields );
        }
    }


}
