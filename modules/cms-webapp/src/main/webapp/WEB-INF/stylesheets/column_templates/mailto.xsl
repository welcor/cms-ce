<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">

	<xsl:template match="mailto" mode="display">
		<xsl:param name="contentxpath"/>
		<a>
			<xsl:attribute name="href">
				<xsl:text>mailto:</xsl:text>
				<xsl:value-of select="admin:evaluate(concat($contentxpath, @emailxpath))"/>
			</xsl:attribute>
			<xsl:value-of select="admin:evaluate(concat($contentxpath, @namexpath))"/>
		</a> 

	</xsl:template>
	
	<xsl:template match="mailto" mode="clickable">
		<xsl:text>false</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
