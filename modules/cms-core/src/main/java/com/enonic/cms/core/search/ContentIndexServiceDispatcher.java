package com.enonic.cms.core.search;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.*;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import org.springframework.util.StopWatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/8/11
 * Time: 10:46 AM
 */
public class ContentIndexServiceDispatcher implements ContentIndexService {


    ContentIndexServiceImpl newContentIndexService;

    com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService;


    private List<ExecuteTimeMeasure> executeTimeLog = new ArrayList<ExecuteTimeMeasure>();

    public int remove(ContentKey contentKey) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByCategory(CategoryKey categoryKey) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeByContentType(ContentTypeKey contentTypeKey) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void index(ContentDocument doc, boolean deleteExisting) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isIndexed(ContentKey contentKey) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ContentResultSet query(ContentIndexQuery query) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("new");
        ContentResultSet resultNew = newContentIndexService.query(query);
        stopWatch.stop();

        long newServiceTime = stopWatch.getLastTaskTimeMillis();

        stopWatch.start("old");
        ContentResultSet resultOld = oldContentIndexService.query(query);
        stopWatch.stop();

        long oldServiceTime = stopWatch.getLastTaskTimeMillis();

        executeTimeLog.add(new ExecuteTimeMeasure(newServiceTime, oldServiceTime, query.toString()));

        if (executeTimeLog.size() >= 20) {
            for (ExecuteTimeMeasure measure : executeTimeLog) {
                System.out.println(measure.toString());
            }
            executeTimeLog.clear();
        }

        return resultNew;
    }

    public IndexValueResultSet query(IndexValueQuery query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AggregatedResult query(AggregatedQuery query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createIndex() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setNewContentIndexService(ContentIndexServiceImpl newContentIndexService) {
        this.newContentIndexService = newContentIndexService;
    }

    public void setOldContentIndexService(com.enonic.cms.core.content.index.ContentIndexServiceImpl oldContentIndexService) {
        this.oldContentIndexService = oldContentIndexService;
    }

    private class ExecuteTimeMeasure {
        long newExecTime;
        long oldExecTime;

        String query;

        private ExecuteTimeMeasure(long newExecTime, long oldExecTime, String query) {
            this.newExecTime = newExecTime;
            this.oldExecTime = oldExecTime;
            this.query = query;
        }

        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("-------------------------------------------------\n\r");
            buf.append("Query: " + query + "\n\r");
            buf.append("Timeused new: " + newExecTime + "\n\r");
            buf.append("Timeused old: " + oldExecTime + "\n\r");

            String winner = newExecTime > oldExecTime ? "old" : "new";

            buf.append("Diff: " + Math.abs(newExecTime - oldExecTime) + " ms\n\r");
            buf.append("Winner: " + winner + "\n\r");
            return buf.toString();
        }
    }

}


