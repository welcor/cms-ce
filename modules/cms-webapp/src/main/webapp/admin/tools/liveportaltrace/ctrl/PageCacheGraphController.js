if ( !lpt )
{
    var lpt = {};
}

lpt.PageCacheGraphController = function ()
{
    var capacityUsageArray = new Array( 290 );

    var effectivenessArray = new Array( 290 );

    this.add = function ( capacityUsage, effectiveness )
    {
        if ( !lpt.ArrayUtility.isInitialized( capacityUsageArray ) )
        {
            lpt.ArrayUtility.initialize( capacityUsageArray, 0 );
        }
        if ( !lpt.ArrayUtility.isInitialized( effectivenessArray ) )
        {
            lpt.ArrayUtility.initialize( effectivenessArray, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( capacityUsageArray, capacityUsage );
        lpt.ArrayUtility.shiftAndAdd( effectivenessArray, effectiveness );

        $( '#graph-page-cache' ).sparkline( capacityUsageArray, {
            chartRangeMin:0,
            chartRangeMax:100,
            type:'line',
            lineColor:'#cd7058',
            fillColor:false,
            height:'2em',
            tooltipSuffix:" % capacity usage"
        } );

        $( '#graph-page-cache' ).sparkline( effectivenessArray, {
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