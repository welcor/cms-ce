package com.enonic.cms.core.search.query.factory.facet.builder;

import java.util.Set;

import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

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
            final String resolvedQueryFieldName = createQueryFieldName( fieldName );

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
                builder.regex( termsFacetXml.getRegex(), getRegexFlagValue( termsFacetXml ) );
            }
            else
            {
                builder.regex( termsFacetXml.getRegex() );
            }
        }
    }

    private int getRegexFlagValue( final TermsFacetModel termsFacetXml )
    {
        final String[] flags = getCommaDelimitedStringAsArray( termsFacetXml.getRegexFlags() );

        int flagValue = 0;

        if ( flags != null && flags.length > 0 )
        {

            for ( String flag : flags )
            {
                try
                {
                    flagValue += regExFlags.valueOf( flag ).getValue();
                }
                catch ( IllegalArgumentException e )
                {
                    throw new IndexQueryException( "Error in definition of facet '" + termsFacetXml.getName() + "': Regex flag '" + flag +
                                                       "' is not a valid regexp flag value" );
                }
            }
        }

        return flagValue;
    }

    private void setFields( final TermsFacetModel termsFacetXml, final TermsFacetBuilder builder )
    {
        final String[] fields = getCommaDelimitedStringAsArray( termsFacetXml.getFields() );

        if ( fields != null && fields.length > 0 )
        {
            final String[] fieldNamesArray = cleanupAndConvertToQueryFieldNames( fields );

            builder.fields( fieldNamesArray );
        }
    }

    private String[] cleanupAndConvertToQueryFieldNames( final String[] fields )
    {
        Set<String> fieldNames = Sets.newLinkedHashSet();

        for ( String field : fields )
        {
            fieldNames.add( createQueryFieldName( field ) );
        }

        return fieldNames.toArray( new String[fieldNames.size()] );
    }

    private String createQueryFieldName( final String field )
    {
        return QueryFieldNameResolver.resolveQueryFieldName( field );
    }


}
