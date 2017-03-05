<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    final String DEFAULT_PROVIDER = "Youtube";
    final String DEFAULT_VARIANT = "default";

    Object[][] componentFields = {
            {"provider", DEFAULT_PROVIDER, "provider"},
            {"component", "onlinemedia", "modules"},
            {"width", 0},
            {"height", 0},
            {"mediaId", ""},
            {"variant", DEFAULT_VARIANT}
    };


    ComponentProperties componentProperties = getComponentStyleProperties(pageContext);
    //out.println("dataAttributes before 1:" + componentProperties.get("componentAttributes", String.class));
    componentProperties.putAll(getComponentProperties(pageContext, componentFields));
    //out.println("dataAttributes before :" + componentProperties.get("componentAttributes", String.class));
    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties, _component, _sling));
    //out.println("dataAttributes after :" + componentProperties.get("dataAttributes", String.class));

//    String inlineStyle = "style=\"width:100%, height:100%\"";
    String inlineStyle = "";

    if (componentProperties.get("width", Integer.class) > 0 && componentProperties.get("height", Integer.class) > 0){
        inlineStyle = " style=\"width: "+componentProperties.get("width", Integer.class)+"px; height: "+componentProperties.get("height", Integer.class)+"px;\"";
    }
    componentProperties.put("inlineStyle", inlineStyle);

    String provider = componentProperties.get("provider").toString().toLowerCase();
    componentProperties.put("provider", provider);

    String src = (provider.equals("vimeo") ? "//player.vimeo.com/video/" : "//www.youtube.com/watch?v=") + componentProperties.get("mediaId");
    componentProperties.put("src", src);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.provider eq 'issuu'}">
        <%@ include file="style.issuudigitalpublishing.jsp"  %>
    </c:when>
    <c:otherwise>
        <%@ include file="style.default.jsp"  %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>