package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.histogram.HistogramFacetBuilder;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.HistogramFacetModel;

public class ElasticsearchHistogramFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{

    final HistogramFacetBuilder build( HistogramFacetModel histogramFacetModel )
    {
        try
        {
            histogramFacetModel.validate();
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Error in histogram-facet definition", e );
        }

        HistogramFacetBuilder builder = new HistogramFacetBuilder( histogramFacetModel.getName() );

        setField( histogramFacetModel, builder );

        setInterval( histogramFacetModel, builder );

        return builder;
    }

    private void setInterval( final HistogramFacetModel histogramFacetModel, final HistogramFacetBuilder builder )
    {
        builder.interval( histogramFacetModel.getInterval() );
    }

    protected void setField( final HistogramFacetModel histogramFacetModel, final HistogramFacetBuilder builder )
    {
        final String fieldName = histogramFacetModel.getIndex();

        if ( !Strings.isNullOrEmpty( fieldName ) )
        {
            builder.field( getNumericFieldName( fieldName ) );

        }
        else if ( !Strings.isNullOrEmpty( histogramFacetModel.getKeyField() ) &&
            !Strings.isNullOrEmpty( histogramFacetModel.getValueField() ) )
        {
            builder.keyField( getNumericFieldName( histogramFacetModel.getKeyField() ) );
            builder.valueField( getNumericFieldName( histogramFacetModel.getValueField() ) );
        }
    }
}
