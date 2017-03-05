<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="interactiveimagedata.jsp" %>

<%


    // init
    Map<String, Object> info = new HashMap<String, Object>();

    info.put("cssClass", _currentStyle.get("cssClass", ""));
    String instanceName = _currentNode.getName();


    //get the strings and positions, not the best way. Should use a list or something
    Map<String, String> contentTextMap = this.getContentText(_resource);
    info.put("contentMenu",contentTextMap);



    //get the image path
    String bgImageFile = _properties.get("bgimage/fileReference", "");
    if (!isEmpty(bgImageFile)) {
        info.put("inlineStyle","style=\"background-image: url('"+ mappedUrl(bgImageFile)+ "')\"");
    }
    boolean isImageEmpty = isEmpty(bgImageFile);
    info.put("isImageEmpty",isImageEmpty);

%>
<c:set var="info" value="<%= info %>" />
<cq:include path="par" resourceType="foundation/components/parsys"/>



    <div id="${info.instanceName}" class="${info.cssClass} ${info.inlineStyle}">
    <c:forEach items="${info.contentMenu}" var="entry">

        <div style="position:absolute;${entry.value}">${entry.key}</div>

    </c:forEach>

    </div>



<%@include file="/apps/aemdesign/global/component-badge.jsp" %>