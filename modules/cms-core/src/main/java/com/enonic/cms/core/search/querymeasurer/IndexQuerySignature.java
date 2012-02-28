package com.enonic.cms.core.search.querymeasurer;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 1:49 PM
 */
public class IndexQuerySignature
{
    private final String queryEqualsValue;

    private String index;

    private String count;

    private String categoryAccessTypeFilter;

    private String contentStatusFilter;

    private String contentOnlineAtFilter;

    private String contentFilter;

    private String sectionFilter;

    private String categoryFilter;

    private String contentTypeFilter;

    private String securityFilter;

    public IndexQuerySignature( String querySignatureValue )
    {
        this.queryEqualsValue = querySignatureValue;
    }

    public String getQueryDisplayValue()
    {
        return queryEqualsValue;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        IndexQuerySignature that = (IndexQuerySignature) o;

        if ( queryEqualsValue != null ? !queryEqualsValue.equals( that.queryEqualsValue ) : that.queryEqualsValue != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return queryEqualsValue != null ? queryEqualsValue.hashCode() : 0;
    }
}
