/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import com.google.common.base.Strings;

public class FacetOrderBy
{
    public enum Direction
    {
        ASC, DESC
    }

    public enum Value
    {
        SUM, HITS, TERM, MIN, MAX, MEAN
    }

    private Value value;

    private Direction direction;

    protected static FacetOrderBy createFacetOrderBy( String orderbyString )
    {
        // Inputs: Value Direction

        if ( Strings.isNullOrEmpty( orderbyString ) )
        {
            return null;
        }

        FacetOrderBy facetOrderBy = new FacetOrderBy();

        String[] split = orderbyString.split( " " );

        try
        {
            facetOrderBy.value = Value.valueOf( split[0].toUpperCase() );

            if ( split.length == 2 )
            {
                facetOrderBy.direction = Direction.valueOf( split[1].toUpperCase() );
            }
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Not a valid orderby-value: " + orderbyString );
        }

        return facetOrderBy;
    }

    protected String getFacetOrderbyString()
    {
        return this.value.toString() + ( this.direction == null ? "" : this.direction.toString() );
    }

    public Value getValue()
    {
        return value;
    }

    public Direction getDirection()
    {
        return direction;
    }
}
