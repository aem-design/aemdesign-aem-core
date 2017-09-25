<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="com.day.cq.wcm.api.Page" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ include file="./common.jsp" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    final String DEFAULT_MEDIA_COUNT_TEMPLATE = "/apps/aemdesign/templates/content/page/mediacount";
    final String DEFAULT_IMAGE_PATH = "/content/dam/aemdesign/admin/defaults/environment.gif";

    String componentPath = "./" + PATH_DEFAULT_CONTENT + "/page-details";

    //init
    Page thisPage = (Page) request.getAttribute("badgePage");

    boolean mediaCountDisplay = false;
    String pageTemplate = (String)thisPage.getProperties().get("cq:template");

    if(DEFAULT_MEDIA_COUNT_TEMPLATE.equals(pageTemplate)){
        mediaCountDisplay = true;
    }

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            {"title", getPageTitle(thisPage)},
            {"description", thisPage.getDescription()},
            {"cq:tags", new String[]{}},
            //subCategory is resolved for Event Detail's badge
            {"subCategory", ""},
            {DETAILS_FIELD_CARDSIZE, ""},
            {"menuColor", ""},
            {"titleFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    String pageTitle = getPageTitle(thisPage);

   // componentProperties.put("title", pageTitle);
    componentProperties.put("url", url);
    componentProperties.put("imgAlt", _i18n.get("readMoreAboutText", "pagedetail") + pageTitle);
    Node gallery = null;

    String galleryBadgeBase = getPageBadgeBase(thisPage, "mediagallery");
    if (galleryBadgeBase == null || mediaCountDisplay == false) {
        componentProperties.put("hasGallery", false);
    } else if(mediaCountDisplay == true) {
        componentProperties.put("hasGallery", true);
        gallery = getComponentNode(thisPage, "article/par/mediagallery");
        request.setAttribute("gallery", gallery);
        request.setAttribute("menuColor", componentProperties.get("menuColor"));
    }

    String script = galleryBadgeBase + String.format("badge.%s.jsp", "summary");

    String width = "220";

    if(componentProperties.get(DETAILS_FIELD_CARDSIZE,"").endsWith(MEDIUM_THUMBNAIL_SIZE)){
        componentProperties.put("isMediumThumbnail",true);
        width = "320";
    }else if(componentProperties.get(DETAILS_FIELD_CARDSIZE,"").endsWith(LARGE_THUMBNAIL_SIZE)){
        componentProperties.put("isLargeThumbnail",true);
        width = "460";
    }else{
        componentProperties.put("isSmallThumbnail",true);
    }
    componentProperties.putAll(this.getPageNamedImage(_sling, thisPage, width));

    componentProperties.put(DEFAULT_SECONDARY_IMAGE_NODE_NAME, getPageContentImagePath(thisPage, "article/par/page-details/secondaryImage"));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));
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
                       title="${componentProperties.imgAlt}">${componentProperties.titleFormatted}</a>

                    <c:if test="${not empty componentProperties.imgUrl}">
                        <img alt="${componentProperties.imgAlt}"  width="${componentProperties.profileWidth}" height="${componentProperties.profileHeight}"
                             src="${componentProperties.imgUrl}"
                             <c:if test="${not empty componentProperties.secondaryImage}">class="rollover" data-img-rollover="${componentProperties.secondaryImage}"</c:if>
                        />
                    </c:if>

                        <div class="label">
                            <c:if test="${not empty componentProperties.category}">
                                <strong
                                    <c:if test="${not empty componentProperties.menuColor}">class="${componentProperties.menuColor}"</c:if>>${componentProperties.category}</strong>
                            </c:if>
                            <c:if test="${componentProperties.hasGallery}">
                                 <%
                                        String defDecor = _componentContext.getDefaultDecorationTagName();

                                        try {

                                            disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                                    %><cq:include script="<%= script %>"/><%
                                } catch (Exception ex) {
                                    if (!"JspException".equals(ex.getClass().getSimpleName())) {
                                        throw ex;
                                    }
                                %>
                                    <p class="cq-error"><%=getError(ERROR_NOTFOUND_BADGE, ERROR_CATEGORY_GENERAL, _i18n, "summary", galleryBadgeBase, script)%>
                                    </p><%
                                    } finally {

                                        enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);
                                    }

                                %>

                            </c:if>
                        </div>

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


