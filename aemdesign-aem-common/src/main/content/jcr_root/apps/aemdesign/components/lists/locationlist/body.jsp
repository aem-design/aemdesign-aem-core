<%@ page import="java.util.Iterator" %>
<%@ page import="com.day.cq.wcm.webservicesupport.ConfigurationManager" %>
<%@ page import="com.day.cq.wcm.webservicesupport.Configuration" %>
<%@ page import="com.adobe.granite.asset.api.AssetManager" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    if ( list == null || list.isEmpty() ) {
%><cq:include script="empty.jsp" /><%
        return;
    }

    //TODO: move this admin session usage into function
    ResourceResolver adminResourceResolver  =  null;
    String googleApiKey = "";
    try {
        adminResourceResolver = this.openAdminResourceResolver(_sling);
        // Getting attached facebook cloud service config in order to fetch appID
        ConfigurationManager cfgMgr = adminResourceResolver.adaptTo(ConfigurationManager.class);
        Configuration googleMapConfiguration = null;
        String[] services = _pageProperties.getInherited("cq:cloudserviceconfigs", new String[]{});

        if (cfgMgr != null) {
            googleMapConfiguration = cfgMgr.getConfiguration("googlemap", services);
            if (googleMapConfiguration != null) {
                googleApiKey = googleMapConfiguration.get("googleApiKey", "");

            }
        }

    } catch (Exception ex) {

        out.write( Throwables.getStackTraceAsString(ex) );

    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }

    Iterator<Page> items = list.getPages();


    //Only support the Drag and Drop Images
    String fileReference = _properties.get("fileReference", StringUtils.EMPTY);

    AssetManager assetManager = _resourceResolver.adaptTo(AssetManager.class);
    com.adobe.granite.asset.api.Asset asset = assetManager.getAsset(fileReference);
    String imageLength = getMetadataStringForKey(asset, "tiff:ImageLength");
    String imageWidth = getMetadataStringForKey(asset, "tiff:ImageWidth");

    Object[][] componentFields = {
            {"googleApiKey", googleApiKey, "mapapikey"},
            {"modules", "map", "modules"},
            {"mapProjectionSystem", "gallPeters", "map"},
            {"fileReference", StringUtils.EMPTY,"mapimagesrc"},
            {"componentInstance", _currentNode.getName(), "maplocationsid"},
            {"imageLength", imageLength, "mapimagey"},
            {"imageWidth", imageWidth, "mapimagex"},
            {"wcmMode", CURRENT_WCMMODE.name().toLowerCase(), "wcmmode"},
            {"topicQueue", StringUtils.EMPTY, "topicqueue"},
            {"variant", DEFAULT_VARIANT}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String badgeSelector = _properties.get("variant", DEFAULT_BADGE);

%>
<c:set var="componentProperties" value="<%= _componentContext.getAttribute("componentProperties") %>"/>
<c:set var="innerComponentProperties" value="<%=componentProperties %>"/>


<div ${componentProperties.componentAttributes} ${innerComponentProperties.dataAttributes}
        <c:if test="${CURRENT_WCMMODE != WCMMODE_DISABLED}">
            style="height: ${innerComponentProperties.imageLength}px; width: ${innerComponentProperties.imageWidth}px"
        </c:if>
        ></div>
<script>
    // GeoJSON, describing the locations and names of some locations.
    var ${innerComponentProperties.componentInstance} = {
        type: 'FeatureCollection',
                features: [
        <%
            //need to retrieve the pages again
            items = list.getPages();

            list.size();

            int index = 0;

            while (items.hasNext()) {

                Page listItem = items.next();

                request.setAttribute("badgePage", listItem);

                request.setAttribute("zIndex", index);


                String badgeBase = getPageBadgeBase(listItem);
                if (badgeBase == null) {
                    badgeBase = PATH_DEFAULT_BADGE_BASE;
                }

                String script = "/apps/aemdesign/components/details/location-details/badge.default.jsp";

                String defDecor =_componentContext.getDefaultDecorationTagName();

                try {
                    disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

        %><cq:include script="<%= script %>" /><%
            if ((index+1) != list.size()){
                out.println(",");
                index ++;
            }

        } catch (Exception ex) {
            if (!"JspException".equals(ex.getClass().getSimpleName())) {
                throw ex;
            }
        %><p class="cq-error">Variation not found for <%=badgeSelector%> content type (<%=script%> not found)</p><%
        }finally {
            enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);
        }

    }
%>

    ]
    };
</script>
