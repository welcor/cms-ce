function queryContent( contentKey )
{
    openQueryContentWindow();
    $( '#queryContent' ).load( baseUrl + "/adminpage?page=914&op=indexMonitor&queryContent=true&contentKey=" + contentKey );
}

function closeQueryContentWindow()
{
    $( "#queryContent-window" ).hide();
}

function openQueryContentWindow()
{
    $( "#queryContent-window" ).show();
}

function loadMeasureList( orderBy )
{
    $( '#measuresList' ).load( baseUrl + "/adminpage?page=914&op=indexMonitor&measuresList=true&orderby=" + orderBy );
}

function loadDiffList()
{
    $( '#diffList' ).load( baseUrl + "/adminpage?page=914&op=indexMonitor&diffList=true" );
}

function loadQueryWindow()
{
    $( '#queryContent' ).load( baseUrl + "/adminpage?page=914&op=indexMonitor&queryContent=true&contentKey=47194" );
}

$( document ).ready( function ()
                     {
                         loadMeasureList( 'AvgTime' );
                         loadDiffList();
                         loadQueryWindow();

                         $( '#btnAvgTimeSort' ).click( function ()
                                                       {
                                                           loadMeasureList( 'AvgTime' );
                                                       } );

                         $( '#btnMaxTimeSort' ).click( function ()
                                                       {
                                                           loadMeasureList( 'MaxTime' );
                                                       } );

                         $( '#btnAvgTimeDiffSort' ).click( function ()
                                                           {
                                                               loadMeasureList( 'AvgTimeDiff' );
                                                           } );

                         $( '#btnTotalHitsSort' ).click( function ()
                                                         {
                                                             loadMeasureList( 'TotalHits' );
                                                         } );
                         $( '#btnRefreshDiffList' ).click( function ()
                                                           {
                                                               loadDiffList();
                                                           } );
                         $( '#btnQueryContent' ).click( function ()
                                                        {
                                                            loadQueryWindow();
                                                        } );

                     } );
