<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="./common.jsp" %>
<%

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"eventStartDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventEndDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventLoc", StringUtils.EMPTY},
            {"eventRefLabel", StringUtils.EMPTY},
            {"eventRefLink", StringUtils.EMPTY},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", StringUtils.EMPTY},
            {"menuColor", StringUtils.EMPTY},
            {"titleFormat",""},
            {"cardSize", ""},
            {"subTitleFormat",""},
            {"eventDisplayDateFormat",""}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    componentProperties.put("url",mappedUrl(_resourceResolver, url));

    if (StringUtils.isNotEmpty(componentProperties.get("eventRefLink", String.class))){
        String eventRefLink = componentProperties.get("eventRefLink", String.class);
        if (_pageManager.getPage(eventRefLink) != null){
            eventRefLink =  mappedUrl(_resourceResolver, _pageManager.getPage(eventRefLink).getPath());
        }
        componentProperties.put("eventRefLink", eventRefLink);
    }

    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","eventdetail") + componentProperties.get("title"));
    componentProperties.put("pastEvent", _i18n.get("pastEventText","eventdetail"));

    String width = "220";

    if(componentProperties.get("cardSize","").endsWith(MEDIUM_THUMBNAIL_SIZE)){
        componentProperties.put("isMediumThumbnail",true);
        width = "320";
    }else if(componentProperties.get("cardSize","").endsWith(LARGE_THUMBNAIL_SIZE)){
        componentProperties.put("isLargeThumbnail",true);
        width = "460";
    }else{
        componentProperties.put("isSmallThumbnail",true);
    }

    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, width));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    componentProperties.put(DEFAULT_SECONDARY_IMAGE_NODE_NAME, getPageContentImagePath(thisPage, "article/par/event-details/secondaryImage"));



%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

            <c:if test="${componentProperties.isLargeThumbnail}">
                <section class="col-6 left">
                <div class="major module">
            </c:if>
            <c:if test="${componentProperties.isMediumThumbnail}">
                <section class="col-4 left">
                <div class="major module">
            </c:if>
            <c:if test="${componentProperties.isSmallThumbnail}">
                <section class="col-3 left">
                <div class="module">
            </c:if>
                <div class="thumbnail ${componentProperties.emptyImagePlaceHolder}">
                    <a href="${componentProperties.url}"
                       title="${componentProperties.imgAlt}">${componentProperties.title}</a>

                    <c:if test="${not empty componentProperties.imgUrl}">
                        <img alt="${componentProperties.imgAlt}"  width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"
                             src="${componentProperties.imgUrl}"
                             <c:if test="${not empty componentProperties.secondaryImage}">class="rollover" data-img-rollover="${componentProperties.secondaryImage}"</c:if>

                        />
                    </c:if>
                    <c:if test="${not empty componentProperties.category}">
                        <div class="label">
                            <strong
                                    <c:if test="${not empty componentProperties.menuColor}">class="${componentProperties.menuColor}"</c:if>>${componentProperties.category}</strong>
                        </div>
                    </c:if>
                </div>

                <div class="body">
                    <c:if test="${componentProperties.isLargeThumbnail || componentProperties.isMediumThumbnail}">
                        <h2>
                            <a href="${componentProperties.url}"
                               title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
                        </h2>
                    </c:if>
                    <c:if test="${componentProperties.isSmallThumbnail}">
                        <h3>
                            <a href="${componentProperties.url}"
                               title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>
                        </h3>
                    </c:if>

                    <p>${componentProperties.description}
                    </p>
                </div>
            </div>
        </section>

