<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%

    final String DEFAULT_ARIA_ROLE = "button";
    final String DEFAULT_ARIA_LABEL = "Audio Fragment";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
            {"audioPath",""},
            {"ariaRole",DEFAULT_ARIA_ROLE},
            {FIELD_ARIA_LABEL,DEFAULT_ARIA_LABEL},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"playerAddress", PLAYER_ADDRESS, "player-address"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String audioUrl = componentProperties.get("audioPath", "");

    if (StringUtils.isNotEmpty(audioUrl)) {
        if (audioUrl.startsWith("/content")) {
            audioUrl = mappedUrl(_resourceResolver, audioUrl);
        }
    }
    componentProperties.put("audioUrl", audioUrl);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

