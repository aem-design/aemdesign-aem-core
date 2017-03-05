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

  Init script.

  Draws the WCM initialization code. This is usually called by the head.jsp
  of the page. If the WCM is disabled, no output is written.

  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp" %><%
%><%@page session="false" import="com.day.cq.wcm.api.WCMMode" %>

<script type="text/javascript" src="/etc/clientlibs/granite/jquery.js"></script>
<script type="text/javascript" src="/etc/clientlibs/granite/utils.js"></script>

<cq:includeClientLib categories="cq.dam.assetshare, cq.dam.edit, cq.jquery"/>

<!-- editicon class is defined in wcm foundation css but is specific to asseteditor. 
  i dont see any reason to modfy that; defining here instead -->
<style>
  .editicon {
      background:url(../../designs/default/images/dam/asseteditor/edit.png) no-repeat;
      width:16px;
      height:16px;
      cursor:pointer;
      float:right;
  }
</style>