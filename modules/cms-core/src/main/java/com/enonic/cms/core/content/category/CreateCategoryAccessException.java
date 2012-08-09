package com.enonic.cms.core.content.category;


import com.enonic.cms.core.security.user.QualifiedUsername;

public class CreateCategoryAccessException
    extends RuntimeException
{
    public CreateCategoryAccessException( String reason, QualifiedUsername user )
    {
        super( buildMessage( reason, user ) );
    }

    private static String buildMessage( String reason, QualifiedUsername user )
    {
        StringBuilder msg = new StringBuilder();
        msg.append( " User " ).append( user ).append( " do not have access to create category: " ).append( reason );
        return msg.toString();
    }
}
