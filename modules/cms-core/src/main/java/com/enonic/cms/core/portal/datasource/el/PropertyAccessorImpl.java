/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el;

import java.util.Map;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;

import com.enonic.cms.core.portal.datasource.el.accessors.Accessor;

/**
 * does not throw Exception if map does not contain key
 */
final class PropertyAccessorImpl
    extends ReflectivePropertyAccessor
{
    @Override
    public TypedValue read( EvaluationContext context, Object target, String name )
        throws AccessException
    {
        if ( target instanceof Accessor )
        {
            final Accessor accessor = (Accessor) target;
            final Object value = accessor.getValue( name );

            if ( value == null )
            {
                return TypedValue.NULL;
            }

            return new TypedValue( value );
        }
        else if ( target instanceof Map )
        {
            final Map map = (Map) target;
            final Object value = map.get( name );

            if ( value == null && !map.containsKey( name ) )
            {
                return TypedValue.NULL;
            }

            return new TypedValue( value );
        }

        return super.read( context, target, name );
    }

    @Override
    public boolean canRead( EvaluationContext context, Object target, String name )
        throws AccessException
    {
        return target instanceof Accessor || target instanceof Map || super.canRead( context, target, name );
    }

    @Override
    public boolean canWrite( EvaluationContext context, Object target, String name )
        throws AccessException
    {
        return false;
    }
}
