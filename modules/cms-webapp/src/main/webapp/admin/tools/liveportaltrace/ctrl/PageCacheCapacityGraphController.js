if ( !lpt )
{
    var lpt = {};
}

lpt.PageCacheCapacityGraphController = function ()
{
    var pageCapacityUsageArray = new Array( 290 );

    var pageEffectivenessArray = new Array( 290 );

    this.add = function ( capacityUsage, effectiveness )
    {
        if ( !lpt.ArrayUtility.isInitialized( pageCapacityUsageArray ) )
        {
            lpt.ArrayUtility.initialize( pageCapacityUsageArray, 0 );
        }
        if ( !lpt.ArrayUtility.isInitialized( pageEffectivenessArray ) )
        {
            lpt.ArrayUtility.initialize( pageEffectivenessArray, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( pageCapacityUsageArray, capacityUsage );
        lpt.ArrayUtility.shiftAndAdd( pageEffectivenessArray, effectiveness );

        $( '#graph-page-cache' ).sparkline( pageCapacityUsageArray, {
            chartRangeMin:0,
            chartRangeMax:100,
            type:'line',
            lineColor:'#cd7058',
            fillColor:false,
            height:'2em',
            tooltipSuffix:" % capacity usage"
        } );

        $( '#graph-page-cache' ).sparkline( pageEffectivenessArray, {
            chartRangeMin:0,
            chartRangeMax:100,
            type:'line',
            lineColor:'#79c36a',
            fillColor:false,
            height:'2em',
            tooltipSuffix:" % effectiveness",
            composite:true
        } );
    };
};