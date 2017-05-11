<%@ page import="com.google.common.base.Throwables" %>
<%@page session="false"%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_STYLE = "default";
    final String DEFAULT_DELIMITER = "";
    final String DEFAULT_EXTENTION = ".html";
    final String DEFAULT_TRAIL = "";
    final String DEFAULT_ARIA_ROLE = "navigation";
    final String DEFAULT_ARIA_LABEL = "breadcrumb";
    final boolean DEFAULT_SHOW_HIDDEN = false;
    final boolean DEFAULT_HIDE_CURRENT = false;
    final long DEFAULT_LEVEL_START = 2L;
    final long DEFAULT_LEVEL_END = 1L;

    Object[][] componentFields = {
            {"displayStyle", DEFAULT_STYLE},
            {"delimiter", DEFAULT_DELIMITER},
            {"trail", DEFAULT_TRAIL},
            {"startLevel", DEFAULT_LEVEL_START},
            {"endLevel", DEFAULT_LEVEL_END},
            {"showHidden", DEFAULT_SHOW_HIDDEN},
            {"hideCurrent", DEFAULT_HIDE_CURRENT},
            {"ariaRole",DEFAULT_ARIA_ROLE},
            {"ariaLabel",DEFAULT_ARIA_LABEL},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    componentProperties.put("componentPath",resource.getPath());

    List<Map> values = new ArrayList<Map>();

    long startLevel = Long.getLong(componentProperties.get("startLevel").toString(), DEFAULT_LEVEL_START);
    long endLevel = Long.getLong(componentProperties.get("endLevel").toString(), DEFAULT_LEVEL_END);
    int currentLevel = currentPage.getDepth();
    boolean showHidden = componentProperties.get("showHidden", DEFAULT_SHOW_HIDDEN);
    boolean hideCurrent = componentProperties.get("hideCurrent", DEFAULT_SHOW_HIDDEN);

    while (startLevel < currentLevel - endLevel) {
        Page pagetrail = currentPage.getAbsoluteParent((int) startLevel);
        if (pagetrail == null) {
            break;
        }

        if (pagetrail != null && (!pagetrail.isHideInNav() || showHidden)) {

            HashMap<String, String> pagetrailvalues = new HashMap<String, String>();

            pagetrailvalues.put("path",pagetrail.getPath());
            pagetrailvalues.put("url",pagetrail.getPath()+DEFAULT_EXTENTION);
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
