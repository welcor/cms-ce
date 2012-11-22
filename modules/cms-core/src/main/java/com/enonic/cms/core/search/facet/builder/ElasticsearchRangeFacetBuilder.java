package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.range.RangeFacetBuilder;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.FacetRange;
import com.enonic.cms.core.search.facet.model.FacetRangeValue;
import com.enonic.cms.core.search.facet.model.RangeFacetModel;
import com.enonic.cms.core.search.query.QueryField;

public class ElasticsearchRangeFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{
    final RangeFacetBuilder build( RangeFacetModel rangeFacetModel )
    {
        try
        {
            rangeFacetModel.validate();
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Error in range-facet definition", e );
        }

        RangeFacetBuilder builder = new RangeFacetBuilder( rangeFacetModel.getName() );

        setField( rangeFacetModel, builder );

        setRanges( rangeFacetModel, builder );

        return builder;
    }

    private void setRanges( final RangeFacetModel rangeFacetModel, final RangeFacetBuilder builder )
    {

        for ( final FacetRange facetRange : rangeFacetModel.getRanges() )
        {
            final FacetRangeValue fromRangeValue = facetRange.getFromRangeValue();
            final FacetRangeValue toRangeValue = facetRange.getToRangeValue();
            addRange( builder, fromRangeValue, toRangeValue );
        }
    }

    private void addRange( final RangeFacetBuilder builder, final FacetRangeValue fromRangeValue, final FacetRangeValue toRangeValue )
    {
        String from = fromRangeValue != null ? fromRangeValue.getStringValue() : null;
        String to = toRangeValue != null ? toRangeValue.getStringValue() : null;

        builder.addRange( from, to );
    }

    protected void setField( final RangeFacetModel rangeFacetModel, final RangeFacetBuilder builder )
    {
        final String fieldName = rangeFacetModel.getIndex();

        if ( !Strings.isNullOrEmpty( fieldName ) )
        {
            QueryField queryField = new QueryField( createQueryFieldName( fieldName ) );

            if ( rangeFacetModel.isNumericRanges() )
            {
                builder.field( queryField.getFieldNameForNumericQueries() );
            }
            else
            {
                builder.field( queryField.getFieldNameForDateQueries() );
            }
        }
        else if ( !Strings.isNullOrEmpty( rangeFacetModel.getKeyField() ) && !Strings.isNullOrEmpty( rangeFacetModel.getValueField() ) )
        {
            QueryField keyField = new QueryField( createQueryFieldName( rangeFacetModel.getKeyField() ) );
            QueryField valueField = new QueryField( createQueryFieldName( rangeFacetModel.getValueField() ) );

            if ( rangeFacetModel.isNumericRanges() )
            {
                builder.keyField( keyField.getFieldNameForNumericQueries() );
                builder.valueField( valueField.getFieldNameForNumericQueries() );
            }
            else
            {
                builder.keyField( keyField.getFieldNameForDateQueries() );
                builder.valueField( valueField.getFieldNameForDateQueries() );
            }
        }
    }
}
