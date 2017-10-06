<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="javax.jcr.query.Query"%>
<%@ page import="javax.jcr.query.QueryResult" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.GregorianCalendar" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
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
        {"listItemShowLink", false},
        {"listItemLinkTarget", "_blank"},
        {"listItemLinkText", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TEXT,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemLinkTitle", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TITLE,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemTitleTrim", false /*trim title up to a max length and add suffix*/},
        {"listItemTitleLengthMax", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemTitleLengthMaxSuffix", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX_SUFFIX,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemSummaryTrim", false /*trim summary up to a max length and add suffix*/},
        {"listItemSummaryLengthMax", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemSummaryLengthMaxSuffix", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX_SUFFIX,DEFAULT_I18N_CATEGORY,_i18n)},
        {"listItemShowOverlayIcon", false},
        {"listItemShowRedirectIcon", false},
        {"printStructure", DEFAULT_PRINT_STRUCTURE},
        {BADGE_TITLE_TAG_TYPE, "h3"},
        {BADGE_THUMBNAIL_TYPE, "rendition"},
        {BADGE_THUMBNAIL_WIDTH, 319},
        {BADGE_THUMBNAIL_HEIGHT, ""},
        {FIELD_TITLE_TAG_TYPE,DEFAULT_TITLE_TYPE},
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
    Map<String, Object> badgeRequestAttributes = new HashMap<>();
    badgeRequestAttributes.put(BADGE_THUMBNAIL_DEFAULT,DEFAULT_IMAGE_BLANK);
    if (_resourceResolver.resolve(DEFAULT_BADGETHUMBNAIL_IMAGE_NODE_NAME) != null) {
        badgeRequestAttributes.putAll(getAssetInfo(_resourceResolver,
                getResourceImagePath(_resource, DEFAULT_BADGETHUMBNAIL_IMAGE_NODE_NAME),
                BADGE_THUMBNAIL_DEFAULT));
    }
    badgeRequestAttributes.put(BADGE_THUMBNAIL_WIDTH,componentProperties.get(BADGE_THUMBNAIL_WIDTH));
    badgeRequestAttributes.put(BADGE_THUMBNAIL_HEIGHT,componentProperties.get(BADGE_THUMBNAIL_HEIGHT));
    badgeRequestAttributes.put(BADGE_THUMBNAIL_TYPE,componentProperties.get(BADGE_THUMBNAIL_TYPE));
    badgeRequestAttributes.put(BADGE_TITLE_TAG_TYPE,componentProperties.get(BADGE_TITLE_TAG_TYPE));

    request.setAttribute("badgeRequestAttributes", badgeRequestAttributes);


    request.setAttribute("componentProperties", componentProperties);

%>

<!--componentProperties: ${componentProperties} -->
<!--badgeRequestAttributes: ${badgeRequestAttributes} -->

<%--LIST START--%>
<c:if test="${componentProperties.printStructure}">
<div ${componentProperties.componentAttributes}>
</c:if>

<%--LIST FEED LINK--%>
<c:if test="${componentProperties.feedEnabled}" >
    <link rel="alternate" type="${componentProperties.feedType}" title="${componentProperties.feedTitle}" href="${componentProperties.feedUrl}" />
</c:if>

<%--LIST CONTENT--%>
<c:if test="${componentProperties.printStructure}">
<div class="content">
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

<c:if test="${componentProperties.printStructure}">
</div>
</c:if>

<%--LIST PAGINATION--%>
<c:if test="${componentProperties.isPaginating}">

    <c:catch var="exception">
        <cq:include script="pagination.jsp" />
    </c:catch>
    <c:if test="${not empty exception}">
        <p class="cq-error">List pagination error<br/>${exception.message}<br/>${exception.stackTrace}</p>
    </c:if>

</c:if>

<%--LIST END--%>
<c:if test="${componentProperties.printStructure}">
</div>
</c:if>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>