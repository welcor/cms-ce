/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import com.enonic.vertical.adminweb.VerticalAdminLogger;

public final class WizardLogger
    extends VerticalAdminLogger
{
    public static void errorWizard( String message, Object msgData )
    {
        error( message, msgData, null );
        throw new WizardException( format( message, msgData ), null );
    }

    public static void errorWizard( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new WizardException( message, throwable );
    }
}
