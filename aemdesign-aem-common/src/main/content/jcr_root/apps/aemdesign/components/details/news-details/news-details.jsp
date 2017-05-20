<%@ page import="com.day.cq.tagging.TagConstants" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    final String DEFAULT_SHOW_BREADCRUMB = "yes";
    final String DEFAULT_SHOW_TOOLBAR = "yes";
    final String I18N_CATEGORY = "news-detail";

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description", _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"author", ""},
            {TagConstants.PN_TAGS, new String[]{}},
            {"variant", DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields, DEFAULT_FIELDS_STYLE, DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));

    Calendar publishDate = _properties.get("publishDate",_pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance()));

    componentProperties.put("publishDate",publishDate);

    //get format strings from dictionary
    String dateFormatString = _i18n.get("publishDateFormat",I18N_CATEGORY);
    String dateDisplayFormatString = _i18n.get("publishDateDisplayFormat",I18N_CATEGORY);

    //format date into formatted date
    SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
    String publishDateText = dateFormat.format(publishDate.getTime());

    //format date into display date
    dateFormat = new SimpleDateFormat(dateDisplayFormatString);
    String publishDisplayDateText = dateFormat.format(publishDate.getTime());

    componentProperties.put("publishDateText",publishDateText);
    componentProperties.put("publishDisplayDateText",publishDisplayDateText);

    //get full published date display text
    String newsDateStatusText = _i18n.get("newsDateStatusText", I18N_CATEGORY, publishDateText, publishDisplayDateText);
    componentProperties.put("newsDateStatusText",newsDateStatusText);

    String[] tags = getMultiplePropertyString(_currentNode,TagConstants.PN_TAGS);

    componentProperties.put("tags",getTagsAsAdmin(_sling, tags, _slingRequest.getLocale()));

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
