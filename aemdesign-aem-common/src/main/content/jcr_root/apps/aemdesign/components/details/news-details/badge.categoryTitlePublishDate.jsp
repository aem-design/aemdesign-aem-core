<%@ page import="com.day.cq.tagging.TagConstants"%>
<%@ page import="com.google.common.base.Throwables" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    // init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/news-details";

    String img = this.getPageImgReferencePath(thisPage);
    img = getThumbnail(img, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver);

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            //Page Title can be override from component
            {"title", getPageTitle(thisPage)},
            {"description", thisPage.getProperties().get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"publishDate", thisPage.getProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {TagConstants.PN_TAGS, new String[]{}}

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, thisPage, componentPath, componentFields);

    String[] tags = getMultiplePropertyString(getComponentNode(thisPage,componentPath),TagConstants.PN_TAGS);

    componentProperties.put("tags",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    componentProperties.put("url", mappedUrl(_resourceResolver, url));
    componentProperties.put("img", img);
    componentProperties.put("imgAlt", (_i18n.get("readMoreAboutText","newsdetail") + componentProperties.get("title", String.class)));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<section class="col-3 left">
    <div class="minor module news twin">
        <div class="body">
            <h3><a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>">${componentProperties.tags[0].localizedTitles[LOCALE]}</a></h3>
            <p>
                <b><a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>">${componentProperties.title}</a></b>
            </p>

            <c:if test="${not empty componentProperties.publishDate}">
                <span>
                    <time datetime="<fmt:formatDate value="${componentProperties.publishDate.time}"/>">
                        <fmt:formatDate value="${componentProperties.publishDate.time}" pattern="dd.MM.yyyy" />
                    </time>
                </span>
            </c:if>
        </div>
    </div>
</section>