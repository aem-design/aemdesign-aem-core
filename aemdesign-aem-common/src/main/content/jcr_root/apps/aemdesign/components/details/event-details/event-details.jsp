<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="./common.jsp" %>
<%

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"title", DEFAULT_TITLE},
            {"hideSiteTitle", DEFAULT_HIDE_SITE_TITLE},
            {"description", DEFAULT_DESCRIPTION},
            {"hideSeparator", DEFAULT_HIDE_SEPARATOR},
            {"hideSummary", DEFAULT_HIDE_SUMMARY},
            {"eventStartDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventEndDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"eventTime", DEFAULT_EVENT_TIME},
            {"eventLoc", DEFAULT_EVENT_LOC},
            {"eventRefLabel", DEFAULT_EVENT_REF_LABEL},
            {"eventRefLink", DEFAULT_EVENT_REF_LINK},
            {"eventRefLabel2", DEFAULT_EVENT_REF_LABEL},
            {"eventRefLink2", DEFAULT_EVENT_REF_LINK},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"useParentPageTitle", false},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"titleFormat",""},
            {"subTitleFormat",""},
            {"cq:tags", new String[]{}},
            {"menuColor", StringUtils.EMPTY},
            {"showTags", false},
            {"subCategory", StringUtils.EMPTY},
            {FIELD_PAGE_URL, getPageUrl(_currentPage)},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(_currentPage)},
            {"eventDisplayDateFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    //TODO: move this admin session usage into function
    ResourceResolver adminResourceResolver = this.openAdminResourceResolver(_sling);
    try {
        TagManager adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

        String category = this.getTags(adminTagManager, componentProperties.get("cq:tags", new String[]{}), _currentPage.getLanguage(true));

        componentProperties.put("category", category);

    } catch (Exception ex) {
        LOG.error("event-details: " + ex.getMessage(), ex);
        //out.write( Throwables.getStackTraceAsString(ex) );
    } finally {
        closeAdminResourceResolver(adminResourceResolver);
    }

    // retrieve component title
    componentProperties.put("componentTitle", _component.getTitle());

    if ((Boolean)(componentProperties.get("useParentPageTitle"))) {
        Page parentPage = _currentPage.getParent();
        String parentPageTitle = parentPage.getPageTitle();
        componentProperties.put("parentPageTitle", parentPageTitle);

    }

    componentProperties.putAll(processComponentFields(componentProperties,_i18n,_sling));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getPageImgReferencePath(_currentPage),
            FIELD_PAGE_IMAGE));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_SECONDARY));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_BACKGROUND));

    componentProperties.putAll(getBadgeRequestConfig(componentProperties,_resourceResolver, request));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>

    <c:when test="${COMPONENT_BADGE eq 'badge.card'}">
        <%@ include file="badge.card.jsp" %>
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


    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>