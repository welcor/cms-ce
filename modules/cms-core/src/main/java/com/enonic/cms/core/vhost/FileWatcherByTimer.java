package com.enonic.cms.core.vhost;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

final class FileWatcherByTimer
    extends TimerTask
{
    private long timeStamp;

    private final File file;

    private final Runnable listener;

    private final Timer timer;

    public FileWatcherByTimer( final File file, final Runnable listener, final int period )
    {
        this.file = file;
        this.listener = listener;
        this.timeStamp = file.lastModified();
        this.timer = new Timer( "VHost File Monitor" );

        if ( this.file.exists() )
        {
            this.timer.schedule( this, new Date(), period );
        }
    }

    public void run()
    {
        long timeStamp = file.lastModified();

        if ( this.timeStamp != timeStamp )
        {
            this.timeStamp = timeStamp;
            listener.run();
        }
    }

    public void stop()
    {
        this.timer.cancel();
    }
}
