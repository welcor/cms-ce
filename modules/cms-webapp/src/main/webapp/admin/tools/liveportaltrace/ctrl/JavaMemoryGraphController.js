if ( !lpt )
{
    var lpt = {};
}

lpt.JavaMemoryGraphController = function ()
{
    var memoryGraphValues = new Array( 290 );

    this.add = function ( used, max )
    {
        if ( !lpt.ArrayUtility.isInitialized( memoryGraphValues ) )
        {
            lpt.ArrayUtility.initialize( memoryGraphValues, 0 );
        }

        var usageInPercent = Math.round( 100 * used / max );

        lpt.ArrayUtility.shiftAndAdd( memoryGraphValues, usageInPercent );

        $( '#graph-memory' ).sparkline( memoryGraphValues, {
            chartRangeMin:0,
            chartRangeMax:100,
            type:'line',
            lineColor:'#939F74',
            fillColor:'#ECFFBB',
            height:'2em',
            tooltipSuffix:" % used"
        } );
    };
};