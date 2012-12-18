package com.enonic.cms.core.search.facet.builder;

import java.util.Set;

import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.TermsFacetModel;
import com.enonic.cms.core.search.query.IndexQueryException;

final class ElasticsearchTermsFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{
    final TermsFacetBuilder build( TermsFacetModel termsFacetModel )
    {
        try
        {
            termsFacetModel.validate();
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Error in terms-facet definition", e );
        }

        TermsFacetBuilder builder = new TermsFacetBuilder( termsFacetModel.getName() );

        setFields( termsFacetModel, builder );

        setSize( termsFacetModel, builder );

        setOrder( termsFacetModel, builder );

        setRegexp( termsFacetModel, builder );

        setExcludes( termsFacetModel, builder );

        return builder;
    }

    protected void setSize( final TermsFacetModel termsFacetModel, final TermsFacetBuilder builder )
    {
        if ( termsFacetModel.getCount() != null )
        {
            builder.size( termsFacetModel.getCount() );
        }
    }

    protected void setOrder( final TermsFacetModel termsFacetModel, final TermsFacetBuilder builder )
    {
        final String elasticsearchOrderBy = createElasticsearchOrderByString( termsFacetModel.getFacetOrderBy() );

        if ( !Strings.isNullOrEmpty( elasticsearchOrderBy ) )
        {
            final TermsFacet.ComparatorType comparatorType = getTermsFacetComperatorType( elasticsearchOrderBy );

            builder.order( comparatorType );
        }
    }

    private TermsFacet.ComparatorType getTermsFacetComperatorType( final String elasticsearchOrderBy )
    {
        try
        {
            return TermsFacet.ComparatorType.valueOf( elasticsearchOrderBy.toUpperCase() );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Parameter value '" + elasticsearchOrderBy + "' not valid order value", e );
        }
    }


    private void setExcludes( final TermsFacetModel termsFacetModel, final TermsFacetBuilder builder )
    {
        final String[] excludes = getCommaDelimitedStringAsArraySkipWhitespaces( termsFacetModel.getExclude() );

        if ( excludes != null && excludes.length > 0 )
        {
            builder.exclude( excludes );
        }
    }

    private void setRegexp( final TermsFacetModel termsFacetModel, final TermsFacetBuilder builder )
    {
        if ( !Strings.isNullOrEmpty( termsFacetModel.getRegex() ) )
        {
            if ( !Strings.isNullOrEmpty( termsFacetModel.getRegexFlags() ) )
            {
                builder.regex( termsFacetModel.getRegex(), getRegexFlagValue( termsFacetModel ) );
            }
            else
            {
                builder.regex( termsFacetModel.getRegex() );
            }
        }
    }

    private int getRegexFlagValue( final TermsFacetModel termsFacetXml )
    {
        final String[] flags = getCommaDelimitedStringAsArraySkipWhitespaces( termsFacetXml.getRegexFlags() );

        int flagValue = 0;

        if ( flags != null && flags.length > 0 )
        {

            for ( String flag : flags )
            {
                try
                {
                    flagValue += RegExpFlags.valueOf( flag ).getValue();
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
        final String[] fields = getCommaDelimitedStringAsArraySkipWhitespaces( termsFacetXml.getIndexes() );

        if ( fields == null || fields.length == 0 )
        {
            return;
        }

        final String[] fieldNamesArray = cleanupAndConvertToQueryFieldNames( fields );

        if ( fields.length == 1 )
        {
            builder.field( fieldNamesArray[0] );
        }
        else
        {
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


}
