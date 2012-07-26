/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.search.query;

/**
 * This class implements the aggregated result.
 */
public final class AggregatedResultImpl
    implements AggregatedResult
{
    /**
     * Count.
     */
    private final double count;

    /**
     * Min.
     */
    private final double minValue;

    /**
     * Max.
     */
    private final double maxValue;

    /**
     * Sum.
     */
    private final double sumValue;

    /**
     * Average.
     */
    private final double averageValue;

    /**
     * Construct the result.
     */
    public AggregatedResultImpl( double count, double minValue, double maxValue, double sumValue, double averageValue )
    {
        this.count = count;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sumValue = sumValue;
        this.averageValue = averageValue;
    }

    /**
     * Return the count.
     */
    public int getCount()
    {
        return (int) Math.round( this.count );
    }

    /**
     * Return min value.
     */
    public double getMinValue()
    {
        return this.minValue;
    }

    /**
     * Return max value.
     */
    public double getMaxValue()
    {
        return this.maxValue;
    }

    /**
     * Return average value.
     */
    public double getAverageValue()
    {
        return this.averageValue;
    }

    /**
     * Return sum value.
     */
    public double getSumValue()
    {
        return this.sumValue;
    }
}
