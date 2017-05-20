<%@ page session="false" import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.Page"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%@ page import="java.text.MessageFormat" %>

<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%

    final String I18N_CATEGORY = "location-detail";


    final String DEFAULT_DETAILS_MARKER_ICONPATH = "fontawesome.markers.MAP_MARKER";
    final String DETAILS_MARKER_ICON = "markerIcon";
    final String DETAILS_MARKER_ICONPATH = "menuColor";

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/location-details";

    // init
    Page thisPage = (Page) request.getAttribute("badgePage");

    int zIndex = (Integer) request.getAttribute("zIndex");

    // set title and description
    String displayTitle = getPageTitle(thisPage);


    Object[][] componentFields = {
            {"title",displayTitle},
            {"latitude", 0},
            {"longitude", 0},
            {"menuColor", "default"},
            {"pages", new String[0]}
    };


    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            thisPage,
            componentPath,
            componentFields);

    String[] pageList = componentProperties.get("pages", new String[0]);

     String showAsMarkerIconPath = DEFAULT_DETAILS_MARKER_ICONPATH;

    componentProperties.put("categoryIconPath", showAsMarkerIconPath);
    componentProperties.put("markerPointX", "17");
    componentProperties.put("markerPointY", "0");

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);


        String path = "component-style:components/sitenav/color/" + componentProperties.put("menuColor", String.class);
        Tag tag = _adminTagManager.resolve(path);

        componentProperties.put("menuColorCode", (tag == null ? "": tag.getDescription()));


    } catch (Exception ex) {
        out.write( Throwables.getStackTraceAsString(ex) );
    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }


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


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
{
    type: 'Feature',
    geometry: {
    type: 'Point',
    coordinates: [${componentProperties.longitude}, ${componentProperties.latitude}]
},
properties: {
venue: '<c:out value='${componentProperties.title}'/>',
categoryIconColor: '<c:out value='${componentProperties.menuColorCode}'/>',
categoryIconPath: '<c:out value='${componentProperties.categoryIconPath}'/>',
markerPointX: <c:out value='${componentProperties.markerPointX}'/>,
markerPointY: <c:out value='${componentProperties.markerPointY}'/>,
zIndex: <c:out value='${componentProperties.zIndex}'/><c:if test="${fn:length(componentProperties.eventList) > 0}">,</c:if>
<c:if test="${fn:length(componentProperties.eventList) > 0}">
    events: [
        <c:forEach varStatus="status" var="event" items="${componentProperties.eventList}">
            {<c:forEach varStatus="s" var="e" items="${event}">${e.key}:'<c:out value="${e.value}"/>' <c:if test="${s.last == false}">,</c:if></c:forEach>}<c:if test="${status.last == false}">,</c:if>
        </c:forEach>
    ]
<%--
    image:"http://d3fveiluhe0xc2.cloudfront.net/media/w140/artspavilion-1.jpg",
    title:"M+ Pavilion",
    description:"",
    url:"en/the-district/architecture-facilities/m-4",
    category:""
--%>
</c:if>
}
}