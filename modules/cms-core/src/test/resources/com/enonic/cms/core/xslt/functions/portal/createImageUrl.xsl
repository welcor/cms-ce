<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/cms/portal">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:createImageUrl('image-key')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createImageUrl('image-key', 'filter')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createImageUrl('image-key', 'filter', 'background')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createImageUrl('image-key', 'filter', 'background', 'format')"/>
      </value>
      <value>
        <xsl:value-of select="portal:createImageUrl('image-key', 'filter', 'background', 'format', 'quality')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
