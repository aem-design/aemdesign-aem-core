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

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("tags",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    componentProperties.put("url",mappedUrl(_resourceResolver, url));
    componentProperties.put("img",img);
    componentProperties.put("imgAlt", (_i18n.get("readMoreAboutText","newsdetail") + componentProperties.get("title", String.class)));

    String description = (String)componentProperties.get("description");
    description = description.replaceAll("&nbsp;", " ");
    componentProperties.put("description", description);
%>
    <c:set var="componentProperties" value="<%= componentProperties %>"/>

    <div class="col-1 date">
    <c:if test="${not empty componentProperties.publishDate}">
        <time datetime="<fmt:formatDate value="${componentProperties.publishDate.time}"/>">
            <fmt:formatDate value="${componentProperties.publishDate.time}" pattern="dd.MM.yyyy" />
        </time>
    </c:if>
    </div>

    <div class="col-4 header desktop-table-cell-only">
        <c:if test="${fn:length(componentProperties.tags) > 0}">
            <span class="label">${componentProperties.tags[0].localizedTitles[LOCALE]}</span>
        </c:if>
        <h3>
            <a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}"/>">
                    ${componentProperties.title}
            </a>
        </h3>
    </div>

    <div class="body mobile-table-cell-only">
        <c:if test="${fn:length(componentProperties.tags) > 0}">
            <span class="label">${componentProperties.tags[0].localizedTitles[LOCALE]}</span>
        </c:if>
        <p>
            ${componentProperties.description}
        </p>
    </div>

    <div class="col-3 thumbnail">
        <c:if test="${not empty componentProperties.img}">
            <a href="<c:out value="${componentProperties.url}"/>" target="_blank">
                <img src="<c:out value="${componentProperties.img}"/>" alt="<c:out value="${componentProperties.imgAlt}"/>"/>
            </a>
        </c:if>
    </div>