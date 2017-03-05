<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.apache.commons.lang3.time.DateUtils" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>

<%
    final String DEFAULT_SHOW_BREADCRUMB = "yes";
    final String DEFAULT_SHOW_TOOLBAR = "yes";

    final String DEFAULT_STYLE = "default";
    final String I18N_CATEGORY = "tenderdetail";

    if (_currentNode.canAddMixin(com.day.cq.tagging.TagConstants.NT_TAGGABLE)){
        _currentNode.addMixin(com.day.cq.tagging.TagConstants.NT_TAGGABLE);
        _currentNode.getSession().save();
    }

    Object[][] componentFields = {
            {"title", _pageProperties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY)},
            {"description",  _pageProperties.get(JcrConstants.JCR_DESCRIPTION, StringUtils.EMPTY)},
            {"publishDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"closingDate", _pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance())},
            {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
            {"showToolbar", DEFAULT_SHOW_TOOLBAR},
            {"displayStyle", DEFAULT_STYLE}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.put("showBreadcrumb", BooleanUtils.toBoolean(componentProperties.get("showBreadcrumb", String.class)));
    componentProperties.put("showToolbar", BooleanUtils.toBoolean(componentProperties.get("showToolbar", String.class)));

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

    List<Tag> tags = getTags(_tagManager, _currentNode, TagConstants.PN_TAGS);

    String closingDateString = _properties.get("closingDate",StringUtils.EMPTY);

    String tenderStatusLabel = StringUtils.EMPTY;

    if (StringUtils.isNotEmpty(closingDateString)) {

        Calendar closingDate = componentProperties.get("closingDate", Calendar.getInstance());

        //check if tender is closed
        Boolean tenderClosed = new Date().after(closingDate.getTime());
        componentProperties.put("closed",tenderClosed);

        //get closing date format
        String dateFormatString = _i18n.get("closingDateFormat",I18N_CATEGORY);
        String dateDisplayFormatString = _i18n.get("closingDateDisplayFormat",I18N_CATEGORY);

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String closingDateText = dateFormat.format(closingDate.getTime());

        dateFormat = new SimpleDateFormat(dateDisplayFormatString);
        String closingDateDisplayText = dateFormat.format(closingDate.getTime());

        componentProperties.put("closingDateDisplayText", closingDateDisplayText);
        componentProperties.put("closingDateText", closingDateText);

        if (tenderClosed) {
            tenderStatusLabel = _i18n.get("closedDateText", I18N_CATEGORY, closingDateText, closingDateDisplayText);
        }else{
            tenderStatusLabel = _i18n.get("closingDateText", I18N_CATEGORY, closingDateText, closingDateDisplayText);
        }
    }
    componentProperties.put("tenderStatusLabel",tenderStatusLabel);
    componentProperties.put("tags",tags);
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

