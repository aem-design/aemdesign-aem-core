<%@ page import="com.google.common.base.Throwables" %>
<%@page session="false"%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_DELIMITER = "";
    final String DEFAULT_TRAIL = "";
    final String DEFAULT_ARIA_ROLE = "navigation";
    final String DEFAULT_ARIA_LABEL = "breadcrumb";
    final boolean DEFAULT_SHOW_HIDDEN = false;
    final boolean DEFAULT_HIDE_CURRENT = false;
    final int DEFAULT_LEVEL_START = 1;
    final int DEFAULT_LEVEL_END = 1;

    Object[][] componentFields = {
            {"delimiter", DEFAULT_DELIMITER},
            {"trail", DEFAULT_TRAIL},
            {"startLevel", ""},
            {"endLevel", ""},
            {"showHidden", DEFAULT_SHOW_HIDDEN},
            {"hideCurrent", DEFAULT_HIDE_CURRENT},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE},
            {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
            {FIELD_VARIANT, DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

//    componentProperties.put("componentPath",resource.getPath());

    List<Map> values = new ArrayList<Map>();

    int startLevel = tryParseInt(componentProperties.get("startLevel",""), DEFAULT_LEVEL_START);
    int endLevel = tryParseInt(componentProperties.get("endLevel",""), DEFAULT_LEVEL_END);
    int currentLevel = _currentPage.getDepth();

    if (isBlank(componentProperties.get("endLevel",""))) {
        endLevel = currentLevel;
    }

    boolean showHidden = componentProperties.get("showHidden", DEFAULT_SHOW_HIDDEN);
    boolean hideCurrent = componentProperties.get("hideCurrent", DEFAULT_SHOW_HIDDEN);

//    out.write("<!--");
//    out.write("s:"+startLevel+";");
//    out.write("e:"+endLevel+";");
//    out.write("c:"+currentLevel+";");
//    out.write("hc:"+hideCurrent+";");
//    out.write("sh:"+showHidden+";");
    for (int i = startLevel; i <= endLevel; i++) {
        Page pagetrail = _currentPage.getAbsoluteParent(i);
        if (pagetrail == null) {
//            out.write("x:"+i+";");
            continue;
        }
        if (hideCurrent) {
            if (i==currentLevel-1) {
//                out.write("xc:"+i+";");
                continue;
            }
        }

        if (pagetrail != null && (!pagetrail.isHideInNav() || showHidden)) {

            HashMap<String, String> pagetrailvalues = new HashMap<String, String>();

            pagetrailvalues.put("path",pagetrail.getPath());
            pagetrailvalues.put("url",pagetrail.getPath().concat(DEFAULT_EXTENTION));
            pagetrailvalues.put("name",pagetrail.getName());
            pagetrailvalues.put("title",getPageTitle(pagetrail));

            values.add(pagetrailvalues);
        } else {
//            out.write("xh:"+i+";");
        }
    }
//    out.write("-->");

    componentProperties.put("values", values);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@ include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
