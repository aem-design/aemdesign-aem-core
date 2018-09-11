<%@ page import="org.apache.sling.commons.json.JSONArray" %>
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<%@ page import="org.apache.commons.lang.ArrayUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    final String DEFALT_ATTRIBUTE_NAME_ISDEFAULT = "isdefault";

    Object[][] componentFields = {
            {"filters", StringUtils.EMPTY, "filters"},
            {"defaultFilters", StringUtils.EMPTY, "defaultFilters"},
            {"topicQueue", StringUtils.EMPTY, "topicqueue"},
            {"component", "topicFilters", "modules"},
            {"bindMethod", "foreach: {data:filters, afterRender: renderedHandler}", "bind"},
            {FIELD_VARIANT, DEFAULT_VARIANT}
    };


    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String[] tagsFilterList = componentProperties.get("filterList", new String[]{});
    String[] tagsDefaultFilters = componentProperties.get("defaultFilters", new String[]{});

    LinkedHashMap<String, Map> filterTag = getTagsAsAdmin(_sling, tagsFilterList, _slingRequest.getLocale());
    LinkedHashMap<String, Map> defaultFilter = getTagsAsAdmin(_sling, tagsDefaultFilters, _slingRequest.getLocale());

    //construct default filter list
    JSONArray jsonArray = new JSONArray() ;
    if( filterTag != null){
        for (String key : filterTag.keySet()){

            HashMap event = new HashMap();
            event.put("name", filterTag.get(key).get("title"));
            event.put("filter", filterTag.get(key).get("tagid"));

            if (defaultFilter != null) {
                if (defaultFilter.get(key) != null) {
                    event.put(DEFALT_ATTRIBUTE_NAME_ISDEFAULT, true);
                } else {
                    event.put(DEFALT_ATTRIBUTE_NAME_ISDEFAULT, false);
                }
            } else {
                if (jsonArray.length() != 0) {
                    event.put(DEFALT_ATTRIBUTE_NAME_ISDEFAULT, false);
                } else {
                    event.put(DEFALT_ATTRIBUTE_NAME_ISDEFAULT, true);
                }
            }

            jsonArray.put(event);
        }

    }
    componentProperties.put("defaultFilters",jsonArray.toString());


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>


<c:choose>
    <c:when test="${componentProperties.variant eq 'default'}">
        <%@ include file="variant.default.jsp"  %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
