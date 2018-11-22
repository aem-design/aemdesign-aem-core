<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.jsoup.nodes.Document" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="./common.jsp" %>
<%
    final String DEFAULT_ARIA_ROLE = "banner";
    final String DEFAULT_TITLE_TAG_TYPE = "h1";
    final String DEFAULT_I18N_CATEGORY = "page-detail";
    final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

    // default values for the component
    final String DEFAULT_TITLE = getPageTitle(_currentPage);
    final String DEFAULT_DESCRIPTION = _currentPage.getDescription();
    final String DEFAULT_SUBTITLE = _currentPage.getProperties().get(FIELD_PAGE_TITLE_SUBTITLE,"");
    final Boolean DEFAULT_HIDE_TITLE = false;
    final Boolean DEFAULT_HIDE_DESCRIPTION = false;
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final Boolean DEFAULT_SHOW_PAGE_DATE = true;
    final Boolean DEFAULT_SHOW_PARSYS = true;


    //not using lamda is available so this is the best that can be done
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"title", DEFAULT_TITLE},
            {"titleFormat",""}, //tag path, will be resolved to value in processComponentFields
            {"description", DEFAULT_DESCRIPTION},
            {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
            {"hideTitle", DEFAULT_HIDE_TITLE},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"showPageDate", DEFAULT_SHOW_PAGE_DATE},
            {"showParsys", DEFAULT_SHOW_PARSYS},
            {"linkTarget", StringUtils.EMPTY, "target"},
            {FIELD_PAGE_URL, getPageUrl(_currentPage)},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(_currentPage)},
            {FIELD_PAGE_TITLE_SUBTITLE, DEFAULT_SUBTITLE},
            {TagConstants.PN_TAGS, new String[]{}},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, FIELD_ARIA_DATA_ATTRIBUTE_ROLE},
            {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
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

    componentProperties.putAll(processBadgeRequestConfig(componentProperties,_resourceResolver, request), true);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<%@ include file="badgeconfig.jsp" %>

<c:choose>

    <%-- NORMAL BADGES --%>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleCategoryDescription'}">
        <%@ include file="badge.cardImageTitleCategoryDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIcon'}">
        <%@ include file="badge.cardIcon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleCategoryDescriptionAction'}">
        <%@ include file="badge.cardIconTitleCategoryDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleCategoryDescription'}">
        <%@ include file="badge.cardIconTitleCategoryDescription.jsp" %>
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

    <c:when test="${COMPONENT_BADGE eq 'badge.cardTitleDescriptionAction'}">
        <%@ include file="badge.cardTitleDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleCategoryDescriptionAction'}">
        <%@ include file="badge.cardImageTitleCategoryDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleSubtitleDescriptionAction'}">
        <%@ include file="badge.cardImageTitleSubtitleDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleAction'}">
        <%@ include file="badge.cardImageTitleAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconDescription'}">
        <%@ include file="badge.cardIconDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleDescription'}">
        <%@ include file="badge.cardIconTitleDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconTitleDescriptionAction'}">
        <%@ include file="badge.cardIconTitleDescriptionAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardImageTitleDescription'}">
        <%@ include file="badge.cardImageTitleDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardHorizontalIconTitleCatefgoryDescriptionAction'}">
        <%@ include file="badge.cardHorizontalIconTitleCategoryDescriptionAction.jsp" %>
    </c:when>


    <%-- ACTION BADGES --%>


    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionIconDescription'}">
        <%@ include file="badge.cardActionIconDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionIconTitleCategoryDescription'}">
        <%@ include file="badge.cardActionIconTitleCategoryDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionIconTitleDescription'}">
        <%@ include file="badge.cardActionIconTitleDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionImageTitle'}">
        <%@ include file="badge.cardActionImageTitle.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionImageTitleCategoryDescription'}">
        <%@ include file="badge.cardActionImageTitleCategoryDescription.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardActionImageTitleDescription'}">
        <%@ include file="badge.cardActionImageTitleDescription.jsp" %>
    </c:when>


    <%-- DEFAULT BADGES --%>

    <c:when test="${COMPONENT_BADGE eq 'badge.metadata'}">
        <%@ include file="badge.metadata.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge' or COMPONENT_BADGE eq 'badge.default'}">
        <%@ include file="badge.default.jsp" %>
    </c:when>

    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT_HIDDEN}">
        <%@ include file="variant.hidden.jsp" %>
    </c:when>

    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>