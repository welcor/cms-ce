<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">
	
	<xsl:include href="../common/getsuffix.xsl"/>

    <xsl:template match="filename" mode="display">
        <xsl:param name="contentxpath"/>
        <div style="font-weight: bold">
            <xsl:value-of select="admin:evaluate(concat($contentxpath, @xpath))"/>
        </div>
        <span style="color: gray">
            <xsl:if test="admin:evaluate(concat($contentxpath, '@repositorypath'))">
                <xsl:value-of select="admin:evaluate(concat($contentxpath, '@repositorypath'))"/>
                <xsl:text>/</xsl:text>
            </xsl:if>
            <xsl:value-of select="admin:evaluate(concat($contentxpath, 'name'))"/>
        </span>
    </xsl:template>

	
	<xsl:template match="filename" mode="title">
		<xsl:text>%fldName%</xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
