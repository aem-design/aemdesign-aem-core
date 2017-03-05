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

    String img =  this.getPageImgReferencePath(thisPage);
    img = getThumbnail(img, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver);

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

    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    ResourceResolver adminResourceResolver = null;

    try {

        adminResourceResolver = this.openAdminResourceResolver(_sling);

        TagManager adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

        String category = this.getTags(adminTagManager,  componentProperties.get("cq:tags", new String[]{}));
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
            eventRefLink =  mappedUrl(_pageManager.getPage(eventRefLink).getPath());
        }
        componentProperties.put("eventRefLink", eventRefLink);
    }

    componentProperties.put("img",mappedUrl(img));
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText","eventdetail") + componentProperties.get("title"));
    componentProperties.put("pastEvent", _i18n.get("pastEventText","eventdetail"));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<div class="col-1 date">
    <time datetime="<fmt:formatDate value="${componentProperties.eventStartDate.time}" type="both" dateStyle="full" timeStyle="full" />">${componentProperties.eventDisplayDateFormatted}</time>
</div>

<div class="header">
    <span class="label ${componentProperties.menuColor}">${componentProperties.category}</span>
    <h3>
        <a href="<c:out value="${componentProperties.url}"/>" title="<c:out value="${componentProperties.imgAlt}" />">
            ${componentProperties.titleFormatted}
        </a>
    </h3>
</div>