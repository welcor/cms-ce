package com.enonic.cms.core.search.result;

import java.util.Set;

import org.jdom.Element;

public class TermsStatsFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{

    public Element create( TermsStatsFacetResultSet termsStatsFacetResultSet )
    {
        final Element rangeFacetRootElement = createFacetRootElement( "terms-stats", termsStatsFacetResultSet );

        final Set<TermsStatsFacetResultEntry> resultEntries = termsStatsFacetResultSet.getResults();

        for ( TermsStatsFacetResultEntry result : resultEntries )
        {
            Element resultEl = new Element( "term" );
            addAttributeIfNotNull( resultEl, "total", result.getTotal() );
            addAttributeIfNotNull( resultEl, "total-count", result.getTotalCount() );
            addAttributeIfNotNull( resultEl, "hits", result.getCount() );
            addAttributeIfNotNull( resultEl, "min", result.getMin() );
            addAttributeIfNotNull( resultEl, "mean", result.getMean() );
            addAttributeIfNotNull( resultEl, "max", result.getMax() );

            resultEl.addContent( result.getTerm() );

            rangeFacetRootElement.addContent( resultEl );
        }

        return rangeFacetRootElement;
    }

}
