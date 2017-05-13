<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/i18n.jsp" %>
<%@page session="false"
        contentType="text/html; charset=utf-8"
        import="com.day.cq.wcm.foundation.ELEvaluator" %>
<%@ page import="com.google.common.base.Throwables" %>

<%
    //check if this page redirects and stop if it is
    if ( checkRedirect(_properties,_currentPage,_slingRequest, response, request, pageContext) ) {
        //done
        return;
    }

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
            {"jcr:description", ""},
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);
    componentProperties.putAll(getComponentStyleProperties(pageContext));

    componentProperties.put("componentAttributes", compileComponentAttributesAsAdmin(componentProperties,_component,_sling));
    componentProperties.put("faviconsPath", "/etc/clientlibs/aemdesign/icons/favicon");

    //TODO: Add theme support or use OOTB theme
    //TODO: move Favicon config toto cloud config

    // get home page - to get clientlib for sites
    Page homePage = _currentPage.getAbsoluteParent(DEPTH_HOMEPAGE);
    if (homePage == null){
        homePage = _currentPage;
    }

    String currentPageTitle = getPageTitleBasedOnDepth(_currentPage);

    Page rootPage = _currentPage.getAbsoluteParent(DEPTH_ROOTNODE);

    String rootTitle = "";
    if (rootPage != null) {
        rootTitle = getPageTitleBasedOnDepth(rootPage);
        if (!"".equals(rootTitle)) {
            rootTitle = " - " + rootTitle;
        }
    }

    String[] tags = _properties.get(TagConstants.PN_TAGS,new String[0]);

    componentProperties.put("keywords", getTagsAsKeywords(_tagManager,",",tags,_slingRequest.getLocale()));
    componentProperties.put("pageTitle", currentPageTitle.concat(rootTitle));
    componentProperties.put("description", _properties.get("description", ""));
    componentProperties.put("canonicalUrl", mappedUrl(_resourceResolver, request.getPathInfo()));

    // set doctype
    if (_currentDesign != null) {
        _currentDesign.getDoctype(_currentStyle).toRequest(request);
    }

    //this allow manually overriding language codes if needed
    componentProperties.put("language", getPageLanguage(_sling,_currentPage));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<!DOCTYPE html>
<html lang="${componentProperties.language}">
<cq:include script="head.jsp"/>
<cq:include script="body.jsp"/>
<%--<cq:include path="timing" resourceType="foundation/components/timing"/>--%>
</html>