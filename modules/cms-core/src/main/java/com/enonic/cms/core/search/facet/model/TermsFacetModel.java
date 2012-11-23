package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;


@XmlAccessorType(XmlAccessType.NONE)
public class TermsFacetModel
    extends AbstractFacetModel
{
    private String indices;

    private String exclude;

    private String orderby;

    private String regex;

    private String regexFlags;

    private Boolean allTerms;

    public void validate()
    {
        if ( Strings.isNullOrEmpty( indices ) )
        {
            throw new IllegalArgumentException( "Terms-facet " + getName() + ": Field 'indices' must be set" );
        }
    }

    @XmlElement(name = "orderby")
    public String getOrderby()
    {
        return orderby;
    }

    @XmlElement(name = "all-terms")
    public Boolean getAllTerms()
    {
        return allTerms;
    }

    @XmlElement(name = "indices")
    public String getIndices()
    {
        return indices;
    }

    @XmlElement(name = "exclude")
    public String getExclude()
    {
        return exclude;
    }

    @XmlElement(name = "regex")
    public String getRegex()
    {
        return regex;
    }

    @XmlElement(name = "regex-flags")
    public String getRegexFlags()
    {
        return regexFlags;
    }

    public void setOrderby( final String orderby )
    {
        this.orderby = orderby;
    }

    public void setAllTerms( final Boolean allTerms )
    {
        this.allTerms = allTerms;
    }

    public void setIndices( final String indices )
    {
        this.indices = indices;
    }

    public void setExclude( final String exclude )
    {
        this.exclude = exclude;
    }

    public void setRegex( final String regex )
    {
        this.regex = regex;
    }

    public void setRegexFlags( final String regexFlags )
    {
        this.regexFlags = regexFlags;
    }
}
