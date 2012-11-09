if ( !lpt )
{
    var lpt = {};
}

lpt.JavaMemoryGraphController = function ()
{
    var memoryGraphValues = new Array( 170 );

    this.add = function ( used, max )
    {
        if ( !lpt.ArrayUtility.isInitialized( memoryGraphValues ) )
        {
            lpt.ArrayUtility.initialize( memoryGraphValues, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( memoryGraphValues, used );

        $( '#graph-memory' ).sparkline( memoryGraphValues,
                                        {chartRangeMin:0, chartRangeMax:max, type:'line', lineColor:'#939F74', fillColor:'#ECFFBB', height:'2em'} );
    };
};