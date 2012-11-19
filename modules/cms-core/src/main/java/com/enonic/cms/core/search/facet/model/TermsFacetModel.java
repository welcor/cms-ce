package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;


@XmlAccessorType(XmlAccessType.NONE)
public class TermsFacetModel
    extends AbstractFacetModel
{

    private String field;

    private String fields;

    private String exclude;

    private String order;

    private String regex;

    private String regexFlags;

    private Boolean allTerms;

    public void setField( final String field )
    {
        this.field = field;
    }

    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( fields ) && Strings.isNullOrEmpty( field ) )
        {
            throw new IllegalArgumentException( "Termfacet + " + getName() + ": Field or fields must be set" );
        }
    }

    @XmlElement(name = "field")
    public String getField()
    {
        return field;
    }

    @XmlElement(name = "order")
    public String getOrder()
    {
        return order;
    }

    @XmlElement(name = "all_terms")
    public Boolean getAllTerms()
    {
        return allTerms;
    }

    @XmlElement(name = "fields")
    public String getFields()
    {
        return fields;
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

    @XmlElement(name = "regex_flags")
    public String getRegexFlags()
    {
        return regexFlags;
    }

    public void setOrder( final String order )
    {
        this.order = order;
    }

    public void setAllTerms( final Boolean allTerms )
    {
        this.allTerms = allTerms;
    }

    public void setFields( final String fields )
    {
        this.fields = fields;
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
