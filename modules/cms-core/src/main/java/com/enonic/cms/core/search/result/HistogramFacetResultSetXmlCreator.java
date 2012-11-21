package com.enonic.cms.core.search.result;

import java.util.Set;

import org.jdom.Element;

public class HistogramFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{

    public Element createHistogramFacetElement( HistogramFacetResultSet histogramFacetResultSet )
    {
        final Element rangeFacetRootElement = createFacetRootElement( "histogram", histogramFacetResultSet );

        final Set<HistogramFacetResultEntry> resultEntries = histogramFacetResultSet.getResultEntries();

        for ( HistogramFacetResultEntry result : resultEntries )
        {
            Element resultEl = new Element( "result" );
            addAttributeIfNotNull( resultEl, "total", result.getTotal() );
            addAttributeIfNotNull( resultEl, "total-count", result.getTotalCount() );
            addAttributeIfNotNull( resultEl, "count", result.getCount() );
            addAttributeIfNotNull( resultEl, "min", result.getMin() );
            addAttributeIfNotNull( resultEl, "mean", result.getMean() );
            addAttributeIfNotNull( resultEl, "max", result.getMax() );

            resultEl.addContent( result.getKey() + "" );

            rangeFacetRootElement.addContent( resultEl );
        }

        return rangeFacetRootElement;
    }


}
