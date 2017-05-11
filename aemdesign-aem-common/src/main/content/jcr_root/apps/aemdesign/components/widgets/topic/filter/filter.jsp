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
            {"variant", "default"}
    };

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);
        LinkedHashMap<String, Tag> filterTag = getTagsMap(_adminTagManager,  _currentNode, "filterList");
        LinkedHashMap<String, Tag> defaultFilter = getTagsMap(_adminTagManager,  _currentNode, "defaultFilters");

        JSONArray jsonArray = new JSONArray() ;
        if( filterTag != null){
            for (String key : filterTag.keySet()){

                HashMap event = new HashMap();
                event.put("name", filterTag.get(key).getTitle());
                event.put("filter", filterTag.get(key).getTagID());

                Resource res = filterTag.get(key).adaptTo(Resource.class);

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
                componentFields[0][1] = jsonArray.toString();
            }
        }


    } catch (Exception ex) {

        out.write( Throwables.getStackTraceAsString(ex) );

    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }

    ComponentProperties componentProperties = getComponentStyleProperties(pageContext);

    //out.println("dataAttributes before 1:" + componentProperties.get("dataAttributes", String.class));
    componentProperties.putAll(getComponentProperties(pageContext, componentFields));
    //out.println("dataAttributes before :" + componentProperties.get("dataAttributes", String.class));
    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties, _component, _sling));
    //out.println("dataAttributes after :" + componentProperties.get("dataAttributes", String.class));


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
