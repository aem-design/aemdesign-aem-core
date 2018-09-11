<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  List component sub-script

  Creates a {com.day.cq.wcm.foundation.List} list from the request and sets
  it as a request attribute.

--%>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/components/lists/list/listData.jsp" %>
<%
    // This is here to stop statically defined pages from not appearing when 'Hide in Navigation' is set.
    String listType = _properties.get("listFrom", (String) null);
    boolean allowHidden = com.day.cq.wcm.foundation.List.SOURCE_STATIC.equals(listType);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, allowHidden));
    request.setAttribute("list", list);

    String strItemLimit = _properties.get(com.day.cq.wcm.foundation.List.LIMIT_PROPERTY_NAME, (String) null);
    String strPageItems = _properties.get(com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, (String) null);

    // no limit set, but pagination enabled, set limit to infinite
    if (StringUtils.isBlank(strItemLimit) && !StringUtils.isBlank(strPageItems)) {
        list.setLimit(Integer.MAX_VALUE);
    }

    request.setAttribute("emptyList", false);

%>