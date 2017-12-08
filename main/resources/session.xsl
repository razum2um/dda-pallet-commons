<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="map">
  <span class="clj-map-click2open"><xsl:value-of select="@type"/> (click to inspect)</span>
  <div class="clj-map">
    <span class="clj-map-click2close">
      <xsl:value-of select="@type"/> (click to collapse)
    </span>
    <br />
    <xsl:apply-templates/>
  </div>
</xsl:template>

<xsl:template match="key">
    <span class="clj-linehelper"></span>
    <xsl:choose><xsl:when test="@name">
      <span class="clj-key">
        <xsl:value-of select="@name"/>
      </span>
    </xsl:when></xsl:choose>
    <span class="clj-val">
      <xsl:apply-templates/>
    </span>
  <br />
</xsl:template>


<xsl:template match="/">
  <html>

  <head>
    <!-- Styles for showing the actions -->
    <style>
      .actiontable {border-left: 1px solid black; margin: 3px; width: 80%}
      .actiontable td {vertical-align: top}
      .actiontable td:first-of-type {width: 80px; padding: 5px;}
      .container {border: 1px dashed gray; padding: 5px; width: 80%;}
      .container .content { max-height: 300px; max-width: 1600px; overflow:scroll; }
      .container .header {cursor: pointer}
      .closed {display:none}
    </style>
    <!-- Styles for raw-printing clojure maps/collections -->
    <style>
      .clj-map {margin-left: 20px; padding: 5px; border-left: 1px dashed; font-family: sans; font-size: 10pt; display: none;}
      .clj-key {min-width: 150px; display: inline-block; font-family: monospace; font-size: 10pt }
      .clj-val { font-family: monospace; font-size: 10pt }
      .clj-map-click2open {color: black; font-family: monospace; font-size: 10pt; font-style: italic; cursor: pointer;}
      .clj-map-click2close, .expandall, .collapsall { cursor: pointer;}
      .clj-linehelper {margin-top: 0.7em; margin-left: -5px; height: 0.3em; width: 20px; border-top: 1px dashed; display: inline-block; }
    </style>
    <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
  </head>
  <body>
  <h2>Pallet Session Explain</h2>

  <h3>Raw Session Prettyprint</h3>
  <div>
    <a class="expandall">Expand all</a> // 
    <a class="collapsall">Collaps all</a><br />
    <br />
    <xsl:apply-templates select="/session/session-data-map" />
  </div>

  <h3>Phases</h3>
  <ul>
    <xsl:for-each select="session/phases/phase">
        <li><xsl:value-of select="text()"/></li>
    </xsl:for-each>
  </ul>

  <h3>Groups</h3>
  <ul>
  <xsl:for-each select="session/groups/group">
      <li><xsl:value-of select="text()"/></li>
  </xsl:for-each>
  </ul>

  <h3>Actions</h3>
  <ul>
    <xsl:for-each select="session/runs/node-actions">
        <li>
          Action-Results for
          Node <em><xsl:value-of select="@node"/></em> in
          Group <xsl:value-of select="@group"/> - -
          Phase <em><xsl:value-of select="@phase"/></em>
          <xsl:for-each select="action-result">
            <p>Action in Context <em><xsl:value-of select="context"/></em>
            <table class='actiontable'>
              <tr>
              	<td>Symbol</td>
              	<td>
              		<div class="container"><xsl:value-of select="action-symbol"/></div>
              	</td>
              </tr>

              <xsl:if test="script">
              <tr>
                <td>Script</td>
                <td>
                  <div class="container">
                    <div class="header">(Click to expand/collapse)</div>
                    <div class="content closed"><pre><xsl:value-of select="script"/></pre></div>
                  </div>
                </td>
              </tr>
              </xsl:if>

              <xsl:if test="out">
              <tr>
                <td>Result</td>
                <td>
                  <div class="container">
                    <div class="header">(Click to expand/collapse)</div>
                    <div class="content closed"><pre><xsl:value-of select="out"/></pre></div>
                  </div>
                </td>
              </tr>
              </xsl:if>

              <xsl:if test="exit">
              <tr><td>Exitcode</td><td><div class="container"><xsl:value-of select="exit"/></div></td></tr>
              </xsl:if>
              

              <xsl:if test="details">
              <tr>
                <td>Details</td>
                <td>
                  <div class="container"><xsl:apply-templates select="details" /></div>
                </td>
              </tr>
              </xsl:if>

            </table></p>
          </xsl:for-each>

        </li>
    </xsl:for-each>
  </ul>

  <!-- Scripts for expanding/collapsing actions -->
  <script>
    $(".header").click(function () {
      $container = $(this);
      $content = $container.parent().find(".content");
      $content.slideToggle(100);
    });
  </script>

  <!-- Scripts for expanding/collapsing raw clojure maps -->
  <script>
    $(".expandall").click(function () {
      $(this).parent().find(".clj-map").slideDown(100);
      $(this).parent().find(".clj-map-click2open").hide();
    });
    $(".collapsall").click(function () {
      $(this).parent().find(".clj-map").slideUp(100);
      $(this).parent().find(".clj-map-click2open").show();
    });
    $(".clj-key").click(function () {
      $content = $(this).next().children(".clj-map");
      $element = $(this).next().children(".clj-map-click2open");
      $content.slideToggle(100);
      $element.toggle();
    });
    $(".clj-map-click2open").click(function () {
      $element = $(this);
      $content = $element.parent().children(".clj-map");
      $content.slideToggle(100);
      $element.toggle();
    });
    $(".clj-map-click2close").click(function () {
      $container = $(this);
      $content = $container.parent();
      $content.slideToggle(100);
      $element = $(this).parent().parent().children(".clj-map-click2open");
      $element.toggle();
    });
  </script>

  </body>
  </html>
</xsl:template>

</xsl:stylesheet>
