<%@ page session="false" import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/component-details.jsp" %>

<%

    final String I18N_CATEGORY = "location-detail";
    final String DEFAULT_DETAILS_MARKER_ICONPATH = "fontawesome.markers.MAP_MARKER";
    final String DETAILS_MARKER_ICON = "markerIcon";
    final String DETAILS_MARKER_ICONPATH = "menuColor";

    Object[][] componentFields = {
            {"title", getPageTitle(_currentPage)},
            {"latitude", 0.0},
            {"longitude", 0.0},
            {"pages", new String[0]},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_PAGE_URL, getPageUrl(_currentPage)},
            {FIELD_PAGE_TITLE_NAV, getPageNavTitle(_currentPage)},
            {"menuColor", "default"},
    };


    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_DETAILS_OPTIONS);

    int zIndex = (Integer) request.getAttribute("zIndex");

    String[] pageList = componentProperties.get("pages", new String[0]);

    String showAsMarkerIconPath = DEFAULT_DETAILS_MARKER_ICONPATH;

    componentProperties.put("categoryIconPath", showAsMarkerIconPath);
    componentProperties.put("markerPointX", "17");
    componentProperties.put("markerPointY", "0");

    String tagPath = "component-style:theme/lists/navlist/color/" + componentProperties.get("menuColor", String.class);
    componentProperties.put("menuColorCode", getTagValueAsAdmin(tagPath,_sling));

    List<Map<String, String>> eventList = new ArrayList<Map<String, String>>();

    for (String targetPage : pageList){
        Page p = _pageManager.getPage(targetPage);
        if (p != null){
            Map<String, String> eventMap = new HashMap<String, String>();
            eventMap.put("url", mappedUrl(_resourceResolver, getPageUrl(p)));
            eventMap.put("title", StringEscapeUtils.escapeEcmaScript(getPageDetailsField(p, "title")));
            eventMap.put("description",StringEscapeUtils.escapeEcmaScript(getPageDetailsField(p,"description")));
            String img = this.getPageImgReferencePath(p);
            img = getThumbnail(img, SMALL_IMAGE_PATH_SELECTOR, _resourceResolver);
            eventMap.put("image",img);
            String category = StringUtils.EMPTY;
            if (p.getContentResource().adaptTo(Node.class) != null){
                String[] categoryMapTags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

                LinkedHashMap<String, Map> categoryMap = getTagsAsAdmin(_sling, categoryMapTags, _slingRequest.getLocale());

                if (categoryMap != null && categoryMap.size() > 0){
                    category = StringUtils.join(categoryMap.keySet(), ",");
                }
            }
            eventMap.put("category",StringEscapeUtils.escapeEcmaScript(category));
            eventList.add(eventMap);
        }
    }

    componentProperties.put("eventList", eventList);

    componentProperties.put("zIndex",zIndex);

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getPageImgReferencePath(_currentPage),
            FIELD_PAGE_IMAGE));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_SECONDARY_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_SECONDARY));

    componentProperties.putAll(getAssetInfo(_resourceResolver,
            getResourceImagePath(_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME),
            FIELD_PAGE_IMAGE_BACKGROUND));

    componentProperties.putAll(getBadgeRequestConfig(componentProperties,_resourceResolver, request));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${COMPONENT_BADGE eq 'badge.card'}">
        <%@ include file="badge.card.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.cardIcon'}">
        <%@ include file="badge.cardIcon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.icon'}">
        <%@ include file="badge.icon.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge.image'}">
        <%@ include file="badge.image.jsp" %>
    </c:when>

    <c:when test="${COMPONENT_BADGE eq 'badge' or COMPONENT_BADGE eq 'badge.default'}">
        <%@ include file="badge.default.jsp" %>
    </c:when>

    <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
        <%@include file="variant.default.jsp" %>
    </c:when>

    <c:otherwise>
        <%@include file="variant.default.jsp" %>
    </c:otherwise>
</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
