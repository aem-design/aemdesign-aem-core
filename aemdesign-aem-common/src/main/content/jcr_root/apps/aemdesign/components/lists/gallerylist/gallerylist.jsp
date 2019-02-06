<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.day.cq.dam.api.collection.SmartCollection" %>
<%@ page import="org.apache.sling.resource.collection.ResourceCollectionManager" %>
<%@ page import="org.apache.sling.resource.collection.ResourceCollection" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="gallerydata.jsp" %>
<%
    Object[][] componentFields = {

            //basic
            {"title", StringUtils.EMPTY},
            {"sortTags", new String[0]},
            {"filterTags", new String[0]},

            //list
            {"listFrom", "children"},
            {"listPath", StringUtils.EMPTY},
            {"listItems", new String[0]},
            {"collection", StringUtils.EMPTY},
            {"assetViewerPagePath", StringUtils.EMPTY},

            //layout
            {"imageHeight", 300},
            {"imageWidth", 480},
            {"galleryGroup", "gallery"},
            {"lightboxHeight",StringUtils.EMPTY},
            {"lightboxWidth",StringUtils.EMPTY},
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"titleAltPrefixText",StringUtils.EMPTY}

    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String listPath = componentProperties.get("listPath", String.class);
    String listFrom = componentProperties.get("listFrom", "children");
    String collection = componentProperties.get("collection",  String.class);
    String assetViewerPagePath = componentProperties.get("assetViewerPagePath",  String.class);
    String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

    Node media = getFirstMediaNode(_currentPage);
    //set display area size to first media node
    if(media != null && !media.getPath().equals(_currentNode.getPath())){
        if(media.hasProperty("lightboxHeight")){
            componentProperties.put("lightboxHeight", media.getProperty("lightboxHeight").getValue().toString());
        }else{
            componentProperties.put("lightboxHeight","");
        }

        if(media.hasProperty("lightboxWidth")){
            componentProperties.put("lightboxWidth", media.getProperty("lightboxWidth").getValue().toString());
        }else{
            componentProperties.put("lightboxWidth","");
        }
    }


    List<Map> listItems = null;

    if ( listFrom.equals("static")  ) {

        listItems = getPicturesFromFixedList(_resourceResolver,componentProperties.get("listItems", new String[0]));

    } else if ( listFrom.equals("children")  ) {
        if(StringUtils.isNotEmpty(listPath))
        {
            Resource listPathR = _resourceResolver.resolve(listPath);
            if (!ResourceUtil.isNonExistingResource(listPathR)) {
                listItems = getPicturesFromResource(_resourceResolver, listPathR);
            }
            componentProperties.put("listPathR",listPathR);
        }

    } else if (listFrom.equals("collection")){

        if(StringUtils.isNotEmpty(collection))
        {
            Resource listPathR = _resourceResolver.resolve(collection);

            if (!ResourceUtil.isNonExistingResource(listPathR) && (listPathR.getResourceType().equals("dam/collection"))) {

                listItems = getPicturesFromCollection(_sling, _resourceResolver, listPathR.getPath(), assetViewerPagePath, variant);
                componentProperties.put("listPathR",listPathR);
            }

        }

    }

    componentProperties.put("prevLinkText",_xssAPI.encodeForHTMLAttr(_i18n.get("prevLinkText", "mediagallery")));
    componentProperties.put("closeText",_xssAPI.encodeForHTMLAttr(_i18n.get("closeText", "mediagallery")));

    componentProperties.put("imageAltText",_xssAPI.encodeForHTMLAttr(_i18n.get("closeText", "mediagallery")));

    componentProperties.put("titleAltPrefixText",_xssAPI.encodeForHTMLAttr(componentProperties.get("titleAltPrefixText", "")));
    componentProperties.put("nextLinkText",_xssAPI.encodeForHTMLAttr(_i18n.get("nextLinkText", "mediagallery")));
    componentProperties.put("listItems",listItems);

%>
<c:set var="componentProperties" value="<%= componentProperties %>" />
<c:choose>
    <c:when test="${componentProperties.variant == 'default' and not empty componentProperties.listItems}">
        <%@ include file="style.default.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'montage.assetviewer' and not empty componentProperties.listItems}">
        <%@ include file="style.montage.assetviewer.jsp" %>
    </c:when>
    <c:when test="${componentProperties.variant == 'montage.lightbox' and not empty componentProperties.listItems}">
        <%@ include file="style.montage.lightbox.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="style.empty.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>