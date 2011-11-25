package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/2/11
 * Time: 1:07 PM
 */
public enum IndexType
{
    Content,
    Binaries,
    Customdata;


    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }
}
