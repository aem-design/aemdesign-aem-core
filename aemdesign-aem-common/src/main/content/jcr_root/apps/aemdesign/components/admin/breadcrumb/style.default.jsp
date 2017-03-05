<%

    // get starting point of trail
    long level = currentStyle.get("absParent", 2L);
    long endLevel = currentStyle.get("relParent", 1L);
    String delimStr = currentStyle.get("delim", "");
    String trailStr = currentStyle.get("trail", "");
    int currentLevel = currentPage.getDepth();
    String delim = "";
%>
<div ${componentProperties.componentAttributes}>
    <ul>
        <%
            while (level < currentLevel - endLevel) {
                Page trail = currentPage.getAbsoluteParent((int) level);
                if (trail == null) {
                    break;
                }
                if (trail != null && trail.isHideInNav()) {

                    delim = delimStr;
                    level++;
                    continue;

                }
                String title = trail.getNavigationTitle();
                if (title == null || title.equals("")) {
                    title = trail.getNavigationTitle();
                }
                if (title == null || title.equals("")) {
                    title = trail.getTitle();
                }
                if (title == null || title.equals("")) {
                    title = trail.getName();
                }
        %><%= xssAPI.filterHTML(delim) %><%
    %><li><a href="<%= xssAPI.getValidHref(trail.getPath()+".html") %>"
             onclick="CQ_Analytics?CQ_Analytics.record({event:'followBreadcrumb',values: { breadcrumbPath: '<%= xssAPI.getValidHref(trail.getPath()) %>' },collect: false,options: { obj: this },componentPath: '<%=resource.getResourceType()%>'}):true;"><%
    %><%= xssAPI.encodeForHTML(title) %><%
    %></a></li><%

            delim = delimStr;
            level++;
        }
        if (trailStr.length() > 0) {
    %><%= xssAPI.filterHTML(trailStr) %><%
        }
    %>
    </ul>
</div>