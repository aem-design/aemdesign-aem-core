<%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image" %><%
%>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%
    Image image = new Image(_resource, "image");

    //drop target css class = dd prefix + name of the drop target in the edit config
    image.addCssClass(DropTarget.CSS_CLASS_PREFIX + "image");
    image.loadStyleData(_currentStyle);
    image.setSelector(".img"); // use image script
    image.setDoctype(Doctype.fromRequest(request));
    // add design information if not default (i.e. for reference paras)
    if (!_currentDesign.equals(_resourceDesign)) {
        image.setSuffix(_currentDesign.getId());
    }
    String divId = "cq-image-jsp-" + _resource.getPath();
    String compCSSClass = properties.get("cssClass", StringUtils.EMPTY);

    String caption = _properties.get("caption", (String) null);
    String imageLocation = _properties.get("image-position", "image-left");
    boolean disableWrapping = _properties.get("wrap-disable", false);
%>
<c:choose>
    <c:when test="<%= imageLocation.equals("image-in-line")%>">
        <%@ include file="textimage.inline.jsp" %>
    </c:when>
    <c:when test="<%= disableWrapping %>">
        <%@ include file="textimage.nowrap.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="textimage.wrap.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>