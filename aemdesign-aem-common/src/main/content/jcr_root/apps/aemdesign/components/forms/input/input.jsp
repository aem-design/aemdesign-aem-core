<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="../formsData.jsp" %>


<%
    String[] HTML_INPUT_TAGS = {"inputHiddenWithLabelOnly","inputHidden","inputSubmit","inputText","textArea"};

    Object[][] componentFields = {
            {"jcr:description", StringUtils.EMPTY},
            {"multivalue", false},
            {"hideTitle", false},
            {"rows", 1},
            {"cols", 35},
            {"width", StringUtils.EMPTY},
            {FIELD_VARIANT, "aemdesign"},
            {"type", "text"},
            {"placeHolder", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("rows", _xssAPI.getValidInteger(componentProperties.get("rows",String.class), 1));
    componentProperties.put("cols", _xssAPI.getValidInteger(componentProperties.get("cols",String.class), 35));

    componentProperties.putAll(getComponentFormProperties(_slingRequest, _resource));

    //Text need to place empty string
    //Override the common form properties
    String[] values = FormsHelper.getValues(_slingRequest, _resource);
    if (values == null) {
        values = new String[]{""};
    }
    componentProperties.put("values", values);

    final boolean readOnly = FormsHelper.isReadOnly(_slingRequest, _resource);
    componentProperties.put("readOnly", readOnly);

    String title = _i18n.getVar(FormsHelper.getTitle(_resource, "Text"));
    componentProperties.put("title", title);

    //Multi Resource
    boolean multiRes = FormResourceEdit.isMultiResource(_slingRequest);

    componentProperties.put("multiRes", multiRes);


    String mrName = componentProperties.get("name", String.class) + FormResourceEdit.WRITE_SUFFIX;
    componentProperties.put("mrName", mrName);

    String mrChangeHandler = multiRes ? "cq5forms_multiResourceChange(event, '" + _xssAPI.encodeForJSString(mrName) + "');" : "";
    componentProperties.put("mrChangeHandler", mrChangeHandler);

    String forceMrChangeHandler = multiRes ? "cq5forms_multiResourceChange(event, '" + _xssAPI.encodeForJSString(mrName) + "', true);" : "";
    componentProperties.put("forceMrChangeHandler", forceMrChangeHandler);

    //Style
    String inputCss = FormsHelper.getCss(_properties, "form_field form_field_text" + (componentProperties.get("multivalue", false) ? " form_field_multivalued" : "" ));
    componentProperties.put("inputCss", inputCss);

    String textAreaCss = FormsHelper.getCss(_properties, "form_field form_field_textarea") ;
    componentProperties.put("textAreaCss", textAreaCss);

    //output Field
    if (componentProperties.get("readOnly", false)){
        //TextFormat
        Map<String, String> textFormatMap = new HashMap<String, String>();
        String []  vals = componentProperties.get("values", new String[]{""});
        for (int i = 0; i < vals.length ; i ++){
            String elementValue = vals[i];
            if (elementValue.length() == 0){
                elementValue = " ";
            }
            String sb = new TextFormat().format(elementValue);
            textFormatMap.put(String.valueOf(i), sb);

        }
        componentProperties.put("textFormatMap", textFormatMap);

        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[0]);
    }else if("hidden".equals(componentProperties.get("type", String.class))){
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[1]);
    }else if("submit".equals(componentProperties.get("type", String.class))){
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[2]);
    }else if (componentProperties.get("rows", Integer.class).equals(Integer.valueOf(1))){
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[3]);
    }else{
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[4]);
    }

    //Add Multi Value
    if ((componentProperties.get("readOnly", false)== false) && (componentProperties.get("multivalue", false))){
    //<c:set value='${componentProperties.width eq null ? "null" : ("\'" + xssAPI.getValidInteger(componentProperties.width, 100) +"\'")}' var="width"/>
    //<span class="form_mv_add" onclick="CQ_form_addMultivalue('${xssAPI.encodeForJSString(componentProperties.name)}', ${componentProperties.rows}, ${width});${componentProperties.forceMrChangeHandler}">[+]</span>
        componentProperties.put("addMultiValue", true);
    }

    //Remove Multi Value
    if ((componentProperties.get("values", new String [0]).length > 1) ){
        // <span class="form_mv_remove" onclick="CQ_form_removeMultivalue('${xssAPI.encodeForJSString(componentProperties.name)}',${status.index});${componentProperties.forceMrChangeHandler}">&nbsp;[&ndash;]</span>
        componentProperties.put("removeMultiValue", true);
    }

    if (componentProperties.get("multivalue", false) && componentProperties.get("readOnly", false) == false) {
    %><%@include file="/libs/foundation/components/form/text/multivalue.jsp"%><%
    }

 %>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'aemdesign'}" >
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <sling:include path="/libs/foundation/components/form/text/text.jsp" />
    </c:otherwise>
</c:choose>

<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>
