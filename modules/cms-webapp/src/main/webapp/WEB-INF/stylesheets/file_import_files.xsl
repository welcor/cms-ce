<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

    <xsl:output method="html" indent="yes"/>
    
    <xsl:include href="content_import_base.xsl"/>

    <xsl:variable name="importtype" select="'file'"/>

    <xsl:template name="step1_description">
      <xsl:text>%txtDescSelectFoldersAndFiles%</xsl:text>
    </xsl:template>

</xsl:stylesheet>
