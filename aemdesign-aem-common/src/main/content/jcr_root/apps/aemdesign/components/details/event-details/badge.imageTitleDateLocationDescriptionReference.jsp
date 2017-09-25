<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.text.SimpleDateFormat" %>

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
            eventRefLink =  mappedUrl(_resourceResolver, _pageManager.getPage(eventRefLink).getPath()).concat(DEFAULT_EXTENTION);
        }
        componentProperties.put("eventRefLink", eventRefLink);
    }

    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","eventdetail") + componentProperties.get("title"));
    componentProperties.put("pastEvent", _i18n.get("pastEventText","eventdetail"));
    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, "220"));

    componentProperties.put(DEFAULT_SECONDARY_IMAGE_NODE_NAME, getPageContentImagePath(thisPage, "article/par/event-details/secondaryImage"));
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<div class="module twin">

    <div class="thumbnail">
        <a href="<c:out value="${componentProperties.url}"/>" title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>

        <c:if test="${not empty componentProperties.imgUrl}">
            <img alt="${componentProperties.imgAlt}"  width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"
                 src="<c:out value="${componentProperties.imgUrl}" />"
                 <c:if test="${not empty componentProperties.secondaryImage}">class="rollover" data-img-rollover="${componentProperties.secondaryImage}"</c:if>
            />
        </c:if>
        <c:if test="${not empty componentProperties.category}">
            <span class="label">
                <strong class="${componentProperties.menuColor}">${componentProperties.category}</strong>
            </span>
        </c:if>
    </div>

    <div class="body">
        <h4>
            <a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}" />">${componentProperties.titleFormatted}</a>
        </h4>

        <p>${componentProperties.subTitleFormatted}</p>

        <p>${componentProperties.description}</p>

        <c:if test="${not empty componentProperties.eventRefLink}">
            <p>
                <c:choose>
                    <c:when test="${not empty componentProperties.eventRefLink}">
                        <a title="${componentProperties.eventRefLabel}" href="${componentProperties.eventRefLink}">
                            <span style="font-size: 90%; background-color: #000000; color: white; padding: 3px; display: block; width: 170px; margin-top: 7px; text-align: center; font-family: 'ramblabold';">
                                    ${componentProperties.eventRefLabel}
                            </span>
                        </a>
                    </c:when>
                    <c:otherwise>
                            <span style="font-size: 90%; background-color: #000000; color: white; padding: 3px; display: block; width: 170px; margin-top: 7px; text-align: center; font-family: 'ramblabold';">
                                    ${componentProperties.eventRefLabel}
                            </span>
                    </c:otherwise>
                </c:choose>
            </p>
        </c:if>

        <c:if test="${componentProperties.isPastEventDate}">
            <p>
                <span class="past">${componentProperties.pastEvent}</span>
            </p>
        </c:if>

    </div>
</div>

