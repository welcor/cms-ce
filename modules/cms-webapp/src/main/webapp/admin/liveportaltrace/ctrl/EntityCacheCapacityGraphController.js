if ( !lpt )
{
    var lpt = {};
}

lpt.EntityCacheCapacityGraphController = function ()
{
    var entityCacheCapacityGraphValues = new Array( 170 );

    this.add = function ( count, capacity, hitCount, missCount )
    {
        if ( !lpt.ArrayUtility.isInitialized( entityCacheCapacityGraphValues ) )
        {
            lpt.ArrayUtility.initialize( entityCacheCapacityGraphValues, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( entityCacheCapacityGraphValues, count );

        $( '#graph-entity-cache-capacity' ).sparkline( entityCacheCapacityGraphValues,
                                                       {chartRangeMin:0, chartRangeMax:capacity, type:'line', lineColor:'#C49183', fillColor:'#E5AA99', height:'2em'} );

        $( '#graph-entity-cache-hits-vs-misses' ).sparkline( [missCount, hitCount],
                                                             { type:'pie', height:'1.7em', sliceColors:['#ECB9AE', '#78C469'] } );
    };
};