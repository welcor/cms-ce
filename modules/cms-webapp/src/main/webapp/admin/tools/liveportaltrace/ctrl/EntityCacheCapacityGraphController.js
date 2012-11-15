if ( !lpt )
{
    var lpt = {};
}

lpt.EntityCacheCapacityGraphController = function ()
{
    var entityCapacityUsageArray = new Array( 290 );

    var entityEffectivenessArray = new Array( 290 );

    this.add = function ( capacityUsage, effectiveness )
    {
        if ( !lpt.ArrayUtility.isInitialized( entityCapacityUsageArray ) )
        {
            lpt.ArrayUtility.initialize( entityCapacityUsageArray, 0 );
        }
        if ( !lpt.ArrayUtility.isInitialized( entityEffectivenessArray ) )
        {
            lpt.ArrayUtility.initialize( entityEffectivenessArray, 0 );
        }

        lpt.ArrayUtility.shiftAndAdd( entityCapacityUsageArray, capacityUsage );
        lpt.ArrayUtility.shiftAndAdd( entityEffectivenessArray, effectiveness );

        $( '#graph-entity-cache' ).sparkline( entityCapacityUsageArray, {
            chartRangeMin:0,
            chartRangeMax:100,
            type:'line',
            lineColor:'#cd7058',
            fillColor:false,
            height:'2em',
            tooltipSuffix:" % capacity usage"
        } );

        $( '#graph-entity-cache' ).sparkline( entityEffectivenessArray, {
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