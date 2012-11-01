<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="param1" select="'1'"/>
  <xsl:param name="param2" select="'2'"/>

  <xsl:template match="/">
    <dummy>
      <xsl:attribute name="a" select="$param1"/>
      <xsl:attribute name="b" select="$param2"/>
    </dummy>
  </xsl:template>

</xsl:stylesheet>
