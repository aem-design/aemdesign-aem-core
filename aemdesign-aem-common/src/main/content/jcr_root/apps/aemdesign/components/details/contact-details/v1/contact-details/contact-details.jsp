<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.jsoup.nodes.Document" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="./common.jsp" %>
<%

    final Boolean DEFAULT_HIDE_DESCRIPTION = false;
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final String DEFAULT_I18N_CATEGORY = "contact-detail";
    final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, _currentPage.getName())},
            {"honorificPrefix", ""}, //tag path
            {"givenName",""},
            {"familyName",""},
            {"titleFormat", ""}, //tag path, will be resolved to value in processComponentFields
            {"jobTitle",""},
            {"employee",""},
            {"email",""},
            {"descriptionFormat", ""}, //tag path, will be resolved to value in processComponentFields
            {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
            {TagConstants.PN_TAGS, new String[]{},"data-tags", Tag.class.getCanonicalName()},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {FIELD_PAGE_URL, getPageUrl(_currentPage)},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(_currentPage)},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"variantHiddenLabel", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
            {DETAILS_LINK_TEXT, getPageNavTitle(_currentPage)},
            {DETAILS_LINK_TITLE, getPageTitle(_currentPage)},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

    componentProperties.put("category",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

    //grab value for prefix
    componentProperties.put("honorificPrefix",
            getTagValueAsAdmin(componentProperties.get("honorificPrefix", ""),_sling)
    );

    //read the image node
    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getPageImgReferencePath(_currentPage),
            FIELD_PAGE_IMAGE));

    //read the secondary image node
    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
            FIELD_PAGE_SECONDARY_IMAGE));

    //read the background image node
    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
            FIELD_PAGE_BACKGROUND_IMAGE));

    //read the thumbnail image node
    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_THUMBNAIL_IMAGE_NODE_NAME),
            FIELD_PAGE_THUMBNAIL_IMAGE));

    componentProperties.put(FIELD_REDIRECT_TARGET,_pageProperties.get(FIELD_REDIRECT_TARGET,""));

    //set thumbnail path for image node
    componentProperties.put(FIELD_PAGE_IMAGE_THUMBNAIL,
            getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    _resourceResolver
            )
    );

    //set thumbnail path for secondary image node
    componentProperties.put(FIELD_PAGE_SECONDARY_IMAGE_THUMBNAIL,
            getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_SECONDARY_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    _resourceResolver
            )
    );

    //set thumbnail path for thumbnail image node
    componentProperties.put(FIELD_PAGE_THUMBNAIL_IMAGE_THUMBNAIL,
            getBestFitRendition(
                    componentProperties.get(FIELD_PAGE_THUMBNAIL_IMAGE, ""),
                    componentProperties.get(DETAILS_THUMBNAIL_WIDTH, DEFAULT_THUMB_WIDTH_SM),
                    _resourceResolver
            )
    );

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling), false);

    //set something if title formatted is empty
    if (isEmpty(componentProperties.get(FIELD_FORMATTED_TITLE,""))) {
        componentProperties.put(FIELD_FORMATTED_TITLE,componentProperties.get("title",""));
        componentProperties.put(FIELD_FORMATTED_TITLE_TEXT,componentProperties.get("title",""));
    }

    componentProperties.putAll(processBadgeRequestConfig(componentProperties,_resourceResolver, request), true);

    componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(pageContext));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<%@ include file="/apps/aemdesign/global/component-background.jsp" %>

<%@ include file="badgeconfig.jsp" %>

<c:choose>

    <%-- NORMAL BADGES --%>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleCategoryDescriptionAction'}">
        <%@ include file="badge.cardImageTitleCategoryDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleCategoryDescription'}">
        <%@ include file="badge.cardActionImageTitleCategoryDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIcon'}">
        <%@ include file="badge.cardIcon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleCategoryDescriptionAction'}">
        <%@ include file="badge.cardIconTitleCategoryDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleCategoryDescription'}">
        <%@ include file="badge.cardActionIconTitleCategoryDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardHorizontalIconTitleCategoryDescriptionAction'}">
        <%@ include file="badge.cardHorizontalIconTitleCategoryDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.icon'}">
        <%@ include file="badge.icon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.image'}">
        <%@ include file="badge.image.jsp" %>
    </c:when>


    <%-- DEFAULT BADGES --%>

    <c:when test="${COMPONENT_BADGE eq 'badge.metadata'}">
        <%@ include file="badge.metadata.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge' or COMPONENT_BADGE eq 'badge.default'}">
        <%@ include file="badge.default.jsp" %>
    </c:when>


    <%-- VARIANTS --%>

    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT_HIDDEN}">
        <%@include file="variant.hidden.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
