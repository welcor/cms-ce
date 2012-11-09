self.onmessage = function ( event )
{
    var url = "../" + event.data.url;

    var request = new XMLHttpRequest();
    request.open( 'GET', url, false );
    request.send( null );

    if ( request.status === 200 )
    {
        var message = { "operation":event.data.operation, "jsonData":request.responseText };
        self.postMessage( message );
    }
};