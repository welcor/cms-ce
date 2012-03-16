package com.enonic.cms.core.product;

public enum ProductEdition
{
    COMMUNITY("Community"),
    ENTERPRISE("Enterprise");
    
    private final String name;

    ProductEdition(final String name)
    {
        this.name = name;
    }
    
    public String toString()
    {
        return this.name;
    }
}
