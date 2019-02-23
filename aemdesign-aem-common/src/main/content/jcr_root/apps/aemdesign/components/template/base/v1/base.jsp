<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/images.jsp" %>
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
    //   { name, defaultValue, attributeName, valueTypeClass }
    // }
    Object[][] componentFields = {
            {"jcr:description", ""},
    };

    ComponentProperties templateProperties = getComponentProperties(
                pageContext,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_METADATA,
                DEFAULT_FIELDS_PAGE_THEME);

    //templateProperties.put("faviconsPath", "/etc/clientlibs/aemdesign/core/icons/favicon");

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

    templateProperties.put("keywords", getTagsAsKeywords(_tagManager,",",tags,_slingRequest.getLocale()));
    templateProperties.put("keywordsList", getTagsAsAdmin(_sling,tags,_slingRequest.getLocale()));
    templateProperties.put("tags", tags);
    templateProperties.put("pageTitle", currentPageTitle.concat(rootTitle));
    templateProperties.put("description", _properties.get("description", ""));
    templateProperties.put("canonicalUrl", mappedUrl(_resourceResolver, _slingRequest, request.getPathInfo()));
    templateProperties.put("imageUrl", mappedUrl(_resourceResolver, _slingRequest, getThumbnailUrl(_currentPage,_resourceResolver)));
    templateProperties.put("imageUrlSecure", mappedUrl(_resourceResolver, _slingRequest, getThumbnailUrl(_currentPage,_resourceResolver), true));



    // set doctype
    if (_currentDesign != null) {
        _currentDesign.getDoctype(_currentStyle).toRequest(request);
    }

    //this allow manually overriding language codes if needed
    templateProperties.put("language", getPageLanguage(_sling,_currentPage));

    request.setAttribute("templateProperties",templateProperties);
%>
<c:set var="templateProperties" value="<%= templateProperties %>"/>
<!DOCTYPE html>
<html lang="${templateProperties.language}">
<cq:include script="head.jsp"/>
<cq:include script="body.jsp"/>
<c:if test="${INCLUDE_PAGE_TIMING}">
    <cq:include path="timing" resourceType="aemdesign/components/common/timing"/>
</c:if>
</html>
