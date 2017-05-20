<%@ page import="com.day.cq.wcm.api.Page"%>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/page-details";

    //Url
    Object[][] componentFields = {
            {"title", thisPage.getTitle()},
            {"componentId", ""},
            {"componentTheme", new String[]{}},
            {"componentModifiers", new String[]{}},
            {"componentModule", new String[]{}},
            {"componentChevron", new String[]{}},
            {"componentIcon", new String[]{}},
            {"positionX", ""},
            {"positionY", ""}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String url = getPageUrl(thisPage);
    componentProperties.put("url", url);

    //TODO:Implement Stacked Icons https://fortawesome.github.io/Font-Awesome/examples/
%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:if test="${fn:length(componentProperties.componentAttributes) > 0}">
    <c:choose>
        <c:when test="${not empty componentProperties.url}">
            <a class="external_link" href="${componentProperties.url}" title="${componentProperties.title}" target="_blank">
                <i ${componentProperties.componentAttributes}  title="${componentProperties.title}"></i>
            </a>
        </c:when>
        <c:otherwise>
            <i ${componentProperties.componentAttributes}  title="${componentProperties.title}"></i>
        </c:otherwise>
    </c:choose>
</c:if>