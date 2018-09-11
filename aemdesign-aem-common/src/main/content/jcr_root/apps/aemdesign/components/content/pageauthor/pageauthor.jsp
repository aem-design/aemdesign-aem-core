<%@ page import="com.adobe.granite.security.user.*" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    com.adobe.granite.security.user.UserPropertiesService _userPropertiesService = _sling.getService(com.adobe.granite.security.user.UserPropertiesService.class);
    UserPropertiesManager _userPropertiesManager = _userPropertiesService.createUserPropertiesManager(_currentNode.getSession(), _resourceResolver);
    UserManager _userManager = _resourceResolver.adaptTo(org.apache.jackrabbit.api.security.user.UserManager.class);

    String pageAuthorUser = _currentPage.getLastModifiedBy();
    String pageAuthorFullName = "";
    String pageAuthorEmail = "";

    if (isNotBlank(pageAuthorUser)) {
        pageAuthorFullName = getUserFullName(_userManager, _userPropertiesManager, pageAuthorUser, "");
        pageAuthorEmail = getUserEmail(_userManager, _userPropertiesManager, pageAuthorUser, "");

        if (isNotBlank(pageAuthorEmail)) {
            pageAuthorEmail = MessageFormat.format("mailto:{}",pageAuthorEmail);
        } else {
            pageAuthorEmail = "#";
        }
    } else {
        pageAuthorFullName = _currentNode.getSession().getUserID();
    }

    Object[][] componentFields = {
            {"author", pageAuthorFullName},
            {"authorUrl", pageAuthorEmail},
            {FIELD_VARIANT, DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'simple'}">
        <%@ include file="variant.simple.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
