<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page session="false"%><%@ page import="java.util.Locale,
                                          java.util.ResourceBundle,
                                          com.day.cq.i18n.I18n,
                                          com.day.cq.tagging.TagManager,
                                          com.day.cq.wcm.foundation.Search" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<cq:setContentBundle source="page" /><%
    Search search = new Search(slingRequest);


    final Locale pageLocale = currentPage.getLanguage(true);
    final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);

    String searchIn = (String) properties.get("searchIn");
    String requestSearchPath = request.getParameter("path");
    if (searchIn != null) {
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

    search.setSearchProperties("jcr:title,jcr:description");

    final String escapedQuery = xssAPI.encodeForHTML(search.getQuery());
    final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(search.getQuery());
    final String escapedQueryForHref = xssAPI.getValidHref(search.getQuery());

    pageContext.setAttribute("escapedQuery", escapedQuery);
    pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);
    pageContext.setAttribute("escapedQueryForHref", escapedQueryForHref);

    pageContext.setAttribute("search", search);
    TagManager tm = resourceResolver.adaptTo(TagManager.class);

    Search.Result result = null;
    try {
        result = search.getResult();
    } catch (RepositoryException e) {
        log.error("Unable to get search results", e);
    }
    pageContext.setAttribute("result", result);

    String nextText = properties.get("nextText", i18n.get("Next", "Next page"));
    String noResultsText = properties.get("noResultsText", i18n.get("Your search - <b>{0}</b> - did not match any documents.", null, escapedQuery));
    String previousText = properties.get("previousText", i18n.get("Previous", "Previous page"));
    String relatedSearchesText = properties.get("relatedSearchesText", i18n.get("Related searches:"));
    String resultPagesText = properties.get("resultPagesText", i18n.get("Results", "Search results"));
    String searchButtonText = properties.get("searchButtonText", i18n.get("Search", "Search button text"));
    String searchTrendsText = properties.get("searchTrendsText", i18n.get("Search Trends"));
    String similarPagesText = properties.get("similarPagesText", i18n.get("Similar Pages"));
    String spellcheckText = properties.get("spellcheckText", i18n.get("Did you mean:", "Spellcheck text if typo in search term"));

%><c:set var="trends" value="${search.trends}"/><c:set var="result" value="${result}"/>


<c:choose>
    <c:when test="${empty result && empty escapedQuery}">
    </c:when>
    <c:when test="${empty result.hits}">
        ${result.trackerScript}
        <c:if test="${result.spellcheck != null}">
            <p><%= xssAPI.encodeForHTML(spellcheckText) %> <a href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${result.spellcheck}"/></c:url>"><b><c:out value="${result.spellcheck}"/></b></a></p>
        </c:if>
        <%= xssAPI.filterHTML(noResultsText) %>
        <span data-tracking="{event:'noresults', values:{'keyword': '<c:out value="${escapedQuery}"/>', 'results':'zero', 'executionTime':'${result.executionTime}'}, componentPath:'<%=resource.getResourceType()%>'}"></span>
    </c:when>
    <c:otherwise>
        <span data-tracking="{event:'search', values:{'keyword': '<c:out value="${escapedQuery}"/>', 'results':'${result.totalMatches}', 'executionTime':'${result.executionTime}'}, componentPath:'<%=resource.getResourceType()%>'}"></span>
        ${result.trackerScript}

        <div class="search-results-total">
            <%= xssAPI.filterHTML(properties.get("statisticsText", i18n.get("<div class='content'> <h2>Results {0} for <b>{1}</b></h2></div>", "Search query information", result.getTotalMatches(), escapedQuery))) %>
        </div>

        <div class="search-results-main content">

            <div class="searchRight">
                <c:if test="${fn:length(trends.queries) > 0}">
                    <p><%= xssAPI.encodeForHTML(searchTrendsText) %></p>
                    <div class="searchTrends">
                        <ul>
                            <c:forEach var="query" items="${trends.queries}">
                                <li>
                                    <a href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${query.query}"/></c:url>"><span style="font-size:${query.size}px"><c:out value="${query.query}"/></span></a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <c:if test="${result.facets.tags.containsHit}">
                    <p><%=i18n.get("Tags")%></p>
                    <ul>
                        <c:forEach var="bucket" items="${result.facets.tags.buckets}">
                            <c:set var="bucketValue" value="${bucket.value}"/>
                            <c:set var="tag" value="<%= tm.resolve((String) pageContext.getAttribute("bucketValue")) %>"/>
                            <li>
                                <c:if test="${tag != null}">
                                    <c:set var="label" value="${tag.title}"/>
                                    <c:choose>
                                        <c:when test="<%= request.getParameter("tag") != null && java.util.Arrays.asList(request.getParameterValues("tag")).contains(pageContext.getAttribute("bucketValue")) %>">
                                            ${label} (${bucket.count}) - <a title="filter results" href="<c:url value="${currentPage.path}.html"><c:param name="q" value="${escapedQueryForHref}"/></c:url>">remove filter</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a title="filter results" href="
                                <c:url value="${currentPage.path}.html">
                                    <c:param name="q" value="${escapedQueryForHref}"/>
                                    <c:param name="tag" value="${bucket.value}"/>
                                </c:url>">
                                                    ${label} (${bucket.count})
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </div>

            <div class="searchLeft">
                <c:if test="${fn:length(search.relatedQueries) > 0}">
                    <%= xssAPI.encodeForHTML(relatedSearchesText) %>
                    <c:forEach var="rq" items="${search.relatedQueries}">
                        <a style="margin-right:10px" href="${currentPage.path}.html?q=${rq}"><c:out value="${rq}"/></a>
                    </c:forEach>
                </c:if>
                <c:forEach var="hit" items="${result.hits}" varStatus="status">
                    <c:if test="${hit.extension != \"\" && hit.extension != \"html\"}">
                        <span class="icon type_${hit.extension}"><img src="/etc/designs/admin/0.gif" alt="*"></span>
                    </c:if>
                    <div class="result-item">
                        <div class="result-item-title">
                            <a href="${hit.URL}" onclick="trackSelectedResult(this, ${status.index + 1})">${hit.title}</a>
                        </div>
                        <div class="result-item-excerpt-link">
                            <a href="${hit.URL}">${hit.URL}</a>
                        </div>

                        <div class="result-item-excerpt">${hit.excerpt}</div>

                    </div>
                </c:forEach>


                <c:if test="${fn:length(result.resultPages) > 1}">
                    <div class="search-result-pagination">
                        <b><%= xssAPI.encodeForHTML(resultPagesText) %></b>

                        <div class="search-result-pagination-list">
                            <ul>
                                <c:if test="${result.previousPage != null}">
                                    <li> <a href="${result.previousPage.URL}"><%= xssAPI.encodeForHTML(previousText) %></a> </li>
                                </c:if>
                                <c:forEach var="page" items="${result.resultPages}">
                                    <li>
                                        <c:choose>
                                            <c:when test="${page.currentPage}">${page.index + 1}</c:when>
                                            <c:otherwise>
                                                <a href="${page.URL}">${page.index + 1}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                                <c:if test="${result.nextPage != null}">
                                    <li><a href="${result.nextPage.URL}"><%= xssAPI.encodeForHTML(nextText) %></a></li>
                                </c:if>
                            </ul>
                        </div>

                    </div>
                </c:if>
            </div>

        </div>
    </c:otherwise>
</c:choose>
