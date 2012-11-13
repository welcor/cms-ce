package com.enonic.cms.core.search.result;

import java.util.Iterator;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

public class FacetResultXmlCreator
{
    public void addFacetResultXml( Document doc, FacetsResultSet facetsResultSet )
    {

        Element root = doc.getRootElement();

        Element facetsNode = new Element( "facets" );

        root.addContent( facetsNode );

        final Iterator<FacetResultSet> iterator = facetsResultSet.iterator();

        while ( iterator.hasNext() )
        {
            final FacetResultSet facet = iterator.next();

            final FacetResultType facetResultType = facet.getFacetResultType();

            switch ( facetResultType )
            {
                case TERMS:
                    addTermFacetResult( facetsNode, (TermsFacetResultSet) facet );
                    break;
                case RANGE:
                    break;
            }
        }
    }

    private void addTermFacetResult( final Element facetsNode, final TermsFacetResultSet facet )
    {
        Element facetEl = new Element( facet.getName() );
        facetsNode.addContent( facetEl );

        final Map<String, Integer> resultMap = facet.getResults();
        for ( String result : resultMap.keySet() )
        {
            final Integer count = resultMap.get( result );

            Element resultEl = new Element( "result" );
            Element termEl = new Element( "term" );
            termEl.addContent( result );
            resultEl.addContent( termEl );
            Element countEl = new Element( "count" );
            countEl.addContent( "" + count );
            resultEl.addContent( countEl );

            facetEl.addContent( resultEl );
        }
    }

}
