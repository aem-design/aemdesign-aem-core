<%@ page import="com.day.cq.wcm.foundation.forms.ValidationInfo,
                 com.day.cq.wcm.foundation.forms.FormsConstants,
                 com.day.cq.wcm.foundation.forms.FormsHelper,
                 org.apache.sling.api.resource.Resource,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.api.resource.ValueMap,
                 org.apache.sling.scripting.jsp.util.JspSlingHttpServletResponseWrapper, com.day.cq.wcm.foundation.Placeholder"%><%
%><cq:setContentBundle/>
<cq:include script="/libs/foundation/components/form/start/abacus.jsp"/>

    <%

    renderFormStart(componentProperties.get("redirectPage", Page.class)
                    , componentProperties.get("actionType", String.class) ,
                     componentProperties.get("formid", String.class),  _slingRequest, _slingResponse);

    // we create the form div our selfs, and turn decoration on again.
    %><div ><%
    %><%= Placeholder.getDefaultPlaceholder(slingRequest, "Form Start", "", "cq-marker-start") %><%
        componentContext.setDecorate(true);
    // check if we have validation errors
    final ValidationInfo info = ValidationInfo.getValidationInfo(request);
    if ( info != null ) {
        %><p class="form_error"><fmt:message key="Please correct the errors and send your information again."/></p><%
        final String[] msgs = info.getErrorMessages(null);
        if ( msgs != null ) {
            for(int i=0;i<msgs.length;i++) {
                %><p class="form_error"><%=msgs[i]%></p><%
            }
        }
    }
%>