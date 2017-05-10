<%@ page import="com.google.common.base.Throwables" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_TOOLTIP_COORDS_FORMAT = "px";
    final String DEFAULT_ARIA_ROLE = "article";

    Object[][] componentFields = {
        {"cssClass", ""},
        {"title", ""},
        {"description", ""},
        {"positionX", "auto"},
        {"positionY", "auto"},
        {"positionFormatX", DEFAULT_TOOLTIP_COORDS_FORMAT},
        {"positionFormatY", DEFAULT_TOOLTIP_COORDS_FORMAT},
        {"ariaRole", DEFAULT_ARIA_ROLE},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<span role="${componentProperties.ariaRole}" class="tooltip ${componentProperties.cssClass}"
      style="top: ${componentProperties.positionY}${componentProperties.positionFormatY};
              left: ${componentProperties.positionX}${componentProperties.positionFormatY};"
      data-title="${componentProperties.title}" data-content="${componentProperties.description}"></span>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
