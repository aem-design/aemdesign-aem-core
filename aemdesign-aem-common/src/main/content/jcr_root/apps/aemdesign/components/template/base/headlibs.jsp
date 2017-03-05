<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<!-- cloud config -->
<cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>

<!-- common js and css -->
<cq:includeClientLib categories="aemdesign.common.head"/>

<!-- components js -->
<cq:includeClientLib js="aemdesign.common.components"/>


<% if (WCMMode.DISABLED != CURRENT_WCMMODE) { %>
<!-- aemdesign.author js and css -->
<cq:includeClientLib categories="aemdesign.author"/>
<!-- aemdesign.common.components.author js and css-->
<cq:includeClientLib categories="aemdesign.common.components.author"/>
<% } %>



<% if (WCMMode.DISABLED == CURRENT_WCMMODE) { %>
<!-- aemdesign.publish css -->
<cq:includeClientLib css="aemdesign.publish"/>
<!-- aemdesign.common.components.publish css -->
<cq:includeClientLib categories="aemdesign.common.components.publish"/>
<% } %>
