<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%
    // init
    Map<String, Object> info = new HashMap<String, Object>();

    info.put("componentName", _component.getProperties().get(JcrConstants.JCR_TITLE,""));

    String[] includePaths = _properties.get(SITE_INCLUDE_PATHS, new String[0]);
    boolean isIncludePathsEmpty = includePaths.length == 0;
    info.put("includePaths", StringUtils.join(includePaths,","));

    Boolean showContentPreview = Boolean.parseBoolean(_properties.get("showContentPreview", "false"));
    Boolean showContent = Boolean.parseBoolean(_properties.get("showContent", "false"));
    info.put("showContentPreview", showContentPreview);
    info.put("showContent", showContent);
    info.put("showContentSet", showContent);

    String includeContents = "";

    if(!isIncludePathsEmpty) {
        //Resource contentResource = _resourceResolver.getResource(_resourceResolver,includePaths,null);
        includeContents = getResourceContent(_resourceResolver,includePaths,"");
        info.put("hasContent", StringUtils.isNotEmpty(includeContents));
    }

    //only allow hiding when in edit mode
    if (CURRENT_WCMMODE == WCMMode.EDIT) {
        info.put("showContent", showContentPreview);
    }

%>

<c:set var="info" value="<%= info %>" />
<c:if test="${not empty info.hasContent}">
    <c:if test="${info.showContent}"><%=includeContents%></c:if>
</c:if>
