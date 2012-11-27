package com.enonic.cms.core.search.facet.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.NONE)
public class DateHistogramFacetModel
    extends AbstractFacetModel
{
    private String index;

    private String interval;

    private String valueIndex;

    private String preZone;

    private String postZone;

    public static final Pattern INTERVAL_PATTERN =
        Pattern.compile( "((\\d+(\\.(\\d)+)?)?(m|h|d|w))|(year|quarter|month|week|day|hour|minute)" );

    @XmlElement(name = "index")
    public String getIndex()
    {
        return index;
    }

    public void setIndex( final String index )
    {
        this.index = index;
    }

    @XmlElement(name = "interval")
    public String getInterval()
    {
        return interval;
    }

    public void setInterval( final String interval )
    {
        this.interval = interval;
    }

    @XmlElement(name = "value-index")
    public String getValueIndex()
    {
        return valueIndex;
    }

    public void setValueIndex( final String valueIndex )
    {
        this.valueIndex = valueIndex;
    }

    @XmlElement(name = "pre-zone")
    public String getPreZone()
    {
        return preZone;
    }

    public void setPreZone( final String preZone )
    {
        this.preZone = preZone;
    }

    @XmlElement(name = "post-zone")
    public String getPostZone()
    {
        return postZone;
    }

    public void setPostZone( final String postZone )
    {
        this.postZone = postZone;
    }

    @Override
    public void validate()
    {
        super.validate();

        if ( Strings.isNullOrEmpty( this.index ) )
        {
            throw new IllegalArgumentException( "Error in date histogram-facet  " + getName() + ": 'index' must be set" );
        }

        if ( this.interval == null )
        {
            throw new IllegalArgumentException( "Error in date histogram-facet " + getName() + ": 'interval' must be set" );
        }

        validateInterval();
    }


    private void validateInterval()
    {
        Matcher m = INTERVAL_PATTERN.matcher( this.interval );
        if ( !m.matches() )
        {
            throw new IllegalArgumentException(
                "Error in date histogram-facet " + getName() + ": Invalid 'interval' expression: " + this.interval );
        }
    }
}
