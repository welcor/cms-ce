package com.enonic.cms.core.search.facet.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class FacetRanges
    extends AbstractFacetModel
{
    private String testString;

    public String getTestString()
    {
        return testString;
    }

    @XmlElement(name = "testString")
    public void setTestString( final String testString )
    {
        this.testString = testString;
    }


    public FacetRanges()
    {
    }




}
