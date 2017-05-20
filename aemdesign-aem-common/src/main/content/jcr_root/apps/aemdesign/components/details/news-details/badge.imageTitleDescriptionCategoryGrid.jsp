<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.day.cq.wcm.api.Page" %>
<%@page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%

    final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/admin/defaults/environment.gif";

    String componentPath = "./" + PATH_DEFAULT_CONTENT + "/news-details";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", getPageTitle(thisPage)},
            {"description", thisPage.getProperties().get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {TagConstants.PN_TAGS, new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", ""},
            {"promoSize", ""},
            {"menuColor", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    componentProperties.put("url", url);
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText", "pagedetail") + componentProperties.get("title", String.class));

    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, "220"));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

    <section class="col-3 left">
        <div class="module twin">
            <div class="thumbnail  ${componentProperties.emptyImagePlaceHolder}">
                <a href="${componentProperties.url}"
                   title="${componentProperties.imgAlt}">${componentProperties.title}</a>
                <c:if test="${not empty componentProperties.imgUrl}">
                    <img alt="${componentProperties.imgAlt}" width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"
                         src="${componentProperties.imgUrl}"/>
                </c:if>
                <c:if test="${not empty componentProperties.category}">
                    <div class="label">
                        <strong
                                <c:if test="${not empty componentProperties.menuColor}">class="${componentProperties.menuColor}"</c:if>>${componentProperties.category}</strong>
                    </div>
                </c:if>
            </div>
            <div class="body">
                <h3>
                    <a href="${componentProperties.url}"
                       title="${componentProperties.imgAlt}">${componentProperties.title}</a>
                </h3>

                <p>${componentProperties.description}</p>
            </div>
        </div>
    </section>

