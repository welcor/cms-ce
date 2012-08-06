<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/cms/portal">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:createServicesUrl('handler','operation')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createServicesUrl('handler','operation', ())"/>
      </value>
      <value>
        <xsl:value-of select="portal:createServicesUrl('handler','operation', ('a', 3))"/>
      </value>
      <value>
        <xsl:value-of select="portal:createServicesUrl('handler','operation', 'redirect', ())"/>
      </value>
      <value>
        <xsl:value-of select="portal:createServicesUrl('handler','operation', 'redirect', ('a', 3))"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
