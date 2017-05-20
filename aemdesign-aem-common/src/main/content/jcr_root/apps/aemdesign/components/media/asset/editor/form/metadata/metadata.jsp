<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@page import="java.util.List" %>
<%@page import="java.security.AccessControlException" %>
<%@page import="org.apache.commons.lang3.BooleanUtils" %>

<%@page import="com.day.cq.dam.api.Asset" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormResourceEdit" %>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper" %>
<%
    final String I18N_CATEGORY = "assetviewer";

    Object[][] componentFields = {
            {"variant", "aemdesign"},
            {"meta/namespace", "dam"},
            {"meta/localPart", StringUtils.EMPTY},
            {"meta/type", "String"},
            {"meta/label", null},
            {"meta/multivalue", false},
            {"multiValueSeparator", ", "},

            {"width", StringUtils.EMPTY},
            {"rows", "1"}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String name = "./jcr:content/metadata/" +  componentProperties.get("meta/namespace", String.class) + ":" + componentProperties.get("meta/localPart", String.class) ;

    final String id = FormsHelper.getFieldId(_slingRequest, _resource);

    final String type = properties.get("meta/type", "String");

    final boolean readOnly = FormsHelper.isReadOnly(_slingRequest, _resource);

    String title = componentProperties.get("meta/label", String.class);

    String localPart = componentProperties.get("meta/localPart", String.class);

    if (title == null ) {

        if ("".equals(localPart)){
            title = _i18n.get("titleAltReadMore", I18N_CATEGORY) ;
        }else{
            title = localPart;
        }

    }
    componentProperties.put("title", title);

    final boolean required = FormsHelper.isRequired(_resource);

    String width = componentProperties.get("width", String.class);
    String widthStyle = StringUtils.EMPTY;
    if (!width.equals("")) {
        widthStyle = "width:" + width + ";";
    }

    componentProperties.put("widthStyle", widthStyle);

    String rows = componentProperties.get("rows", String.class);

    String[] values = null;
    try{
        values = FormsHelper.getValues(_slingRequest, _resource, name);

        if (componentProperties.get("meta/type", "String").equals("Tags")){

            ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

            try {

                TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

                if (values != null){

                    Map<String, String> tagMap = new HashMap<String, String>();

                    for (String s : values){

                        Tag tag = _adminTagManager.resolve(s);
                        if (tag != null){
                            tagMap.put(s, tag.getTitle(_currentPage.getLanguage(true)));
                        }

                    }
                    componentProperties.put("tagMap", tagMap);


                }

            } catch (Exception ex) {
                out.write( Throwables.getStackTraceAsString(ex) );
            } finally {
                this.closeAdminResourceResolver(adminResourceResolver);
            }

        }

    }catch(Exception e){
        _log.error("failed to retrieve values "+ e.getMessage(), e);

    }
    if (values == null) {
        values = new String[]{""};
    }

    componentProperties.put("values", values);

    final boolean mv = values.length > 1 ? true : componentProperties.get("meta/multivalue", false);

    String mvCls = mv ? "mv" : "";

    componentProperties.put("mvCls", mvCls);

    boolean multiRes = FormResourceEdit.isMultiResource(_slingRequest);
    String mrName = name + FormResourceEdit.WRITE_SUFFIX;

    componentProperties.put("mrName", mrName);

    String mrChangeHandler = multiRes ? "cq5forms_multiResourceChange(event, '" + _xssAPI.encodeForJSString(mrName) + "');" : "";

    componentProperties.put("mrChangeHandler", mrChangeHandler);

    String forceMrChangeHandler = multiRes ? "cq5forms_multiResourceChange(event, '" + _xssAPI.encodeForJSString(mrName) + "', true);" : "";
    componentProperties.put("forceMrChangeHandler", forceMrChangeHandler);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${componentProperties.variant == 'aemdesign'}">
        <%@ include file="variant.default.jsp" %>
    </c:when>
</c:choose>
<%--<%@include file="/apps/aemdesign/global/component-badge.jsp" %>--%>
