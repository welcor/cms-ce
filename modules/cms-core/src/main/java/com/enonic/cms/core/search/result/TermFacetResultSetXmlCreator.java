package com.enonic.cms.core.search.result;

import java.util.Map;

import org.jdom.Element;

public class TermFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{
    Element create( final TermsFacetResultSet facet )
    {
        final Element termFacetRoot = createFacetRootElement( "terms", facet );

        setFacetResultMetaData( facet, termFacetRoot );

        final Map<String, Integer> resultMap = facet.getResults();
        for ( String result : resultMap.keySet() )
        {
            final Integer count = resultMap.get( result );

            Element resultEl = new Element( "term" );
            addAttributeIfNotNull( resultEl, "count", count );
            resultEl.addContent( result );
            termFacetRoot.addContent( resultEl );
        }
        return termFacetRoot;
    }

    private void setFacetResultMetaData( final TermsFacetResultSet facet, final Element facetEl )
    {
        final Long missing = facet.getMissing();
        final Long total = facet.getTotal();
        final Long other = facet.getOther();

        addAttributeIfNotNull( facetEl, "total", total );
        addAttributeIfNotNull( facetEl, "missing", missing );
        addAttributeIfNotNull( facetEl, "other", other );
    }
}