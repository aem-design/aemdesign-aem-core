<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="../formsData.jsp" %>
<%
    Object[][] componentFields = {
            {FIELD_VARIANT, "aemdesign"},
            {"jcr:description", StringUtils.EMPTY},
            {"multivalue", false},
            {"hideTitle", false},
            {"rows", 1},
            {"cols", 35},
            {"width", String.class},
            {"type", "text"},
            {"placeHolder", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    //Form attributes
    componentProperties.putAll(getComponentFormProperties(_slingRequest, _resource));

    Map<String, String> displayValues = FormsHelper.getOptions(_slingRequest, _resource);

    if (displayValues == null) {
        displayValues = Collections.singletonMap("item1", _i18n.getVar("Item 1"));
    }

    //Title
    String title = _i18n.getVar(FormsHelper.getTitle(_resource, "Selection"));

    componentProperties.put("displayValues", displayValues);


    componentProperties.put("title", title);

    String inputCss = FormsHelper.getCss(_properties, "form_field form_field_checkbox");

    componentProperties.put("inputCss", inputCss);

    %>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.variant eq 'aemdesign'}" >
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <sling:include path="/libs/foundation/components/form/checkbox/checkbox.jsp" />
    </c:otherwise>
</c:choose>
<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>
