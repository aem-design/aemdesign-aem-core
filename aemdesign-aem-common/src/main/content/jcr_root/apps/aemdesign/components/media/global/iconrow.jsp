<%
    boolean showIcons = _properties.get("showIcons", Boolean.FALSE);
    boolean showSocial = _properties.get("showShareButton", Boolean.FALSE);
    String iconPath = DESIGN_PATH + "/images/i-" + iconType + ".png";
%>
<div class="more">
    <c:if test="<%= showIcons %>">
        <img src="<%= mappedUrl(_resourceResolver, iconPath) %>" />
    </c:if>

    <c:if test="<%= showSocial %>">
        <div class="addthis_toolbox addthis_default_style"
             addthis:url="<%= asset.getPath() %>"
             addthis:title="<%= escapeBody(displayTitle) %>">
                <a class="addthis_button_compact"></a>
        </div>
    </c:if>
</div>