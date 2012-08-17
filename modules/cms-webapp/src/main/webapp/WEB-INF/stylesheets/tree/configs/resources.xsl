<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

	<xsl:template match="resources" mode="icon">
        <xsl:text>icon_folder_resources</xsl:text>
	</xsl:template>
	
	<xsl:template match="resources" mode="text">
        <xsl:text>%mnuResources%</xsl:text>
	</xsl:template>
	
	<xsl:template match="resources" mode="page">
		<xsl:text>800</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>