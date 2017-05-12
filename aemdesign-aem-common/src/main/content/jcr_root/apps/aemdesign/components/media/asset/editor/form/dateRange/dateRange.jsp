<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@page import="java.util.List" %>
<%@page import="com.day.cq.dam.api.Asset" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormResourceEdit" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper" %>
<%@ page import="com.adobe.xmp.XMPDateTime" %>
<%
    final String DEFAULT_DATE_RANGE_PATTERN = "yyyy";
    final String I18N_CATEGORY = "assetviewer";


    Object[][] componentFields = {
            {"variant", "aemdesign"},
            {"separator", "-"},
            {"title", _i18n.get("dateRangeField", I18N_CATEGORY)},
            {"fromDateField", StringUtils.EMPTY},
            {"fromDatePattern", DEFAULT_DATE_RANGE_PATTERN},

            {"toDateField", StringUtils.EMPTY},
            {"toDatePattern", DEFAULT_DATE_RANGE_PATTERN}

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    List<Resource> resources = FormResourceEdit.getResources(_slingRequest);

    if (resources != null) {

        if (resources.size() > 1) {

        } else {

            Resource r = resources.get(0);

            Asset asset = r.adaptTo(Asset.class);

            try {
                // it might happen that the adobe xmp lib creates an array
                if (componentProperties.get("fromDateField", String.class).length() > 0){
                    String fromDateField = componentProperties.get("fromDateField", String.class);
                    XMPDateTime fromDate = null;

                    Object titleObj = asset.getMetadata(fromDateField);

                    if (titleObj instanceof Object[]) {
                        Object[] dateArray = (Object[]) titleObj;
                        fromDate = (dateArray.length > 0) ? (XMPDateTime)dateArray[0] : null;
                    } else {

                        fromDate = (XMPDateTime)titleObj;
                    }
                    if (fromDate != null){
                        componentProperties.put("fromDate", fromDate.getCalendar());
                    }

                }
            } catch (NullPointerException e) {
                _log.error("failed to retrieve metadata object " + e.getMessage(), e);
            }

            try{

                if (componentProperties.get("toDateField", String.class).length() > 0) {

                    String toDateField = componentProperties.get("toDateField", String.class);
                    XMPDateTime toDate = null;

                    Object titleObj = asset.getMetadata(toDateField);
                    if (titleObj instanceof Object[]) {
                        Object[] titleArray = (Object[]) titleObj;
                        toDate = (titleArray.length > 0) ? (XMPDateTime)titleArray[0] : null;
                    } else {
                        toDate = (XMPDateTime)titleObj;
                    }
                    if (toDate != null){
                        componentProperties.put("toDate", toDate.getCalendar());
                    }
                }


            } catch (NullPointerException e) {
                _log.error("failed to retrieve metadata object " + e.getMessage(), e);
            }
        }
    }


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'aemdesign'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
</c:choose>
<%--<%@include file="/apps/aemdesign/global/component-badge.jsp" %>--%>
