package com.enonic.cms.core.search.measure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/8/11
 * Time: 2:55 PM
 */
public class IndexTimeMeasurer {

    private final static long threshold_ms = 50;
    private final static int numberOfHitsBeforeLog = 10;

    private List<IndexMeasureLogEntry> contentIndexTimeLog = new ArrayList<IndexMeasureLogEntry>();

    private int newWon = 0;
    private int oldWon = 0;
    private int total = 0;
    private long totalTimeNew = 0;
    private long totalTimeOld = 0;
    private int currentNumber = 0;


    private Map<String, Long> newExecTimeMap = new HashMap<String, Long>();

    public synchronized void add(IndexMeasureLogEntry entry) {

        total++;
        currentNumber++;

        if (entry.newWon()) {
            newWon++;
        } else {
            oldWon++;
        }

        totalTimeNew = totalTimeNew + entry.getNewExecTime();
        totalTimeOld = totalTimeOld + entry.getOldExecTime();

        if (Math.abs(entry.getNewExecTime() - entry.getOldExecTime()) > threshold_ms) {
            this.contentIndexTimeLog.add(entry);
        }

        if (currentNumber >= numberOfHitsBeforeLog) {
            System.out.println(this.toString());
            currentNumber = 0;
            contentIndexTimeLog.clear();
        }

    }

    public List<IndexMeasureLogEntry> getExecuteTimeLog() {
        return contentIndexTimeLog;
    }

    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer();

        buf.append("--------------------------------" + "\n\r");
        buf.append("Total hits: " + total + "\n\r");
        buf.append("New won: " + newWon + "\n\r");
        buf.append("Old won: " + oldWon + "\n\r");
        buf.append("TotalTime new: " + totalTimeNew + ", avg: " + totalTimeNew / total + "\n\r");
        buf.append("TotalTime old: " + totalTimeOld + ", avg: " + totalTimeOld / total + "\n\r");

        if (contentIndexTimeLog.size() > 0) {

            buf.append("Queries with diff > " + threshold_ms + " ms:\n\r");

            for (IndexMeasureLogEntry measure : contentIndexTimeLog) {
                buf.append(measure.toString());
            }

        }
        return buf.toString();
    }


}
