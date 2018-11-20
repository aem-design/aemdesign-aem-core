<%

    Page componentPage = _pageManager.getContainingPage(_resource.getPath());

    String[] pageMetaProperty = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY, new String[0]);
    String[] pageMetaPropertyContent = componentProperties.get(DETAILS_PAGE_METADATA_PROPERTY_CONTENT, new String[0]);
    Map<String, String> metaPropertyFields = new HashMap<String, String>();

    if (pageMetaProperty.length == pageMetaPropertyContent.length) {
        for (int i = 0; i < pageMetaProperty.length; i ++) {
            String key = pageMetaProperty[i];
            String value = pageMetaPropertyContent[i];
            if (isNotEmpty(key) || isNotEmpty(value)) {
                metaPropertyFields.put(key, value);
            }
        }
    }

    if (!metaPropertyFields.containsKey("og:url")) {
        metaPropertyFields.put("og:url", mappedUrl(_resourceResolver, _slingRequest, componentPage.getPath()).concat(DEFAULT_EXTENTION));
    }
    if (!metaPropertyFields.containsKey("og:image")) {
        metaPropertyFields.put("og:image", mappedUrl(_resourceResolver, _slingRequest, getThumbnailUrl(componentPage,_resourceResolver)));
    }
    if (!metaPropertyFields.containsKey("og:title")) {
        metaPropertyFields.put("og:title", getPageTitle(componentPage));
    }
    if (!metaPropertyFields.containsKey("og:description")) {
        metaPropertyFields.put("og:description", getPageDescription(componentPage));
    }

    componentProperties.put("metaPropertyFields",metaPropertyFields);

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:forEach items="${componentProperties.metaPropertyFields}" var="field" varStatus="fieldStatus">
<c:if test="${not empty field.value}">
    <meta property="${field.key}" content="${field.value}" />
</c:if>
</c:forEach>
