<%@page session="false"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.util.Locale,
    java.util.ResourceBundle,
    com.day.cq.i18n.I18n,
    com.day.cq.search.Predicate,
    design.aem.CustomSearchResult" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="init.jsp" %>
<cq:setContentBundle source="page" />
<%

    final String DEFAULT_ARIA_ROLE = "search";

    final String componentName = _componentContext.getComponent().getName().trim();
    final Locale pageLocale = _currentPage.getLanguage(true);
    final ResourceBundle resourceBundle = _slingRequest.getResourceBundle(pageLocale);

    final List<CustomSearchResult> results = new ArrayList<>();

    // Perform the search
    final Query query = composeQueryBulilder(_slingRequest, _resourceResolver);

    SearchResult result = null;
    String queryText = StringUtils.EMPTY;

    if (query != null) {
        result = query.getResult();

        Predicate fulltextPredicate = query.getPredicates().getByName("fulltext");

        if (fulltextPredicate != null) {
            queryText = fulltextPredicate.get("fulltext", StringUtils.EMPTY);
        }
    }

    final String escapedQuery = _xssAPI.encodeForHTML(queryText);
    final String escapedQueryForAttr = _xssAPI.encodeForHTMLAttr(queryText);
    final String escapedQueryForHref = _xssAPI.getValidHref(queryText);

    pageContext.setAttribute("escapedQuery", escapedQuery);
    pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);
    pageContext.setAttribute("escapedQueryForHref", escapedQueryForHref);

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
            {"printStructure", true},
            {"listTag", "ul"},
            {FIELD_ARIA_ROLE,DEFAULT_ARIA_ROLE, DEFAULT_ARIA_ROLE_ATTRIBUTE},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    componentProperties.put(COMPONENT_ATTRIBUTES,
            addComponentBackgroundToAttributes(componentProperties,_resource, DEFAULT_BACKGROUND_IMAGE_NODE_NAME));

    componentProperties.putAll(
            getAssetInfo(_resourceResolver, getResourceImagePath(_resource, DEFAULT_BACKGROUND_IMAGE_NODE_NAME), FIELD_PAGE_BACKGROUND_IMAGE));

    componentProperties.putAll(processBadgeRequestConfig(componentProperties,_resourceResolver, request), true);

    componentProperties.put(COMPONENT_ATTRIBUTES,
            addComponentAttributes(componentProperties, "data-component-id", componentName));

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

        // Normalise the results tree
        normaliseContentTree(results, _sling, _slingRequest, result);

        // Pagination
        if (result.getResultPages().size() > 0 && result.getNextPage() != null) {
            boolean hasPages = false;

            if (result.getNextPage() != null) {
                hasPages = true;

                // This value is offset by one because the front end code is expecting the index to begin at '0'
                componentProperties.put(COMPONENT_ATTRIBUTES,
                        addComponentAttributes(componentProperties, "data-total-pages", String.valueOf(result.getResultPages().size() - 1)));

                componentProperties.put(COMPONENT_ATTRIBUTES,
                        addComponentAttributes(componentProperties, "data-content-target", "#" + componentProperties.get(FIELD_STYLE_COMPONENT_ID)));

                componentProperties.put(COMPONENT_ATTRIBUTES,
                        addComponentAttributes(componentProperties, "data-page-offset", String.valueOf(result.getHits().size())));

                componentProperties.put(COMPONENT_ATTRIBUTES,
                        addComponentAttributes(componentProperties, "data-showing-text", componentProperties.get("statisticsTextFooter", "")));
            }

            componentProperties.put(COMPONENT_ATTRIBUTES,
                    addComponentAttributes(componentProperties, "data-content-url", request.getPathInfo() + "?q=" + queryText));

            componentProperties.put(COMPONENT_ATTRIBUTES,
                    addComponentAttributes(componentProperties, "data-content-start", "start"));

            componentProperties.put(COMPONENT_ATTRIBUTES,
                    addComponentAttributes(componentProperties, "data-has-pages", String.valueOf(hasPages)));
        }
    }

    pageContext.setAttribute("results", results);

    request.setAttribute(COMPONENT_PROPERTIES, componentProperties);

%>
<c:set var="results" value="${results}"/>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${empty escapedQuery}">
        <%@ include file="searchlist.empty.jsp" %>
    </c:when>
    <c:when test="${empty results}">
        <%@ include file="searchlist.noresults.jsp" %>
    </c:when>
    <c:otherwise>
        <cq:include script="bodyData.jsp" />
        <%@ include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
<%
    request.removeAttribute(COMPONENT_PROPERTIES);

    pageContext.removeAttribute("results");
%>
