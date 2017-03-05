<%@taglib prefix="xss" uri="http://www.adobe.com/consulting/acs-aem-commons/xss" %>
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
            {"variant", "aemdesign"},
            {"type", "text"},
            {"placeHolder", StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));

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
    //<c:if test="${status.index == 0 && componentProperties.multiRes}">
    //  <span class="mr_write">
    //      <input type="checkbox" name="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrName)}" id="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrName)}" value="true" <c:if test="${requestScope[componentProperties.mrName]}">  checked="checked" </c:if> >
    //  </span>
    //</c:if>
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
        /*<c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <input type="hidden" disabled name="${formatedName}">
            ${textFormatMap[status.index]}
        */
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[0]);
    }else if("hidden".equals(componentProperties.get("type", String.class))){
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[1]);
    }else if("submit".equals(componentProperties.get("type", String.class))){
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[2]);
    }else if (componentProperties.get("rows", Integer.class).equals(Integer.valueOf(1))){
        /*<c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
          <input class="${componentProperties.inputCss}"
            id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
            name="${formatedName}"
            value="${xss:encodeForHTMLAttr(xssAPI, actualValue)}"
            size="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
            type="${xss:encodeForHTMLAttr(xssAPI,componentProperties.type)}"
            placeHolder="${xss:encodeForHTMLAttr(xssAPI,componentProperties.placeHolder)}"
            <c:if test="${not empty componentProperties.width}">
               style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            onkeydown="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrChangeHandler)}" >
        */
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[3]);
    }else{
        /*<c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
        <textarea class="${componentProperties.textAreaCss}"
            id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
            name="${formatedName}"
            rows="${xss:encodeForHTMLAttr(xssAPI,componentProperties.rows)}"
            cols="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
            <c:if test="${not empty componentProperties.width}">
                style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            onkeydown="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrChangeHandler)}" >
            ${xss:encodeForHTML(xssAPI, actualValue)}
        </textarea>
         */
        componentProperties.put("htmlInputTag", HTML_INPUT_TAGS[4]);
    }

    //Add Multi Value
    if ((componentProperties.get("readOnly", false)== false) && (componentProperties.get("multivalue", false))){
    //<c:set value='${componentProperties.width eq null ? "null" : ("\'" + xss:getValidInteger(xssAPI, componentProperties.width, 100) +"\'")}' var="width"/>
    //<span class="form_mv_add" onclick="CQ_form_addMultivalue('${xss:encodeForJSString(xssAPI, componentProperties.name)}', ${componentProperties.rows}, ${width});${componentProperties.forceMrChangeHandler}">[+]</span>
        componentProperties.put("addMultiValue", true);
    }

    //Remove Multi Value
    if ((componentProperties.get("values", new String [0]).length > 1) ){
        // <span class="form_mv_remove" onclick="CQ_form_removeMultivalue('${xss:encodeForJSString(xssAPI, componentProperties.name)}',${status.index});${componentProperties.forceMrChangeHandler}">&nbsp;[&ndash;]</span>
        componentProperties.put("removeMultiValue", true);
    }

    if (componentProperties.get("multivalue", false) && componentProperties.get("readOnly", false) == false) {
    %><%@include file="/libs/foundation/components/form/text/multivalue.jsp"%><%
    }

 %>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant eq 'aemdesign'}" >
        <%@include file="style.default.jsp" %>
    </c:when>
    <c:otherwise>
        <sling:include path="/libs/foundation/components/form/text/text.jsp" />
    </c:otherwise>
</c:choose>

<%--
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
--%>