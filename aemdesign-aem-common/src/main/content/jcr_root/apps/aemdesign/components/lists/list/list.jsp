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

    } else {
        componentProperties.put("isEmpty", true);
    }

    if ((Boolean)componentProperties.get("feedEnabled")) {
        if ("atom".equals(componentProperties.get("feedEnabled"))) {
            componentProperties.put("feedExt", ".feed");
            componentProperties.put("feedTitle", "Atom 1.0 (List)");
            componentProperties.put("feedType", "application/atom+xml");
        } else {
            componentProperties.put("feedExt", ".rss");
            componentProperties.put("feedTitle", "RSS Feed");
            componentProperties.put("feedType", "application/rss+xml");
        }
        componentProperties.put("feedUrl", componentProperties.get("resourcePath").toString() + componentProperties.get("feedExt").toString());
    }

    //prepare request parms to pass to badges
    ComponentProperties badgeRequestAttributes = getComponentProperties(
            pageContext,
            DEFAULT_FIELDS_DETAILS_OPTIONS_OVERRIDE);

    if (_resourceResolver.resolve(badgeRequestAttributes.get(DETAILS_THUMBNAIL,"")) != null) {
        badgeRequestAttributes.putAll(getAssetInfo(_resourceResolver,
                getResourceImagePath(_resource, DETAILS_THUMBNAIL),
                DETAILS_THUMBNAIL));
    }

    request.setAttribute(BADGE_REQUEST_ATTRIBUTES, badgeRequestAttributes);


    request.setAttribute(COMPONENT_PROPERTIES, componentProperties);

%>

<%--LIST START--%>
<c:catch var="exception">
    <cq:include script="list-start.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List start error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST FEED LINK--%>
<c:catch var="exception">
    <cq:include script="list-feed.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List feed error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST CONTENT START--%>
<c:catch var="exception">
    <cq:include script="list-content-start.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List content start error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST BODY CONFIG--%>
<c:catch var="exception">
    <cq:include script="bodyData.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List body data error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST BODY--%>
<c:catch var="exception">
    <cq:include script="body.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List body error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST CONTENT END--%>
<c:catch var="exception">
    <cq:include script="list-content-end.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List content end error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%--LIST PAGINATION--%>
<c:catch var="exception">
    <cq:include script="pagination.jsp" />
</c:catch>
<c:if test="${not empty exception}">
    <p class="cq-error">List pagination error<br/>${exception.message}<br/>${exception.stackTrace}</p>
</c:if>

<%--LIST END--%>
<c:catch var="exception">
    <cq:include script="list-end.jsp" />
</c:catch>
<c:if test="${ exception != null }">
    <p class="cq-error">List end error<br>${exception.message}<br>${exception.stackTrace}</p>
</c:if>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>