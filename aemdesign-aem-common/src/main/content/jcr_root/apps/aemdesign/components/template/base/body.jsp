<%@page session="false"
        contentType="text/html; charset=utf-8"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<c:set var="templateProperties" value="<%= request.getAttribute("templateProperties") %>"/>

<body>
<!--clientcontext: start-->
<c:if test="${INCLUDE_PAGE_CLIENTCONTEXT}">
    <cq:include path="clientcontext" resourceType="cq/personalization/components/clientcontext"/>
</c:if>
<!--clientcontext: end-->

<!--header: start-->
<cq:include script="header.jsp"/>
<!--header: end-->

<!--page content: start-->
<div${templateProperties.componentAttributes}>
    <!--content: start-->
    <cq:include script="content.jsp"/>
    <!--content: end-->
</div>
<!--page content: end-->

<!--footer: start-->
<cq:include script="footer.jsp"/>
<!--footer: end-->

<!--body js: start-->
<cq:includeClientLib js="aemdesign.common.body"/>
<!--body js: end-->

<!--cloud services: start-->
<c:if test="${INCLUDE_PAGE_CLOUDSERVICES}">
    <cq:include path="cloudservices" resourceType="cq/cloudserviceconfigs/components/servicecomponents"/>
</c:if>
<!--cloud services: end-->
</body>
