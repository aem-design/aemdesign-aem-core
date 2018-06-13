<%@page session="false"%>
<%@ page import="com.google.common.base.Throwables" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.util.Locale,
                                          java.util.ResourceBundle,
                                          com.day.cq.i18n.I18n,
                                          com.day.cq.tagging.TagManager,
                                          com.day.cq.wcm.foundation.Search" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<cq:setContentBundle source="page" />
<%
    final String DEFAULT_ARIA_ROLE = "search";

    final Locale pageLocale = _currentPage.getLanguage(true);
    final ResourceBundle resourceBundle = _slingRequest.getResourceBundle(pageLocale);

    Search search = new Search(_slingRequest);


    final String escapedQuery = _xssAPI.encodeForHTML(search.getQuery());
    final String escapedQueryForAttr = _xssAPI.encodeForHTMLAttr(search.getQuery());
    final String escapedQueryForHref = _xssAPI.getValidHref(search.getQuery());

    pageContext.setAttribute("escapedQuery", escapedQuery);
    pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);
    pageContext.setAttribute("escapedQueryForHref", escapedQueryForHref);

    pageContext.setAttribute("search", search);

    I18n i18n = new I18n(resourceBundle);

    // {
    //   { name, defaultValue, attributeName, valueTypeClass }
    // }
    Object[][] componentFields = {
            {"searchButtonText", i18n.get("Search", "Search button text")},
            {"searchQueryInformation", i18n.get("Search query information", "Search query information")},
            {"statisticsText", "<div class='content'> <h2>Results {0} for <b>{1}</b></h2></div>"},
            {"noResultsText", i18n.get("Your search - <b>{0}</b> - did not match any documents.", null, escapedQuery)},
            {"spellcheckText", i18n.get("Did you mean:", "Spellcheck text if typo in search term")},
            {"similarPagesText", i18n.get("Similar Pages")},
            {"relatedSearchesText", i18n.get("Related searches:")},
            {"searchTrendsText", i18n.get("Search Trends")},
            {"resultPagesText", i18n.get("Results", "Search results")},
            {"previousText", i18n.get("Previous", "Previous page")},
            {"nextText", i18n.get("Next", "Next page")},
            {"assetActionText", i18n.get("Download")},
            {"pageActionText", i18n.get("Read More")},
            {"otherActionText", i18n.get("Find Out More")},
            {"componentPath",_resource.getResourceType()},
            {"escapedQuery",escapedQuery},
            {"escapedQueryForAttr",escapedQueryForAttr},
            {"escapedQueryForHref",escapedQueryForHref},
            {"searchIn",""},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
            {FIELD_VARIANT, DEFAULT_VARIANT},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put(COMPONENT_ATTRIBUTES, addComponentBackgroundToAttributes(componentProperties,_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_BACKGROUND));

    Search.Result result = null;
    try {
        result = search.getResult();

        if (result != null) {
            if (!isEmpty(componentProperties.get("statisticsText", ""))) {
                componentProperties.put("statisticsText",
                        i18n.get(
                                componentProperties.get("statisticsText", ""),
                                componentProperties.get("searchQueryInformation", ""),
                                result.getTotalMatches(),
                                escapedQuery)
                );
            }
        }

    } catch (RepositoryException ex) {
        _log.error("Unable to get search results", ex);
    }
    pageContext.setAttribute("result", result);


    String searchIn = componentProperties.get("searchIn","");
    String requestSearchPath = request.getParameter("path");
    if (!isEmpty(searchIn)) {
        // only allow the "path" request parameter to be used if it
        // is within the searchIn path configured
        if (requestSearchPath != null && requestSearchPath.startsWith(searchIn)) {
            search.setSearchIn(requestSearchPath);
        } else {
            search.setSearchIn(searchIn);
        }
    } else if (requestSearchPath != null) {
        search.setSearchIn(requestSearchPath);
    }

%>
<c:set var="trends" value="${search.trends}"/>
<c:set var="result" value="${result}"/>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${empty result && empty escapedQuery}">
        <%@ include file="searchlist.empty.jsp" %>
    </c:when>
    <c:when test="${empty result.hits}">
        <%@ include file="searchlist.noresults.jsp" %>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${componentProperties.variant eq 'cards' }">
                <%@ include file="variant.cards.jsp" %>
            </c:when>
            <c:otherwise>
                <%@ include file="variant.default.jsp" %>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
