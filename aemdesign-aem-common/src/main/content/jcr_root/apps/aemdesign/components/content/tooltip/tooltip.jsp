<%@include file="/apps/aemdesign/global/global.jsp" %>
<%

    String cssClass = _properties.get("cssClass", "");
    String title = _properties.get("title", "");
    String description = _properties.get("description", "");
    String positionX = _properties.get("positionX", "");
    String positionY = _properties.get("positionY", "");



%>
<span class="tooltip-span feature-tooltip left <%=cssClass %>" style="top: <%=positionY %>px; left: <%=positionX %>px;" data-title="<%=title %>" data-content="<%=description %>"></span>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>