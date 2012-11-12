package com.enonic.cms.core.search.query.factory.facet;

import com.enonic.cms.core.search.query.factory.facet.model.FacetsModel;
import com.enonic.cms.core.search.query.factory.facet.model.FacetsModelFactory;

public class Main
{
    public static void main(String... args)
    {
        final FacetsModel model = new FacetsModelFactory().buildFromXml( "<facets/>" );



    }
}
