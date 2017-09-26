<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.day.cq.commons.DownloadResource" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%


    //init
    Page thisPage = (Page) request.getAttribute(FIELD_BADGE_PAGE);

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/page-details";

    //Url
    Object[][] componentFields = {
            {"title", thisPage.getTitle()}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String pageTitle = getPageNavTitle(thisPage);

    //componentProperties.put("title", pageTitle);

    String img =  this.getPageImgReferencePath(thisPage);
    img = getThumbnail(img, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver);

    String url = getPageUrl(thisPage);
    componentProperties.put("img", img);

    componentProperties.put("url", url);
    componentProperties.put("title", thisPage.getTitle());
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","pagedetail",pageTitle) );

    String altImg = getPageContentImagePath(thisPage, "article/par/page-details/secondaryImage");
    componentProperties.put(DEFAULT_SECONDARY_IMAGE_NODE_NAME, getThumbnail(altImg, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver));


%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

<a class="external_link" href="${componentProperties.url}" title="${componentProperties.imgAlt}" target="_blank">
    <img src="${componentProperties.img}" alt="${componentProperties.imgAlt}"
         <c:if test="${not empty componentProperties.secondaryImage}">class="rollover" data-img-rollover="${componentProperties.secondaryImage}"</c:if>	>
</a>
