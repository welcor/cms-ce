<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">

	<xsl:template match="number" mode="display">
		<xsl:param name="contentxpath"/>
		
		<xsl:variable name="xpath">
			<xsl:value-of select="$contentxpath"/>
			<xsl:value-of select="."/>
		</xsl:variable>
		
		<xsl:value-of select="format-number(round(100*number(admin:evaluate($xpath))) div 100, '0.00')"/>
	</xsl:template>
	
	<xsl:template match="number" mode="orderby">
		<xsl:param name="indexingxpath"/>
		
		<xsl:variable name="fullxpath">
			<xsl:value-of select="$indexingxpath"/>
			<xsl:text>index[@xpath='</xsl:text>
			<xsl:value-of select="."/>
			<xsl:text>']</xsl:text>
		</xsl:variable>
		
		<xsl:if test="admin:evaluate($fullxpath)">
			<xsl:value-of select="."/>
		</xsl:if>
		
	</xsl:template>
	
	<xsl:template match="number" mode="titlealign">
		<xsl:text>center</xsl:text>
	</xsl:template>

	<xsl:template match="number" mode="columnalign">
		<xsl:text>right</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>