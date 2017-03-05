<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    final String DEFAULT_SHOW_BREADCRUMB = "yes";
    final String DEFAULT_SHOW_TOOLBAR = "yes";
    final String DEFAULT_STYLE = "default";
    final String I18N_CATEGORY = "newsdetail";

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {TagConstants.PN_TAGS, new String[]{}},
            {"displayStyle", DEFAULT_STYLE}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    Calendar publishDate = _properties.get("publishDate",_pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance()));

    componentProperties.put("publishDate",publishDate);

    String dateFormatString = _i18n.get("publishDateFormat",I18N_CATEGORY);
    String dateDisplayFormatString = _i18n.get("publishDateDisplayFormat",I18N_CATEGORY);

    SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
    String publishDateText = dateFormat.format(publishDate.getTime());

    dateFormat = new SimpleDateFormat(dateDisplayFormatString);
    String publishDisplayDateText = dateFormat.format(publishDate.getTime());

    componentProperties.put("publishDateText",publishDateText);
    componentProperties.put("publishDisplayDateText",publishDisplayDateText);

    String newsStatusLabel = _i18n.get("newsDateStatusText", I18N_CATEGORY, publishDateText, publishDisplayDateText);
    componentProperties.put("newsStatusLabel",newsStatusLabel);

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {
        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

        componentProperties.put("componentAttributes", compileComponentAttributes(_adminTagManager, componentProperties, _component));

        List<Tag> tags = getTags(_adminTagManager, _currentNode, TagConstants.PN_TAGS);

        componentProperties.put("tags",tags);

    } catch (Exception ex) {
        out.write( Throwables.getStackTraceAsString(ex) );
    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }


%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.displayStyle == 'default'}">
        <%@include file="style.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="style.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>