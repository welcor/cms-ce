<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://saxon.sf.net/"
    >

	<xsl:include href="../common/convert_filesize.xsl"/>

	<xsl:template match="filesize" mode="display">
		<xsl:param name="contentxpath"/>
		
		<xsl:call-template name="convert_filesize">
			<xsl:with-param name="fsize" select="saxon:evaluate(concat($contentxpath, @xpath))"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
