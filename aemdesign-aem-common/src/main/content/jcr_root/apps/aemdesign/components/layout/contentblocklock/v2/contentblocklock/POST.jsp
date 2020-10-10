<%@page session="false"
        import="com.day.cq.commons.servlets.HtmlStatusResponseHelper,
                com.day.cq.i18n.I18n,
                org.apache.jackrabbit.api.security.user.Authorizable" %>
<%@ page import="static design.aem.utils.components.SlingPostUtil.processDeletes" %>
<%@ page import="static design.aem.utils.components.SlingPostUtil.writeContent" %>
<%@ page import="static design.aem.utils.components.SlingPostUtil.*" %>
<%@ page import="static design.aem.utils.components.SecurityUtil.isUserMemberOf" %>
<%@ page import="org.apache.sling.api.servlets.HtmlResponse" %>
<%@ page import="javax.jcr.Node" %>
<%@ page import="org.apache.log4j.Logger," %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<cq:defineObjects/>
<%

  javax.jcr.Node _currentNode = (javax.jcr.Node) pageContext.getAttribute("currentNode");
  org.slf4j.Logger _log = (org.slf4j.Logger) pageContext.getAttribute("log");
  org.apache.sling.api.resource.ValueMap _properties = (org.apache.sling.api.resource.ValueMap) pageContext.getAttribute("properties");
  org.apache.sling.api.resource.Resource _resource = (org.apache.sling.api.resource.Resource) pageContext.getAttribute("resource");
  org.apache.sling.api.resource.ResourceResolver _resourceResolver = (org.apache.sling.api.resource.ResourceResolver) pageContext.getAttribute("resourceResolver");
  org.apache.sling.api.scripting.SlingScriptHelper _sling = (org.apache.sling.api.scripting.SlingScriptHelper) pageContext.getAttribute("sling");
  org.apache.sling.api.SlingHttpServletRequest _slingRequest = (org.apache.sling.api.SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
  com.day.cq.tagging.TagManager _tagManager = _resourceResolver.adaptTo(com.day.cq.tagging.TagManager.class);

  I18n _i18n = new I18n(request);

  HtmlResponse htmlResponse = null;
  _log.error("Content Block Lock POST");

  boolean update = false;

  try {
    String[] groups = _properties.get("groups", new String[]{"administrators"});
    Boolean islocked = _properties.get("islocked", false);
    _log.error("Content Block Lock POST: islocked {}", islocked);
    _log.error("Content Block Lock POST: groups {}", groups);
    if (islocked) {
      final Authorizable authorizable = _resourceResolver.adaptTo(Authorizable.class);
      final List<String> groupsList = Arrays.asList(groups);

      if (isUserMemberOf(authorizable, groupsList)) {
        _log.error("Content Block Lock POST: ADMIN");

        update = true;
        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true, _i18n.get("This content block is locked for updates but you have access to update"), _resource.getPath());
      } else {
        _log.error("Content Block Lock POST: LOCKED");
        htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false, _i18n.get("This content block is locked for updates."), _resource.getPath());
      }
    } else {
      _log.error("Content Block Lock POST: NOT LOCKED");

      update = true;

      htmlResponse = HtmlStatusResponseHelper.createStatusResponse(true, _i18n.get("This content block is not locked."), _resource.getPath());
    }


    if (update) {
      processDeletes(_currentNode, request);
      writeContent(_currentNode, request);

      List<String> tagsParameters = getTagRequestParameters(request);
      for (String name : tagsParameters) {
        List<String> processedTags = getProcessedTags(_tagManager, name, request);
        if (!processedTags.isEmpty()) {
          Node finalNode = getParentNode(_currentNode, name);
          String propertyName = getPropertyName(name);
          finalNode.setProperty(propertyName, processedTags.toArray(new String[0]));
        }
      }

      _currentNode.getSession().save();
    }


  } catch (Exception e) {
    _log.error("Error occur creating a new page", e);

    htmlResponse = HtmlStatusResponseHelper.createStatusResponse(false, _i18n.get("Cannot update content ({0})", null, e.getMessage()));
  }

  htmlResponse.send(response, true);
%>

