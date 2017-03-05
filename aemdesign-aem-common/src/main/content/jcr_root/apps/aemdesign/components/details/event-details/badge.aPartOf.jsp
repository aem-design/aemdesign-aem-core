<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>


<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>

<%
    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/event-details";

    final String I18N_CATEGORY = "eventdetail";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", StringUtils.EMPTY}
    };

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    componentProperties.put("url",mappedUrl(url));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    componentProperties.put("apartof", _i18n.get("apartof", I18N_CATEGORY));
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","eventdetail") + componentProperties.get("title"));

    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, "220"));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div class="compl_links">
    <h5>${componentProperties.apartof}</h5>
    <ul>
        <li>
            <div class="thumbnail">
                <a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
                <c:if test="${not empty componentProperties.imgUrl}">
                    <img alt="${componentProperties.imgAlt}"  width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"
                         src="<c:out value="${componentProperties.imgUrl}" />">
                </c:if>
            </div>
            <div class="body">
                <h4>
                    <a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
                </h4>
                <p>${componentProperties.description}</p>
            </div>
        </li>
    </ul>
</div>