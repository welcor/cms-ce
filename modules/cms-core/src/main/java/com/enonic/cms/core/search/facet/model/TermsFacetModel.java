package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;


@XmlAccessorType(XmlAccessType.NONE)
public class TermsFacetModel
    extends AbstractFacetModel
{
    private String indexes;

    private String exclude;

    private FacetOrderBy orderby;

    private String regex;

    private String regexFlags;

    private Boolean allTerms;

    @XmlElement(name = "orderby")
    public String getOrderby()
    {
        return this.orderby != null ? this.orderby.getFacetOrderbyString().toLowerCase() : null;
    }

    public FacetOrderBy getFacetOrderBy()
    {
        return this.orderby;
    }

    @XmlElement(name = "all-terms")
    public Boolean getAllTerms()
    {
        return allTerms;
    }

    @XmlElement(name = "indexes")
    public String getIndexes()
    {
        return indexes;
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
        this.orderby = FacetOrderBy.createFacetOrderBy( orderby );
    }

    public void setAllTerms( final Boolean allTerms )
    {
        this.allTerms = allTerms;
    }

    public void setIndexes( final String indexes )
    {
        this.indexes = indexes;
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

    public void validate()
    {
        if ( Strings.isNullOrEmpty( indexes ) )
        {
            throw new IllegalArgumentException( "Terms-facet " + getName() + ": Field 'indexes' must be set" );
        }

        if ( orderby != null )
        {
            final FacetOrderBy.Value value = orderby.getValue();

            if ( !( value.equals( FacetOrderBy.Value.HITS ) || value.equals( FacetOrderBy.Value.TERM ) ) )
            {
                throw new IllegalArgumentException(
                    "Terms-facet " + getName() + ": Unsupported orderby-value: " + orderby.getFacetOrderbyString() );
            }
        }
    }
}
