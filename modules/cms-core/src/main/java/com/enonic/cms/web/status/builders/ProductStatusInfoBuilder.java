package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.product.ProductVersion;
import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public final class ProductStatusInfoBuilder
    extends StatusInfoBuilder
{
    public ProductStatusInfoBuilder()
    {
        super( "product" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        json.put( "name", ProductVersion.getTitle() );
        json.put( "version", ProductVersion.getVersion() );
        json.put( "edition", ProductVersion.getEdition().toLowerCase() );
    }
}
