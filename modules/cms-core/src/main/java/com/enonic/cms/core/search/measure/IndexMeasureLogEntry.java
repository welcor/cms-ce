package com.enonic.cms.core.search.measure;

import com.enonic.cms.core.content.index.ContentIndexQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/8/11
 * Time: 2:54 PM
 */
public class IndexMeasureLogEntry {

    long newExecTime;
    long oldExecTime;

    ContentIndexQuery query;

    public IndexMeasureLogEntry(long newExecTime, long oldExecTime, ContentIndexQuery query) {
        this.newExecTime = newExecTime;
        this.oldExecTime = oldExecTime;
        this.query = query;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("**\n\r");
        buf.append("Query: " + query.toString() + "\n\r");
        buf.append("Timeused new: " + newExecTime + "\n\r");
        buf.append("Timeused old: " + oldExecTime + "\n\r");

        String winner = newExecTime > oldExecTime ? "old" : "new";

        buf.append("Diff: " + getDiff() + " ms\n\r");
        buf.append("Winner: " + winner + "\n\r");
        return buf.toString();
    }

    private long getDiff() {
        return Math.abs(newExecTime - oldExecTime);
    }

    public boolean newWon() {
        return newExecTime - oldExecTime <= 0;
    }

    public long getNewExecTime() {
        return newExecTime;
    }

    public long getOldExecTime() {
        return oldExecTime;
    }

    public ContentIndexQuery getQuery() {
        return query;
    }
}