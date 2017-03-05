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
            {"eventRefLabel2", StringUtils.EMPTY},
            {"eventRefLink2", StringUtils.EMPTY},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", StringUtils.EMPTY},
            {"menuColor", StringUtils.EMPTY},
            {"titleFormat",""},
            {"subTitleFormat",""},
            {"eventDisplayDateFormat",""}

    };

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    ResourceResolver adminResourceResolver = null;

    try {

        adminResourceResolver = this.openAdminResourceResolver(_sling);

        TagManager adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

        String category = this.getTags(adminTagManager,  componentProperties.get("cq:tags", new String[]{}), _currentPage.getLanguage(true));

        componentProperties.put("category", category);

    } catch (Exception ex) {
        out.write( Throwables.getStackTraceAsString(ex) );
    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }

    componentProperties.put("url",mappedUrl(url));

    if (StringUtils.isNotEmpty(componentProperties.get("eventRefLink", String.class))){
        String eventRefLink = componentProperties.get("eventRefLink", String.class);
        if (_pageManager.getPage(eventRefLink) != null){
            eventRefLink =  mappedUrl(_pageManager.getPage(eventRefLink).getPath()) + DEFAULT_PAGE_EXTENTION;
        }
        componentProperties.put("eventRefLink", eventRefLink);
    }

    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","eventdetail") + componentProperties.get("title"));
    componentProperties.put("pastEvent", _i18n.get("pastEventText","eventdetail"));
    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, "220"));

    componentProperties.put("secondaryImage", getSecondaryImageReferencePath(thisPage, "article/par/event-details/secondaryImage"));
%>
<c:set var="componentProperties" value="<%= componentProperties %>" />
<div class="imgLocRef2_module">
    <div class="imgLocRef2_wrapper">
        <div style="position: relative;">
            <a title="${componentProperties.titleFormatted}" href="${componentProperties.url}"><img id="img1" alt="${componentProperties.imgAlt}" src="${componentProperties.imgUrl}" width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"></a>
       </div>
        <div class="imgLocRef2_body">
            <h4><a href="${componentProperties.url}">${componentProperties.titleFormatted}</a></h4>
            <p>${componentProperties.subTitleFormatted}</p>
            <p>${componentProperties.description}</p>
            <a href="${componentProperties.eventRefLink}">
                <span>${componentProperties.eventRefLabel}</span>
            </a>
            <span style="background-color: #e2e2e2; color: white; padding: 3px; display: inline-block; width: 70px; margin-top: 7px; text-align: center;">&nbsp;</span>
            <a href="${componentProperties.eventRefLink2}" target="_blank">
                <span>${componentProperties.eventRefLabel2}</span>
            </a>
        </div>
    </div>
</div>
