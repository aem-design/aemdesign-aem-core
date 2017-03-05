<%@ page %>
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
%><body id="top" class="<%= cls %>">
<%--<cq:include path="siteinclude" resourceType="aemdesign/components/admin/siteinclude"/>--%>
<%--<cq:include path="siteheader" resourceType="aemdesign/components/admin/siteheader"/>--%>
<%--<cq:include path="clientcontext" resourceType="cq/personalization/components/clientcontext"/>--%>
<%
    if (isNewEdit) { //this so that authoring works
%>

<%
    }
%>
<cq:include script="pageheader.jsp"/>
<div id="content" role="main">
    <cq:include script="contentheader.jsp"/>
    <cq:include script="content.jsp"/>
    <cq:include script="footer.jsp"/>
</div>

<%--<cq:include path="sitefooter" resourceType="aemdesign/components/admin/sitefooter"/>--%>
<cq:includeClientLib js="aemdesign.common.body"/>

<cq:include path="cloudservices" resourceType="cq/cloudserviceconfigs/components/servicecomponents"/>
</body>