<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="querylistdata.jsp" %>
<%
    // This is here to stop statically defined pages from not appearing when 'Hide in Navigation' is set.
    String listType = _properties.get("listFrom", (String) null);
    boolean allowHidden = List.SOURCE_STATIC.equals(listType);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, allowHidden));
    request.setAttribute("list", list);

    String strItemLimit = _properties.get(List.LIMIT_PROPERTY_NAME, (String) null);
   	String strPageItems = _properties.get(List.PAGE_MAX_PROPERTY_NAME, (String) null);

       // no limit set, but pagination enabled, set limit to infinite
   	if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
   		list.setLimit(Integer.MAX_VALUE);
   	}

    //
    // Determine whether to replace the ROOT path inside the query
    //

    String componentRoot = _component.getProperties().get("componentRoot", (String) null);
    if (!componentRoot.endsWith("/")) {
        componentRoot += "/";
    }

    String queryScript = componentRoot + "query.jsp";
    //String topLayoutScript = componentRoot + "layoutTop.jsp";
    String searchIn = _properties.get("searchIn", (String) null);


%>

<%-- will include the query script if it exists --%>
<c:choose>
    <c:when test="<%= searchIn != null && !searchIn.startsWith("/") %>">
        <p class="cq-info">Make sure you setup your root path properly</p>
    </c:when>
    <c:otherwise>
        <cq:include script="<%= queryScript %>" />
    </c:otherwise>
</c:choose>
