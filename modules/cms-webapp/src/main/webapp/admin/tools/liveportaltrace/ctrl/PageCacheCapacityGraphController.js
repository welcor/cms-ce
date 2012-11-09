if ( !lpt )
{
    var lpt = {};
}

lpt.PageCacheCapacityGraphController = function ()
{
    var pageCacheCapacityGraphValues = new Array( 170 );

    this.add = function ( count, capacity, hitCount, missCount )
    {
        if ( !lpt.ArrayUtility.isInitialized( pageCacheCapacityGraphValues ) )
        {
            lpt.ArrayUtility.initialize( pageCacheCapacityGraphValues, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( pageCacheCapacityGraphValues, count );

        $( '#graph-page-cache-capacity' ).sparkline( pageCacheCapacityGraphValues,
                                                     {chartRangeMin:0, chartRangeMax:capacity, type:'line', lineColor:'#70A5A9', fillColor:'#8ED0D5', height:'2em'} );

        $( '#graph-page-cache-hits-vs-misses' ).sparkline( [missCount, hitCount],
                                                           {type:'pie', height:'1.7em', sliceColors:['#ECB9AE', '#78C469']} );
    };
};