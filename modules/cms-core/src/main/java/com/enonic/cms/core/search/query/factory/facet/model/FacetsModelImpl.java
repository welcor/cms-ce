package com.enonic.cms.core.search.query.factory.facet.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "facets")
public class FacetsModelImpl
    implements FacetsModel
{
    @XmlElements(
        {
            @XmlElement(name = "facet", type = TermsFacetModel.class),
            @XmlElement(name = "facet", type = RangeFacetModel.class)
        }
    )

    private final Set<FacetModel> facetModels = new HashSet<FacetModel>();

    public void addFacet( FacetModel facet )
    {
        facetModels.add( facet );
    }

    public Set<FacetModel> getFacetModels()
    {
        return facetModels;
    }
}
