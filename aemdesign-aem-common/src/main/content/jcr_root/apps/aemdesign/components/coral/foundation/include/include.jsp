<%--
  ADOBE CONFIDENTIAL
  ___________________

  Copyright 2015 Adobe
  All Rights Reserved.

  NOTICE: All information contained herein is, and remains
  the property of Adobe and its suppliers, if any. The intellectual
  and technical concepts contained herein are proprietary to Adobe
  and its suppliers and are protected by all applicable intellectual
  property laws, including trade secret and copyright laws.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe.
--%><%
%><%@ include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false" import="com.adobe.granite.ui.components.Config" pageEncoding="utf-8" %>
<%--###
Include
=======

.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/include

   A component to include other resource.

   It has the following content structure:

   .. gnd:gnd::

      [granite:Include]

      /**
       * The path of the included resource. It accepts any value that Sling ResourceResolver can accept.
       */
      - path (String)

      /**
       * The resource type of the included resource.
       */
      - resourceType (String)

      /**
       * The selector you want to pass onto the request.
       */
      - selector (String)
###--%><%

  Config cfg = cmp.getConfig();

  String path = cfg.get("path", String.class);

  if (path == null) {
    return;
  }

  // Get the resource using resourceResolver so that the search path is applied.
  Resource targetResource = resourceResolver.getResource(path);

  if (targetResource == null) {
    return;
  }

  cmp.include(targetResource, cfg.get("resourceType", String.class), cfg.get("selector", ""), cmp.getOptions());
%>
