<%@include file="/libs/granite/ui/global.jsp" %>
<%
%>
<%@page session="false"
        import="com.adobe.granite.ui.components.Config,
                com.adobe.granite.ui.components.rendercondition.RenderCondition,
                com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition,
                org.apache.jackrabbit.api.security.user.Authorizable,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceUtil,
                org.apache.sling.api.resource.ValueMap" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ page import="static design.aem.utils.components.SecurityUtil.isUserMemberOf" %>
<%
  /**
   A condition that evaluates to true, if the node is not locked or if the current user session owns the lock, false otherwise.
   @name locked
   @location /apps/aemdesign/components/layout/contentblocklock/v2/contentblocklock/renderconditions/notlocked

   @example
   + mybutton
   - sling:resourceType = "granite/ui/components/foundation/button"
   + rendercondition
   - sling:resourceType = "dam/gui/components/admin/renderconditions/notlocked"
   */
  Config cfg = cmp.getConfig();
  String path = cmp.getExpressionHelper().getString(cfg.get("path", ""));
  boolean condition = true; //default value

//    Resource currentRes = UIHelper.getCurrentSuffixResource(slingRequest);
  Resource currentRes = resourceResolver.resolve(path);
  if (!ResourceUtil.isNonExistingResource(currentRes)) {
    try {
      Node currentResNode = currentRes.adaptTo(Node.class);
      ValueMap values = currentRes.adaptTo(ValueMap.class);
      Boolean islocked = values.get("islocked", false);
      String[] groups = values.get("groups", new String[]{"administrators"});
      log.error("XXXXXXXXXX currentRes: {}", currentRes.getPath());
      log.error("XXXXXXXXXX path: {}", path);
      log.error("XXXXXXXXXX locked: {}", islocked);
      log.error("XXXXXXXXXX groups: {}", groups);
      if (islocked) {

        final Authorizable authorizable = currentRes.getResourceResolver().adaptTo(Authorizable.class);
        final List<String> groupsList = Arrays.asList(groups);

        if (isUserMemberOf(authorizable, groupsList)) {
          log.error("XXXXXXXXXX ADMIN: {}", groups);
          condition = false;
        } else {
          log.error("XXXXXXXXXX NOT ADMIN: {}", groups);
          condition = true;
        }
      } else {
        log.error("XXXXXXXXXX NOT LOCKED: {}", groups);
        condition = false;
      }

    } catch (Exception ex) {
      log("Exception occurred while checking locked render condition: "
        + ex.getMessage());
    }
  }
  request.setAttribute(RenderCondition.class.getName(),
    new SimpleRenderCondition(condition));
%>
