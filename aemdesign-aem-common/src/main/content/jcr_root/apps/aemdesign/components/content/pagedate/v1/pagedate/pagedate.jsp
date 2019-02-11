<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.day.cq.replication.ReplicationStatus" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "pagedate";


    //not using lamda is available so this is the best that can be done
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"publishDate", _pageProperties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED,_pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance()))},
            {"jcr:created", ""}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    Calendar publishDate = componentProperties.get("publishDate",Calendar.getInstance()); //_pageProperties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED,_pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance()));

//    componentProperties.put("publishDate",publishDate);

    //get format strings from dictionary
    String dateFormatString = _i18n.get("publishDateFormat",DEFAULT_I18N_CATEGORY);
    String dateDisplayFormatString = _i18n.get("publishDateDisplayFormat",DEFAULT_I18N_CATEGORY);

    try {

        //format date into formatted date
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String publishDateText = dateFormat.format(publishDate.getTime());

        //format date into display date
        dateFormat = new SimpleDateFormat(dateDisplayFormatString);
        String publishDisplayDateText = dateFormat.format(publishDate.getTime());

        componentProperties.put("publishDateText", publishDateText);
        componentProperties.put("publishDisplayDateText", publishDisplayDateText);

        //update attributes - consider updating to using componentProperties.attr
        Object[][] componentAttibutes = {
                {"datetime", publishDateText},
        };
        componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, componentAttibutes));

    } catch (Exception ex) {
        LOG.error("dateFormatString: " + dateFormatString);
        LOG.error("dateDisplayFormatString: " + dateDisplayFormatString);
        LOG.error("publishDate: " + publishDate);
        LOG.error("path: " + resource.getPath());
        LOG.error("pagedate error: " + ex.getMessage(), ex);
    }


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>

    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>

</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

