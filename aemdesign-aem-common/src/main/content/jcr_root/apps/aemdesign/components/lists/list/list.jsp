<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="javax.jcr.query.Query"%>
<%@ page import="javax.jcr.query.QueryResult" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.GregorianCalendar" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>
<%@ include file="init.jsp"  %>
<%@ include file="listData.jsp" %>
<%

    final Boolean DEFAULT_PRINT_STRUCTURE = true;
    final String DEFAULT_TITLE_TYPE = "h2";
    final String DEFAULT_I18N_CATEGORY = "list";
    final String DEFAULT_BADGE = "default";

    //no lambada is available so this is the best that can be done
    Object[][] componentFields = {
        {"feedEnabled", false},
        {"feedType", "rss"},
        {"listSplit", false},
        {"listSplitEvery", 5, "data-list-split-every"},
        {"tags", new String[]{},"data-search-tags", Tag.class.getCanonicalName()},
        {"orderBy", ""},
        {"detailsBadge", DEFAULT_BADGE},
        {"printStructure", DEFAULT_PRINT_STRUCTURE},
        {com.day.cq.wcm.foundation.List.LIMIT_PROPERTY_NAME, ""},
        {com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, ""},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    if (list != null) {
        componentProperties.put("isOrdered", list.isOrdered());
        componentProperties.put("isEmpty", list.isEmpty());
        componentProperties.put("isPaginating", list.isPaginating());

        componentProperties.put("listTag", list.isOrdered() ? "ol" : "ul");

        if (list.isPaginating()) {
            //copied from com.day.cq.wcm.foundation.List.generateId
            String path = _resource.getPath();
            String rootMarker = JcrConstants.JCR_CONTENT.concat("/");
            int root = path.indexOf(rootMarker);
            if (root >= 0) {
                path = path.substring(root + rootMarker.length());
            }
            String start_param = path.replace('/', '_').concat("_start");

            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, "data-has-pages", String.valueOf(list.isPaginating())));

            int totalPages = 0;
            int itemsPerPage = list.getPageMaximum();

            // When the maximum number of pages is greater than zero, calculate the total number by checking
            // the modulus of each item compared to the total known number of items that can be shown per page.
            if (itemsPerPage > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0 && i % itemsPerPage == 0) {
                        totalPages++;
                    }
                }
            }

            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, "data-total-pages", String.valueOf(totalPages)));

            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, "data-content-url", _resource.getPath().concat(DEFAULT_EXTENTION)));

            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, "data-content-start", start_param));
        } else {
            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties, "data-has-pages", String.valueOf(false)));
        }

    } else {
        componentProperties.put("isEmpty", true);
    }

    if ((Boolean)componentProperties.get("feedEnabled")) {
        if ("atom".equals(componentProperties.get("feedType"))) {
            componentProperties.put("feedExt", ".feed");
            componentProperties.put("feedTitle", "Atom 1.0 (List)");
            componentProperties.put("feedType", "application/atom+xml");
        } else {
            componentProperties.put("feedExt", ".rss");
            componentProperties.put("feedTitle", "RSS Feed");
            componentProperties.put("feedType", "application/rss+xml");
        }
        if (isNotEmpty(componentProperties.get("feedExt",""))) {
            componentProperties.put("feedUrl", _resource.getPath().concat(componentProperties.get("feedExt","")));
        } else {
            componentProperties.put("feedUrl",_resource.getPath());
        }
    }

    //prepare request parms to pass to badges
    ComponentProperties badgeRequestAttributes = getComponentProperties(
            pageContext,
            DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE);

    badgeRequestAttributes.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource, DETAILS_THUMBNAIL),
            DETAILS_THUMBNAIL));

    request.setAttribute(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);


    request.setAttribute(COMPONENT_PROPERTIES, componentProperties);

    String strItemLimit = componentProperties.get(com.day.cq.wcm.foundation.List.LIMIT_PROPERTY_NAME, "");
    String strPageItems = componentProperties.get(com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, "");

    // no limit set, but pagination enabled, set limit to infinite
    if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
        list.setLimit(Integer.MAX_VALUE);
    }


%>

<%--LIST START--%>
<c:catch var="exception">
    <cq:include script="list-start.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List start error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST FEED LINK--%>
<c:catch var="exception">
    <cq:include script="list-feed.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List feed error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST CONTENT START--%>
<c:catch var="exception">
    <cq:include script="list-content-start.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List content start error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST BODY CONFIG--%>
<c:catch var="exception">
    <cq:include script="bodyData.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List body data error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST BODY--%>
<c:catch var="exception">
    <cq:include script="body.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List body error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST CONTENT END--%>
<c:catch var="exception">
    <cq:include script="list-content-end.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List content end error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%--LIST PAGINATION--%>
<c:catch var="exception">
    <cq:include script="pagination.jsp" />
</c:catch>
<c:if test="${not empty exception}">
    <div class="cq-error">List pagination error<br/>${exception.message}<br/>${exception.stackTrace}</div>
</c:if>

<%--LIST END--%>
<c:catch var="exception">
    <cq:include script="list-end.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <div class="cq-error">List end error<br>${exception.message}<br>${exception.stackTrace}</div>
</c:if>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>