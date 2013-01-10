function reindex( startURL, baseURL ) {
    $('.progress').html('<div class="bar" style="width: 0"></div>');

    $('.operation_button').attr("disabled", "disabled");

    if ( startURL ) {
        $.post( startURL );
    }

    var timerId = setInterval(function () {
        $.post( baseURL + "/tools/reindexContent??op=custom&info=progress" ).done(function(data) {
            if (startURL) {
                $('.bar').css('width', data.percent + '%');
            } else {
                $('.progress').html('<div class="bar" style="width: '+data.percent+'%"></div>');
                startURL = '*';
            }
            $('#message').html(data.logLine);

            if (data.inProgress == false)
            {
                clearInterval( timerId );

                setTimeout(function(){
                    $('#message').html('Finished. Last reindex was executed at ' + new Date().toTimeString() );
                    $('.operation_button').removeAttr("disabled");
                }, 500);
            }
        });
    }, 100);
}


