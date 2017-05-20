<%@ page import="com.day.cq.wcm.api.Page" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "search";
    final String DEFAULT_I18N_CODE_LEGEND = "legendText";
    final String DEFAULT_I18N_CODE_PLACEHOLDER = "placeholderText";
    final String DEFAULT_I18N_CODE_LEBEL = "labelText";
    final String DEFAULT_I18N_CODE_SEARCH = "searchButtonText";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
            {"searchURL", "/en/search"},
            {"formMethod", "get"},
            {"formParameterName", StringUtils.EMPTY},
            {"feedUrl", new String[0],"feed-urls"},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


    String placeholderText = _properties.get("placeholderText","");
    String legendText = _properties.get("legendText","");
    String labelText = _properties.get("labelText","");
    String searchButtonText = _properties.get("searchButtonText","");

    componentProperties.put("placeholderText", getDefaultLabelIfEmpty(placeholderText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_PLACEHOLDER,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("legendText", getDefaultLabelIfEmpty(legendText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_LEGEND,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("labelText", getDefaultLabelIfEmpty(labelText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_LEBEL,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("searchButtonText", getDefaultLabelIfEmpty(searchButtonText,DEFAULT_I18N_CATEGORY,DEFAULT_I18N_CODE_SEARCH,DEFAULT_I18N_CATEGORY,_i18n));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<cq:includeClientLib categories="granite.csrf.standalone"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'advanced'}">
        <%@ include file="variant.quick.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
