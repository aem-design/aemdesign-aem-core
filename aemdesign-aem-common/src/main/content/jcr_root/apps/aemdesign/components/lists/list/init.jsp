<%

    Boolean showHidden = _properties.get("showHidden", false);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, showHidden));

    //for Children list set the current page as the Parent Page if the property is not set
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, com.day.cq.wcm.foundation.List.SOURCE_CHILDREN))) {
        String parentPage = _properties.get(com.day.cq.wcm.foundation.List.PARENT_PAGE_PROPERTY_NAME,"");
        if (isEmpty(parentPage)) {
            list.setStartIn(resource.getPath());
        }
    }

    request.setAttribute("list", list);

    String strItemLimit = _properties.get(com.day.cq.wcm.foundation.List.LIMIT_PROPERTY_NAME, (String) null);
    String strPageItems = _properties.get(com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, (String) null);

    // no limit set, but pagination enabled, set limit to infinite
    if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
        list.setLimit(Integer.MAX_VALUE);
    }

%>