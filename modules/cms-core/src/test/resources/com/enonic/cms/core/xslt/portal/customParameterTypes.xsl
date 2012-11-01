<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Ignore type -->
  <xsl:param name="param1">
    content
  </xsl:param>

  <!-- Ignore type -->
  <xsl:param name="param2">
    <other>content</other>
  </xsl:param>

  <!-- Expect type -->
  <xsl:param name="param3">
    <type>content</type>
  </xsl:param>

  <xsl:template match="/">
    <dummy/>
  </xsl:template>

</xsl:stylesheet>
