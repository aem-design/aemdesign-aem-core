<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Atom feed renderer for list nodes

  Draws the current list as a feed, listing its items as feed entries.

--%><%@ page session="false" %><%
%><%@ page import="com.adobe.granite.license.ProductInfo,
                   com.adobe.granite.license.ProductInfoService,
                   com.day.cq.commons.Externalizer,
                   com.day.cq.wcm.api.Page,
                   com.day.cq.wcm.api.WCMMode,
                   java.util.Iterator,
                   com.day.cq.wcm.api.components.ComponentContext" %><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%
%><cq:defineObjects /><%

    try {
        WCMMode.DISABLED.toRequest(request);
        request.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);

        Externalizer externalizer = sling.getService(Externalizer.class);
        String url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentPage.getPath());

        String link = url + ".feed";
        String title = currentPage.getTitle() != null ?
                currentPage.getTitle() : currentNode.getName();
        String subTitle = currentPage.getDescription() != null ?
                currentPage.getDescription() : (String)properties.get(JcrConstants.JCR_DESCRIPTION, null);
        %><atom:feed id="<%= url %>"><%
            %><atom:title><c:out value="<%= title %>"/></atom:title><%
            if (subTitle != null) {
                %><atom:subtitle><c:out value="<%= subTitle %>"/></atom:subtitle><%
            }
        %><atom:link href="<%= link %>" rel="self"/><%

        final ProductInfo[] infos = sling.getService(ProductInfoService.class).getInfos();
        final ProductInfo pi = null != infos && infos.length > 0 ? infos[0] : null;
        if (null != pi) {
            String genUri = pi.getUrl();
            String genName = pi.getName();
            String genVersion = pi.getShortVersion();
            %><atom:generator uri="<%= genUri %>" version="<%= genVersion%>"><%= genName %></atom:generator><%
        }
       //initialize the query
       %>
        <cq:include script="init.jsp"/>

       <%

       com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List)request.getAttribute("list");

 		if (!list.isEmpty()) {

            Iterator<Page> i = list.getPages();           

            while (i.hasNext()) {
                final Page p = i.next();
                String path = p.getPath() + ".feedentry";
                      %><sling:include path="<%= path %>"/><%            

            }
        }    

        %></atom:feed><%

    } catch (Exception e) {
        log.error("error rendering feed for list", e);
    }
%>