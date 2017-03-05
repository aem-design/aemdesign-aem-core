<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%

    // init
    Map<String, Object> info = new HashMap<String, Object>();

    info.put("componentName", _component.getProperties().get(JcrConstants.JCR_TITLE,""));
    info.put("cssClass", _currentStyle.get("cssClass", ""));
    info.put("locked", _properties.get("locked", true));


    // layout value
    String layoutValue = _properties.get("layout", (String) null);
    if ( StringUtils.isBlank(layoutValue) || layoutValue.equals("none")) {
        layoutValue = "mrec";
    }
    info.put("layout", layoutValue);

    if (layoutValue=="custom") {
        info.put("width", _properties.get("width", ""));
        info.put("height", _properties.get("height", ""));

        StringBuilder strB = new StringBuilder();
        strB.append("style=\"");

        if (StringUtils.isNotEmpty(info.get("width").toString())) {
            strB.append("width:"+info.get("width").toString()+"px;");
        }
        if (StringUtils.isNotEmpty(info.get("height").toString())) {
            strB.append("height:"+info.get("height").toString()+"px;");
        }

        strB.append("\"");

        info.put("customSizeCSS", strB.toString());
    }

    String instanceName = _component.getCellName();
    if (_currentNode !=null ) {
        instanceName = _currentNode.getName();
    }
    info.put("instanceName", instanceName);

    info.put("title", _properties.get("title", ""));

    info.put("editMode", CURRENT_WCMMODE == WCMMode.EDIT);

    info.put("target", _currentNode.getPath());

    info.put("ddClassName", DropTarget.CSS_CLASS_PREFIX + "paragraph");

    getLogger().warn("locked par: {}",info.get("target").toString());
%>
<c:set var="info" value="<%= info %>" />
<div id="${info.instanceName}" class="${info.cssClass}${info.layout}"${not empty info.customSizeCSS ? ' ' + info.customSizeCSS : ''}>
<c:choose>
    <c:when test="${not info.locked}">
        <cq:include path="par" resourceType="foundation/components/parsys"/>
    </c:when>
    <c:otherwise>
        <c:if test="${info.editMode}">
            <%
            String defDecor =_componentContext.getDefaultDecorationTagName();

            try {

                disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                %><cq:include path="par" resourceType="foundation/components/parsys"/><%
            }
            catch (Exception ex) {
                %><p class="cq-error">Missing content.</p><%
            }
            finally {

                enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

            }
            %>
        </c:if>
    </c:otherwise>
</c:choose>
</div>
<c:if test="${info.editMode}">
    <p class="cq-info"><small>${info.componentName} - Layout: ${info.layout}; Locked: ${info.locked};</small></p>
</c:if>
