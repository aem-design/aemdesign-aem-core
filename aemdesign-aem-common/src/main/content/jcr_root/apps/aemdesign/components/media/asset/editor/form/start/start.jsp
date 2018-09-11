<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'start' component

  Draws the start of a form

--%><%@ page session="false" import="com.day.cq.wcm.foundation.forms.ValidationInfo,
                 com.day.cq.wcm.foundation.forms.FormsConstants,
                 com.day.cq.wcm.foundation.forms.FormsHelper,
                 org.apache.sling.api.resource.Resource,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.api.resource.ValueMap,
                 org.apache.sling.scripting.jsp.util.JspSlingHttpServletResponseWrapper,
                 java.net.URLDecoder,
                 java.security.AccessControlException" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/libs/foundation/components/form/actions/edit/init.jsp" %><%
%><cq:setContentBundle/><%

    FormsHelper.startForm(slingRequest, new JspSlingHttpServletResponseWrapper(pageContext));

    String redirectURI = request.getRequestURI() + (FormResourceEdit.isMultiResource(slingRequest) ? "?" + FormResourceEdit.REOPEN_PARAM + "=true" : "");

    // we create the form div our selfs, and turn decoration on again.
    %><div class="form"><%
    %><input type="hidden" name=":redirect" value="<%= URLDecoder.decode(redirectURI,"UTF-8") %>"> <%
    componentContext.setDecorate(true);

    boolean readOnly = true;
    Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
    if (resources == null) {
        readOnly = false;
    }
    else {
        for (int i = 0; i < resources.size(); i++) {
            String path = resources.get(i).getPath();
            readOnly = !session.hasPermission(path, "set_property");
            if (!readOnly) break; // at least one asset with write permission detected
        }
    }
    if (readOnly) {
        FormsHelper.setFormReadOnly(slingRequest);
        %><style type="text/css">
            .asseteditor.page .section.end {
                display:none;
            }
        </style><%
    }
%>