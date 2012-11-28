package com.enonic.cms.core.vhost;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FileWatcherByTimer
    extends TimerTask
{
    private long timeStamp;

    private File file;

    private Runnable listener;

    public FileWatcherByTimer( File file, Runnable listener, int period )
    {
        this.file = file;
        this.listener = listener;
        this.timeStamp = file.lastModified();

        if ( this.file != null && this.file.exists() )
        {
            // repeat the check every second
            new Timer().schedule( this, new Date(), period );
        }
    }

    public final void run()
    {
        long timeStamp = file.lastModified();

        if ( this.timeStamp != timeStamp )
        {
            this.timeStamp = timeStamp;
            listener.run();
        }
    }
}
