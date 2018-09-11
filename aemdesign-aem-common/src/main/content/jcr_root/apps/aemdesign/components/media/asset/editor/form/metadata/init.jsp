
<%@page import="com.day.cq.wcm.foundation.forms.FieldDescription"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Text Field Component for Asset Editor

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="com.day.cq.wcm.foundation.forms.FieldDescription,
                   com.day.cq.wcm.foundation.forms.FieldHelper" %><%

    final String namespace = properties.get("meta/namespace", "");
    final String localPart = properties.get("meta/localPart", "");
    final String name = "./jcr:content/metadata/" +  namespace + ":" + localPart;
    final FieldDescription desc = FieldHelper.createDefaultDescription(slingRequest, resource);
    desc.setName(name);
    desc.setMultiValue(properties.get("meta/multivalue", false));
%>
