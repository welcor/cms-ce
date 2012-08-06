<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/cms/portal">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:createWindowUrl()"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl(())"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl(('a', 3))"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl('window-key', ())"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl('window-key', ('a', 3))"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl('window-key', (), 'xml')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createWindowUrl('window-key', ('a', 3), 'xml')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
