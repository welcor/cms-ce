/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;
import org.jdom.Element;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.esl.util.ArrayUtil;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.portal.datasource.xml.DataSourceXmlFactory;
import com.enonic.cms.core.portal.datasource.xml.DataSourcesElement;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.core.structure.TemplateParameterType;

public class PageTemplateEntity
    implements Serializable
{
    private Integer key;

    private String name;

    private String description;

    private Date timestamp;

    private LazyInitializedJDOMDocument xmlData;

    private ResourceKey styleKey;

    private SiteEntity site;

    private ResourceKey cssKey;

    private PageTemplateType type;

    private RunAsType runAs;

    private Set<PageTemplateRegionEntity> pageTemplateRegions = new HashSet<PageTemplateRegionEntity>();

    private List<PageTemplatePortletEntity> pageTemplatePortlets = new ArrayList<PageTemplatePortletEntity>();

    private Set<ContentTypeEntity> contentTypes = new HashSet<ContentTypeEntity>();

    private transient DataSourcesElement datasources;

    public PageTemplateEntity()
    {
    }

    /**
     * Copy constructor. clones contained references for portlets, regions...
     *
     * @param source
     */
    public PageTemplateEntity( final PageTemplateEntity source )
    {
        this.key = source.key;
        this.name = source.name;
        this.description = source.description;
        this.timestamp = source.timestamp == null ? null : new Date( source.timestamp.getTime() );
        this.xmlData = (LazyInitializedJDOMDocument) source.xmlData.clone();
        this.styleKey = source.styleKey;
        this.site = source.site;
        this.cssKey = source.cssKey;
        this.type = source.type;
        this.runAs = source.runAs;

        final Map<Integer, PageTemplateRegionEntity> templateRegionTable = Maps.newConcurrentMap();
        // clone page template regions
        for ( PageTemplateRegionEntity region : source.pageTemplateRegions )
        {
            final PageTemplateRegionEntity regionClone = new PageTemplateRegionEntity( region );
            regionClone.setPageTemplate( this );
            this.pageTemplateRegions.add( regionClone );
            templateRegionTable.put( regionClone.getKey(), regionClone );
        }

        // clone page template portlets
        for ( PageTemplatePortletEntity pageTemplatePortlet : source.pageTemplatePortlets )
        {
            final PageTemplatePortletEntity pageTemplatePortletClone = new PageTemplatePortletEntity( pageTemplatePortlet );
            pageTemplatePortletClone.setPageTemplate( this );

            // make reference pointing to region in cloned portlet, point to cloned region instance
            final PageTemplateRegionEntity pageTemplateRegionClone =
                templateRegionTable.get( pageTemplatePortlet.getPageTemplateRegion().getKey() );
            pageTemplatePortletClone.setPageTemplateRegion( pageTemplateRegionClone );

            this.pageTemplatePortlets.add( pageTemplatePortletClone );
        }

        // fix references to portlets in cloned regions
        for ( PageTemplateRegionEntity region : this.pageTemplateRegions )
        {
            final Set<PageTemplatePortletEntity> clonedPortlets = Sets.newHashSet();
            for ( PageTemplatePortletEntity portlet : region.getPortlets() )
            {
                clonedPortlets.add( this.pageTemplatePortlets.get( this.pageTemplatePortlets.indexOf( portlet ) ) );
            }
            region.setPortlets( clonedPortlets );
        }

        this.contentTypes = Sets.newHashSet( source.contentTypes );
        this.datasources = source.datasources == null ? null : new DataSourcesElement( source.datasources );
    }

    public int getKey()
    {
        return key;
    }

    public PageTemplateKey getPageTemplateKey()
    {
        return new PageTemplateKey( getKey() );
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public LazyInitializedJDOMDocument getXmlData()
    {
        return xmlData;
    }

    public Document getXmlDataAsDocument()
    {
        if ( xmlData == null )
        {
            return null;
        }

        return xmlData.getDocument();
    }

    public Document getXmlDataAsJDOMDocument()
    {
        if ( xmlData == null )
        {
            return null;
        }

        return xmlData.getDocument();
    }

    public ResourceKey getStyleKey()
    {
        return styleKey;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public ResourceKey getCssKey()
    {
        return cssKey;
    }

    public PageTemplateType getType()
    {
        return type;
    }

    public RunAsType getRunAs()
    {
        return runAs;
    }

    public void addPageTemplateRegion( PageTemplateRegionEntity value )
    {
        pageTemplateRegions.add( value );
    }

    public void clearPageTemplateRegions()
    {
        pageTemplateRegions.clear();
    }

    public Set<PageTemplateRegionEntity> getPageTemplateRegions()
    {
        return pageTemplateRegions;
    }

    public void setPageTemplateRegions( final Set<PageTemplateRegionEntity> pageTemplateRegions )
    {
        this.pageTemplateRegions = pageTemplateRegions;
    }

    public void addPageTemplatePortlet( PageTemplatePortletEntity value )
    {
        pageTemplatePortlets.add( value );
    }

    public void clearPageTemplatePortlets()
    {
        pageTemplatePortlets.clear();

        // clean all regions
        for ( PageTemplateRegionEntity region : pageTemplateRegions )
        {
            region.clearPortlets();
        }
    }

    public List<PageTemplatePortletEntity> getPortlets()
    {
        return pageTemplatePortlets;
    }

    public void setPageTemplatePortlets( final List<PageTemplatePortletEntity> pageTemplatePortlets )
    {
        this.pageTemplatePortlets = pageTemplatePortlets;
    }

    public void setKey( Integer key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setXmlData( Document value )
    {
        if ( value == null )
        {
            this.xmlData = null;
        }
        else
        {
            this.xmlData = LazyInitializedJDOMDocument.parse( value );
        }
    }

    public void setXmlData( String value )
    {
        if ( value == null )
        {
            this.xmlData = null;
        }
        else
        {
            this.xmlData = new LazyInitializedJDOMDocument( value );
        }
    }

    public void setStyleKey( ResourceKey styleKey )
    {
        this.styleKey = styleKey;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setCssKey( ResourceKey cssKey )
    {
        this.cssKey = cssKey;
    }

    public void setType( PageTemplateType type )
    {
        this.type = type;
    }

    public void setRunAs( RunAsType runAs )
    {
        this.runAs = runAs;
    }

    public void setContentTypes( Set<ContentTypeEntity> contentTypes )
    {
        this.contentTypes = contentTypes;
    }

    public Set<ContentTypeEntity> getContentTypes()
    {
        return contentTypes;
    }

    public boolean supportsContentType( ContentTypeEntity contentType )
    {
        return contentTypes.contains( contentType );
    }

    public void addContentType( ContentTypeEntity contentType )
    {
        contentTypes.add( contentType );
    }

    public void clearContentTypes()
    {
        contentTypes.clear();
    }

    public Map<String, TemplateParameter> getTemplateParameters()
    {
        Map<String, TemplateParameter> params = new LinkedHashMap<String, TemplateParameter>();

        Element rootEl = getXmlDataAsJDOMDocument().getRootElement();
        @SuppressWarnings({"unchecked"}) List<Element> paramElList = rootEl.getChildren( "pagetemplateparameter" );
        for ( Element paramEl : paramElList )
        {
            TemplateParameterType type = TemplateParameterType.parse( paramEl.getAttributeValue( "type" ) );
            String name = paramEl.getAttributeValue( "name" );
            String value = paramEl.getAttributeValue( "value" );
            if ( value != null && value.length() == 0 )
            {
                value = null;
            }
            TemplateParameter templateParameter = new TemplateParameter( type, name, value );
            params.put( name, templateParameter );
        }

        return params;
    }

    public DataSourcesElement getDatasources()
    {
        if ( datasources == null )
        {
            Element rootEl = getXmlDataAsJDOMDocument().getRootElement();
            Element datasourcesEl = rootEl.getChild( "datasources" );
            datasources = new DataSourceXmlFactory().create( datasourcesEl );
        }

        return datasources;
    }

    public Element getDocumentElement()
    {
        Document doc = getXmlDataAsJDOMDocument();
        Element rootEl = doc.getRootElement();
        return rootEl.getChild( "document" );
    }


    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplateEntity ) )
        {
            return false;
        }

        PageTemplateEntity that = (PageTemplateEntity) o;

        if ( key != that.getKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 631, 567 ).append( key ).toHashCode();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "key = " ).append( getKey() ).append( ", name = '" ).append( getName() ).append( "'" );
        return s.toString();
    }


    public void removePageTemplParams( final int[] droppedRegionKeys )
    {
        final Set<PageTemplateRegionEntity> regions = new HashSet<PageTemplateRegionEntity>();

        for ( final PageTemplateRegionEntity region : pageTemplateRegions )
        {
            if ( !ArrayUtil.contains( droppedRegionKeys, region.getKey() ) )
            {
                regions.add( region );
            }
        }

        clearPageTemplateRegions();
        pageTemplateRegions.addAll( regions );
    }

    public PageTemplateRegionEntity findRegionByKey( int regionKey )
    {
        for ( PageTemplateRegionEntity region : pageTemplateRegions )
        {
            if ( regionKey == region.getKey() )
            {
                return region;
            }
        }

        return null;
    }

    public UserEntity resolveRunAsUser( UserEntity currentUser )
    {
        if ( currentUser.isAnonymous() )
        {
            // Anonymous user cannot run as any other user
            return currentUser;
        }

        RunAsType runAsType = getRunAs();

        if ( runAsType.equals( RunAsType.PERSONALIZED ) )
        {
            return currentUser;
        }
        else if ( runAsType.equals( RunAsType.DEFAULT_USER ) )
        {
            if ( getSite().resolveDefaultRunAsUser() != null )
            {
                return getSite().resolveDefaultRunAsUser();
            }
            return null;
        }
        else if ( runAsType.equals( RunAsType.INHERIT ) )
        {
            return null;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported runAsType: " + runAsType );
        }
    }
}
