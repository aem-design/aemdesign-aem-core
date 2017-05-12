<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="../formsData.jsp" %>
<%

    Object[][] componentFields = {
            {"variant", "aemdesign"},
            {"jcr:description", StringUtils.EMPTY},
            {"multivalue", false},
            {"hideTitle", false},
            {"width", String.class},
            {"type", "text"},
            {"placeHolder", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));


    //Form attributes
    componentProperties.putAll(getComponentFormProperties(_slingRequest, _resource));

    Map<String, String> displayValues = FormsHelper.getOptions(_slingRequest, _resource);

    if (displayValues == null) {
        displayValues = new LinkedHashMap<String, String>();
        displayValues.put("item1",  _i18n.getVar("Item 1"));
        displayValues.put("item2",  _i18n.getVar("Item 2"));
        displayValues.put("item3",  _i18n.getVar("Item 3"));
    }

    //Title
    String title = _i18n.getVar(FormsHelper.getTitle(_resource, "Selection"));

    componentProperties.put("displayValues", displayValues);

    componentProperties.put("title", title);

    boolean multiSelect = FormsHelper.hasMultiSelection(_resource);

    componentProperties.put("multiSelect", multiSelect);

    //style
    String inputCss = FormsHelper.getCss(_properties, "form_field form_field_select");

    componentProperties.put("inputCss", inputCss);

%>

<c:set var="componentProperties" value="<%= componentProperties %>"/>

<c:choose>
    <c:when test="${componentProperties.variant eq 'aemdesign'}" >
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <sling:include path="/libs/foundation/components/form/dropdown/dropdown.jsp" />
    </c:otherwise>
</c:choose>
<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>
