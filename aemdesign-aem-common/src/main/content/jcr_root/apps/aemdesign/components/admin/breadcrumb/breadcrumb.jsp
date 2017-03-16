<%@ page import="com.google.common.base.Throwables" %>
<%@page session="false"%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_STYLE = "default";
    final String DEFAULT_DELIMITER = "";
    final String DEFAULT_TRAIL = "";
    final long DEFAULT_LEVEL_START = 2L;
    final long DEFAULT_LEVEL_END = 1L;

    Object[][] componentFields = {
            {"displayStyle", DEFAULT_STYLE},
            {"delimiter", DEFAULT_DELIMITER},
            {"trail", DEFAULT_TRAIL},
            {"absParent", DEFAULT_LEVEL_START},
            {"relParent", DEFAULT_LEVEL_END},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    componentProperties.put("componentPath",resource.getPath());

    List<Map> values = new ArrayList<Map>();

    long level = Long.getLong(componentProperties.get("absParent").toString(), DEFAULT_LEVEL_START);
    long endLevel = Long.getLong(componentProperties.get("relParent").toString(), DEFAULT_LEVEL_END);
    int currentLevel = currentPage.getDepth();

    while (level < currentLevel - endLevel) {
        Page pagetrail = currentPage.getAbsoluteParent((int) level);
        if (pagetrail == null) {
            break;
        }

        if (pagetrail != null && pagetrail.isHideInNav()) {

            HashMap<String, String> pagetrailvalues = new HashMap<String, String>();

            pagetrailvalues.put("path",pagetrail.getPath());
            pagetrailvalues.put("name",pagetrail.getName());
            pagetrailvalues.put("title",getPageTitle(pagetrail));

            values.add(pagetrailvalues);
        }
    }

    componentProperties.put("values", values);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${componentProperties.displayStyle == 'default'}">
        <%@ include file="style.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="style.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
