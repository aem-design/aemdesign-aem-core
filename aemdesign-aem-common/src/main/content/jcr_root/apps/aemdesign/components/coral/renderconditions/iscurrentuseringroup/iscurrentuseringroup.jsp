<%@include file="/libs/granite/ui/global.jsp"%>
<%
%><%@page session="false"
 import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.rendercondition.RenderCondition,
                  com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition,
                  org.apache.jackrabbit.api.security.user.Authorizable"%>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="static design.aem.utils.components.SecurityUtil.isUserMemberOf" %>
<%
    /**
     A condition that evaluates to true, if the node is not locked or if the current user session owns the lock, false otherwise.
     @name group
     @location /apps/aemdesign/components/coral/renderconditions/iscurrentuseringroup

     @example
     + mybutton
     - sling:resourceType = "granite/ui/components/foundation/button"
     + rendercondition
     - sling:resourceType = "dam/gui/components/admin/renderconditions/notlocked"
     */
    Config cfg = cmp.getConfig();
    String[] groups = cfg.get("groups", new String[0]);
    boolean condition = true; //default value

    if (groups.length == 0) {
      return;
    }

    try {
        final Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);
        final List<String> groupsList = Arrays.asList(groups);
        if (isUserMemberOf(authorizable, groupsList)) {
            condition = false;
        } else {
            condition = true;
        }

    } catch (Exception ex) {
        log("Exception occurred while checking locked render condition: "
            + ex.getMessage());
    }
    request.setAttribute(RenderCondition.class.getName(),
        new SimpleRenderCondition(condition));
%>
