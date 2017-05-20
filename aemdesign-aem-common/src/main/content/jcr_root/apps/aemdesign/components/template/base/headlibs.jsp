<%@ page session="false" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<!--contexthub: start-->
<c:if test="${INCLUDE_PAGE_CONTEXTHUB}">
    <sling:include path="contexthub" resourceType="granite/contexthub/components/contexthub" />
</c:if>
<!--contexthub: end-->

<!--cloud config: start-->
<c:if test="${INCLUDE_PAGE_CLOUDSERVICES}">
<cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
</c:if>
<!--cloud config: start-->

<!-- common js and css -->
<cq:includeClientLib categories="aemdesign.common.head"/>

<!-- components js -->
<cq:includeClientLib js="aemdesign.common.components"/>

<c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
    <!-- aemdesign.author js and css -->
    <cq:includeClientLib categories="aemdesign.author"/>
    <!-- aemdesign.common.components.author js and css-->
    <cq:includeClientLib categories="aemdesign.common.components.author"/>
</c:if>

<c:if test="${CURRENT_WCMMODE eq WCMMODE_DISABLED}">
    <!-- aemdesign.publish css -->
    <cq:includeClientLib css="aemdesign.publish"/>
    <!-- aemdesign.common.components.publish css -->
    <cq:includeClientLib categories="aemdesign.common.components.publish"/>
</c:if>

