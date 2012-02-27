package com.enonic.cms.core.search.IndexPerformance;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 11:10 AM
 */
public class IndexQueryStats
    implements Serializable
{

    private String query;

    private List<IndexQuerySourceStats> sourceStats;


    public IndexQueryStats( String query )
    {
        this.query = query;
    }

    public void setSourceStats( List<IndexQuerySourceStats> sourceStats )
    {
        this.sourceStats = sourceStats;
    }

    public String getQuery()
    {
        return query;
    }

    public List<IndexQuerySourceStats> getSourceStats()
    {
        return sourceStats;
    }
}
