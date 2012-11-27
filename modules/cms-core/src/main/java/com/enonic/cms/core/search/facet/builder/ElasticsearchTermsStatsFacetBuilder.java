package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.termsstats.TermsStatsFacet;
import org.elasticsearch.search.facet.termsstats.TermsStatsFacetBuilder;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.TermsStatsFacetModel;
import com.enonic.cms.core.search.query.IndexQueryException;

public class ElasticsearchTermsStatsFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{
    final TermsStatsFacetBuilder build( TermsStatsFacetModel termsStatsFacetModel )
    {
        try
        {
            termsStatsFacetModel.validate();
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Error in terms-stats-facet definition", e );
        }

        TermsStatsFacetBuilder builder = new TermsStatsFacetBuilder( termsStatsFacetModel.getName() );

        setField( termsStatsFacetModel, builder );

        setSize( termsStatsFacetModel, builder );

        setOrder( termsStatsFacetModel, builder );

        return builder;
    }


    protected void setField( final TermsStatsFacetModel termsStatsFacetModel, final TermsStatsFacetBuilder builder )
    {
        builder.keyField( createQueryFieldName( termsStatsFacetModel.getIndex() ) );
        builder.valueField( createNumericFieldName( termsStatsFacetModel.getValueIndex() ) );
    }


    protected void setSize( final TermsStatsFacetModel termsStatsFacetModel, final TermsStatsFacetBuilder builder )
    {
        if ( termsStatsFacetModel.getCount() != null )
        {
            builder.size( termsStatsFacetModel.getCount() );
        }
    }

    protected void setOrder( final TermsStatsFacetModel termStatsFacetsModel, final TermsStatsFacetBuilder builder )
    {
        if ( !Strings.isNullOrEmpty( termStatsFacetsModel.getOrderby() ) )
        {
            final TermsStatsFacet.ComparatorType comparatorType = getTermsFacetComperatorType( termStatsFacetsModel );

            builder.order( comparatorType );
        }
    }

    private TermsStatsFacet.ComparatorType getTermsFacetComperatorType( final TermsStatsFacetModel termStatsFacetModel )
    {
        try
        {
            return TermsStatsFacet.ComparatorType.valueOf( termStatsFacetModel.getOrderby().toUpperCase() );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Parameter value '" + termStatsFacetModel.getOrderby() + "' not valid order value", e );
        }
    }

}
