<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
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
            {"variant", DEFAULT_VARIANT},
            {"useParentPageTitle", false},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"titleFormat",""},
            {"subTitleFormat",""},
            {"cq:tags", new String[]{}},
            {"menuColor", StringUtils.EMPTY},
            {"showTags", "no"},
            {"eventDisplayDateFormat",""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));

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

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

<%@ include file="variant.default.jsp" %>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
