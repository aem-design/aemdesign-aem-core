<%@page session="false" import="
                                com.day.cq.commons.jcr.JcrConstants,
                                com.day.cq.wcm.api.designer.Style,
                                com.day.cq.wcm.foundation.Paragraph,
                                com.day.cq.wcm.foundation.ParagraphSystem,
                                org.apache.commons.lang3.StringUtils,
                                static org.apache.commons.lang3.StringUtils.*,
                                com.day.cq.wcm.foundation.Placeholder,
                                org.apache.sling.api.resource.ValueMap,
                                org.slf4j.Logger,
                                org.slf4j.LoggerFactory,
                                com.day.cq.wcm.api.WCMMode,
                                com.day.cq.wcm.api.LanguageManager,
                                com.day.cq.commons.Doctype,
                                com.day.cq.wcm.api.components.DropTarget,
                                com.day.cq.wcm.foundation.Image,
                                com.adobe.granite.ui.components.ComponentHelper,
                                com.day.cq.i18n.I18n,
                                com.day.cq.wcm.api.WCMMode,
                                com.day.cq.wcm.foundation.Placeholder,
                                org.apache.sling.tenant.Tenant,
                                org.apache.sling.tenant.TenantProvider,
                                org.apache.sling.xss.XSSAPI,
                                java.util.Arrays,
                                java.util.Iterator,
                                java.util.List,
                                java.util.regex.Matcher,
                                java.util.regex.Pattern" %>

<%
    org.apache.sling.api.scripting.SlingBindings _bindings = (org.apache.sling.api.scripting.SlingBindings) pageContext.getAttribute("bindings");
    com.day.cq.wcm.api.components.Component _component = (com.day.cq.wcm.api.components.Component) pageContext.getAttribute("component");
    com.day.cq.wcm.api.components.ComponentContext _componentContext = (com.day.cq.wcm.api.components.ComponentContext) pageContext.getAttribute("componentContext");
    com.day.cq.wcm.api.designer.Design _currentDesign = (com.day.cq.wcm.api.designer.Design) pageContext.getAttribute("currentDesign");
    javax.jcr.Node _currentNode = (javax.jcr.Node) pageContext.getAttribute("currentNode");
    com.day.cq.wcm.api.Page _currentPage = (com.day.cq.wcm.api.Page) pageContext.getAttribute("currentPage");
    com.day.cq.wcm.api.designer.Style _currentStyle = (com.day.cq.wcm.api.designer.Style) pageContext.getAttribute("currentStyle");
    com.day.cq.wcm.api.designer.Designer _designer = (com.day.cq.wcm.api.designer.Designer) pageContext.getAttribute("designer");
    com.day.cq.wcm.api.components.EditContext _editContext = (com.day.cq.wcm.api.components.EditContext) pageContext.getAttribute("editContext");
    org.slf4j.Logger _log = (org.slf4j.Logger) pageContext.getAttribute("log");
    com.day.cq.wcm.api.PageManager _pageManager = (com.day.cq.wcm.api.PageManager) pageContext.getAttribute("pageManager");
    com.day.cq.commons.inherit.InheritanceValueMap _pageProperties = (com.day.cq.commons.inherit.InheritanceValueMap) pageContext.getAttribute("pageProperties");
    org.apache.sling.api.resource.ValueMap _properties = (org.apache.sling.api.resource.ValueMap) pageContext.getAttribute("properties");
    org.apache.sling.api.resource.Resource _resource = (org.apache.sling.api.resource.Resource) pageContext.getAttribute("resource");
    com.day.cq.wcm.api.designer.Design _resourceDesign = (com.day.cq.wcm.api.designer.Design) pageContext.getAttribute("resourceDesign");
    com.day.cq.wcm.api.Page _resourcePage = (com.day.cq.wcm.api.Page) pageContext.getAttribute("resourcePage");
    org.apache.sling.api.resource.ResourceResolver _resourceResolver = (org.apache.sling.api.resource.ResourceResolver) pageContext.getAttribute("resourceResolver");
    org.apache.sling.api.scripting.SlingScriptHelper _sling = (org.apache.sling.api.scripting.SlingScriptHelper) pageContext.getAttribute("sling");
    org.apache.sling.api.SlingHttpServletRequest _slingRequest = (org.apache.sling.api.SlingHttpServletRequest) pageContext.getAttribute("slingRequest");
    org.apache.sling.api.SlingHttpServletResponse _slingResponse = (org.apache.sling.api.SlingHttpServletResponse) pageContext.getAttribute("slingResponse");
    org.apache.sling.xss.XSSAPI _xssAPI = _sling.getService(XSSAPI.class).getRequestSpecificAPI(_slingRequest);
    com.day.cq.tagging.TagManager _tagManager = _resourceResolver.adaptTo(com.day.cq.tagging.TagManager.class);
    com.day.cq.commons.Externalizer _externalizer = _sling.getService(com.day.cq.commons.Externalizer.class);
    com.day.cq.i18n.I18n _i18n = new I18n(_slingRequest);
    com.day.cq.wcm.api.LanguageManager _languageManager = _sling.getService(com.day.cq.wcm.api.LanguageManager.class);

    final ComponentHelper cmp = new ComponentHelper(pageContext);
%>