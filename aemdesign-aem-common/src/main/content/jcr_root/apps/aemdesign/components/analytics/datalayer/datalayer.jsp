<%@ page import="com.google.gson.Gson" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/datetime.jsp" %>
<%

    String digitalDataJson = "{\"error\":\"could not load data layer\"}";

    try {
        String detailsPath = findComponentInPage(_currentPage, DEFAULT_LIST_DETAILS_SUFFIX);
        Resource details = _resourceResolver.getResource(detailsPath);
        ValueMap detailsProperties = details.adaptTo(ValueMap.class);
        if (detailsProperties == null) {
            detailsProperties = _properties;
        }


        String pageName = _currentPage.getPath().substring(1).replace('/', ':');
        pageName = pageName.replace("content:", "");


        HashMap<String, Object> digitalData = new HashMap<String, Object>();
        HashMap<String, Object> digitalDataPage = new HashMap<String, Object>();
        HashMap<String, Object> digitalDataPagePageInfo = new HashMap<String, Object>();
        ArrayList<String> digitalDataPageEvent = new ArrayList<String>();
        ArrayList<String> digitalDataPageError = new ArrayList<String>();
        HashMap<String, Object> digitalDataPageAttributes = new HashMap<String, Object>();

        digitalDataPagePageInfo.put("pageName", detailsProperties.get("analyticsPageName", pageName));
        digitalDataPagePageInfo.put("pageType", detailsProperties.get("analyticsPageType", ""));
        if (isNotEmpty(_properties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, ""))) {
            digitalDataPagePageInfo.put("effectiveDate", formatDate(_properties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.getInstance()), "YYYY-MM-DD"));
        } else {
            digitalDataPagePageInfo.put("effectiveDate", "");
        }
        digitalDataPagePageInfo.put("contentLanguage", _currentPage.getLanguage(false).getDisplayCountry());
        digitalDataPagePageInfo.put("contentCountry", _currentPage.getLanguage(false).getDisplayLanguage().toLowerCase());

        digitalDataPageAttributes.put("platform", detailsProperties.get("analyticsPlatform", "aem"));
        digitalDataPageAttributes.put("abort", detailsProperties.get("analyticsAbort", "false"));
        digitalDataPageAttributes.put("detailsMissing", isEmpty(detailsPath));

        digitalDataPage.put("pageInfo", digitalDataPagePageInfo);
        digitalDataPage.put("attributes", digitalDataPageAttributes);

        digitalData.put("page", digitalDataPage);
        digitalData.put("event", digitalDataPageEvent);
        digitalData.put("error", digitalDataPageError);

        Gson gson = new Gson();

        digitalDataJson = gson.toJson(digitalData);

    } catch (Exception ex) {
        LOG.error("dattalayer: {}", ex);
    }

%>
<c:set var="digitalDataJson" value="<%= digitalDataJson %>"/>
<cq:includeClientLib categories="aemdesign.components.datalayer"/>
<script type="text/javascript">
    window.digitalData = ${digitalDataJson};
    window.digitalData.page.pageInfo.referringURL = AEMDESIGN.analytics.getReferringUrl();
    window.digitalData.page.pageInfo.destinationUrl = AEMDESIGN.analytics.getDestinationUrl();
    window.digitalData.page.attributes.breakPoint = AEMDESIGN.analytics.getBreakpoint()["label"];
</script>