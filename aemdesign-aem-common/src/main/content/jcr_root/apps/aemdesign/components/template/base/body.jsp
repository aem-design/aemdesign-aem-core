<%@page session="false"
        contentType="text/html; charset=utf-8"%>
<%@include file="/apps/aemdesign/global/global.jsp" %><%
    StringBuffer cls = new StringBuffer();
    for (String c: componentContext.getCssClassNames()) {
        cls.append(c).append(" ");
    }
    String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
    boolean isNewEdit = false;
    for (String selector : selectors) {
        if (selector.equals("touchedit")) {
            isNewEdit = true;
            break;
        }
    }
%><body ${componentProperties.componentAttributes}>
<%--<cq:include path="clientcontext" resourceType="cq/personalization/components/clientcontext"/>--%>
<div class=".container-fluid">
    <cq:include script="pageheader.jsp"/>
    <div id="row">
    <cq:include script="content.jsp"/>
    </div>
    <cq:include script="footer.jsp"/>
    <!-- common -->
    <cq:includeClientLib js="aemdesign.common.body"/>

    <cq:include path="cloudservices" resourceType="cq/cloudserviceconfigs/components/servicecomponents"/>
</div>
</body>
