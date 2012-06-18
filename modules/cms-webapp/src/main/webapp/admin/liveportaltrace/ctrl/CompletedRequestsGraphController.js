if ( !lpt )
{
    var lpt = {};
}

lpt.CompletedRequestsGraphController = function ()
{
    var completedRequestsGraphValues = new Array( 270 );
    var endTimeOfLastRecordedCompletedRequest = null;
    var peakCompletedRequestsPrSecond = 0;
    var lastSecondCounter = 0;

    this.add = function ( portalRequestTraceRows )
    {
        if ( !lpt.ArrayUtility.isInitialized( completedRequestsGraphValues ) )
        {
            lpt.ArrayUtility.initialize( completedRequestsGraphValues, 0 );
        }

        if ( portalRequestTraceRows.length === 0 )
        {
            lpt.ArrayUtility.shiftAndAdd( completedRequestsGraphValues, lastSecondCounter );
            lastSecondCounter = 0;
        }
        else
        {
            for ( var key in portalRequestTraceRows )
            {
                var row = portalRequestTraceRows[key];
                var portalRequestTrace = row.portalRequestTrace;

                var currTime = new Date( portalRequestTrace.duration.stopTime );

                if ( endTimeOfLastRecordedCompletedRequest == null )
                {
                    // first recording
                    lpt.ArrayUtility.shiftAndAdd( completedRequestsGraphValues, lastSecondCounter );
                    lastSecondCounter = 0;
                }
                else if ( isWithinSameSecond( currTime, endTimeOfLastRecordedCompletedRequest ) )
                {
                    // within same second, increment last with one
                    lastSecondCounter++;
                }
                else
                {
                    // new second, record possible peak
                    if ( lastSecondCounter > peakCompletedRequestsPrSecond )
                    {
                        peakCompletedRequestsPrSecond = lastSecondCounter;
                        $( "#peak-number-of-completed-request-pr-second" ).html( peakCompletedRequestsPrSecond );
                    }
                    lpt.ArrayUtility.shiftAndAdd( completedRequestsGraphValues, lastSecondCounter );
                    lastSecondCounter = 1;
                }
                endTimeOfLastRecordedCompletedRequest = currTime;
            }
        }

        $( "#last-number-of-completed-request-pr-second" ).html( completedRequestsGraphValues[completedRequestsGraphValues.length - 1] );

        $( '#graph-completed-requests-pr-second' ).sparkline( completedRequestsGraphValues,
                                                              {chartRangeMin:0, chartRangeMax:100, type:'line', lineColor:'#939F74', fillColor:'#ECFFBB', height:'2em'} );
    };

    function isWithinSameSecond( date1, date2 )
    {
        if ( date1.getFullYear() !== date2.getFullYear() )
        {
            return false;
        }
        else if ( date1.getMonth() !== date2.getMonth() )
        {
            return false;
        }
        else if ( date1.getDate() !== date2.getDate() )
        {
            return false;
        }
        else if ( date1.getHours() !== date2.getHours() )
        {
            return false;
        }
        else if ( date1.getMinutes() !== date2.getMinutes() )
        {
            return false;
        }
        else if ( date1.getSeconds() !== date2.getSeconds() )
        {
            return false;
        }

        return true;
    }
};

