<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
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
    final Boolean DEFAULT_HIDE_DESCRIPTION = false;
    final Boolean DEFAULT_SHOW_BREADCRUMB = true;
    final Boolean DEFAULT_SHOW_TOOLBAR = true;
    final Boolean DEFAULT_SHOW_PARSYS = true;


    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"title", DEFAULT_TITLE},
            {"titleFormat",""},
            {"description", DEFAULT_DESCRIPTION},
            {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"showParsys", DEFAULT_SHOW_PARSYS},
            {"linkTarget", StringUtils.EMPTY, "target"},
            {FIELD_PAGE_URL, getPageUrl(_currentPage)},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(_currentPage)},
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

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getPageImgReferencePath(_currentPage),
            FIELD_PAGE_IMAGE));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_SECONDARY));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_BACKGROUND));

    componentProperties.put(FIELD_REDIRECT_TARGET,_pageProperties.get(FIELD_REDIRECT_TARGET,""));

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling), false);

    componentProperties.putAll(processBadgeRequestConfig(componentProperties,_resourceResolver, request), true);


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${COMPONENT_BADGE eq 'badge.card'}">
        <%@ include file="badge.card.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardAction'}">
        <%@ include file="badge.cardAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardSelect'}">
        <%@ include file="badge.cardSelect.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIcon'}">
        <%@ include file="badge.cardIcon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconAction'}">
        <%@ include file="badge.cardIconAction.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIconSelect'}">
        <%@ include file="badge.cardIconSelect.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.icon'}">
        <%@ include file="badge.icon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.image'}">
        <%@ include file="badge.image.jsp" %>
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

