package com.enonic.cms.web.main;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
public class MainWebBeans
    extends WebMvcConfigurerAdapter
{
    @Override
    public void configureDefaultServletHandling( final DefaultServletHandlerConfigurer configurer )
    {
        configurer.enable();
    }

    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer()
    {
        final Properties settings = new Properties();
        settings.setProperty( "number_format", "0.######" );
        settings.setProperty( "default_encoding", "UTF-8" );

        final FreeMarkerConfigurer bean = new FreeMarkerConfigurer();
        bean.setTemplateLoaderPath( "/WEB-INF/freemarker/" );
        bean.setFreemarkerSettings( settings );
        return bean;
    }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver()
    {
        final FreeMarkerViewResolver bean = new FreeMarkerViewResolver();
        bean.setCache( true );
        bean.setPrefix( "" );
        bean.setSuffix( ".ftl" );
        bean.setExposeSpringMacroHelpers( true );
        return bean;
    }
}
