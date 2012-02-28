<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        >

  <xsl:template name="content_usedby">

    <xsl:param name="contentKey"/>

    <xsl:if test="$create = 0">
      <div class="tab-page" id="tab-page-usedby">
        <span class="tab">%blockUsedBy%</span>
        <script type="text/javascript" language="JavaScript" src="dwr/interface/AjaxService.js"/>
        <script type="text/javascript" language="JavaScript" src="dwr/engine.js"/>
        <script type="text/javascript" language="JavaScript">

          tabPane1.addTabPage( document.getElementById( "tab-page-usedby" ) );
          tabPane1.enablePageClickEvent();

          function handle_tabpane_onclick( pageIndex, page )
          {
            if (page &amp;&amp; page.id == "tab-page-usedby") {
              if (document.getElementById('usedBy').className == 'used-by-unloaded') {
                getContentUsedByAsHtml(<xsl:value-of select="$contentKey"/>);
              }
            }
          }

          function getContentUsedByAsHtml( contentKey )
          {
            document.getElementById('usedBy').innerHTML = "%headPleaseWait%";
            AjaxService.getContentUsedByAsHtml(contentKey, {callback:handleResponse_getContentUsedByAsHtml});
          }

          function handleResponse_getContentUsedByAsHtml( content )
          {
            var tag = document.getElementById('usedBy');
            tag.innerHTML = content;
            tag.className = 'used-by-loaded';
          }

        </script>
        <fieldset>
          <legend>&nbsp;%blockContents%&nbsp;</legend>

          <xsl:if test="/contents/relatedcontents/content[@current = 'false' and @versionkey = /contents/content/relatedcontentkeys/relatedcontentkey[@level = -1]/@versionkey]">
            <xsl:text>%msgContentInUseByNonCurrentVersion%</xsl:text>
            <br/><br/>
          </xsl:if>

          <span id="usedBy" class="used-by-unloaded">
            &nbsp;
          </span>

        </fieldset>
      </div>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
