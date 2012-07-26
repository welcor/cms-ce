package com.enonic.cms.core.content.index.queryexpression;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.store.dao.ContentTypeDao;

public class ContentTypeEvaluator
    extends QueryEvaluatorAdapter
{
    private ContentTypeDao contentTypeDao;

    public ContentTypeEvaluator( ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    public Object evaluate( CompareExpr expr )
    {
        Expression left = (Expression) expr.getLeft().evaluate( this );

        if ( isContentType( left ) )
        {
            int operator = expr.getOperator();

            if ( CompareExpr.EQ == operator || CompareExpr.NEQ == operator )
            {
                return populateEqual( expr );
            }
            else if ( CompareExpr.IN == operator || CompareExpr.NOT_IN == operator )
            {
                return populateIn( expr );
            }
        }

        return expr;
    }

    private Object populateEqual( CompareExpr expr )
    {
        Expression right = (Expression) expr.getRight().evaluate( this );

        if ( right instanceof ValueExpr )
        {
            ValueExpr valueExpr = (ValueExpr) right;

            if ( valueExpr.isString() )
            {
                String contentTypeName = (String) valueExpr.getValue();
                ContentTypeEntity type = contentTypeDao.findByName( contentTypeName );

                if ( type != null )
                {
                    return new CompareExpr( expr.getOperator(), new FieldExpr( "contenttypekey" ), new ValueExpr( type.getKey() ) );
                }
            }
        }
        return expr;
    }

    private Object populateIn( CompareExpr expr )
    {
        Expression right = (Expression) expr.getRight().evaluate( this );

        if ( right instanceof ArrayExpr )
        {
            ArrayExpr arrayExpr = (ArrayExpr) right;
            ValueExpr[] values = arrayExpr.getValues();

            List<ValueExpr> keyList = new ArrayList<ValueExpr>( values.length );

            for ( ValueExpr valueExpr : values )
            {
                if ( valueExpr.isString() )
                {
                    String contentTypeName = (String) valueExpr.getValue();
                    ContentTypeEntity type = contentTypeDao.findByName( contentTypeName );

                    if ( type == null )
                    {
                        return expr;
                    }
                    keyList.add( new ValueExpr( type.getKey() ) );
                }
            }

            ValueExpr[] keyArray = keyList.toArray( new ValueExpr[keyList.size()] );
            return new CompareExpr( expr.getOperator(), new FieldExpr( "contenttypekey" ), new ArrayExpr( keyArray ) );

        }
        return expr;
    }

    private boolean isContentType( Expression expr )
    {
        return ( expr instanceof FieldExpr ) && ( (FieldExpr) expr ).isContentType();
    }
}
