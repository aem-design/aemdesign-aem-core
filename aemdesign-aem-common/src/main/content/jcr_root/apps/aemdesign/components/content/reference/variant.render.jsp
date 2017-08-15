<div ${componentProperties.componentAttributes}>
    <%=resourceRenderAsHtml(
            pageContext.getAttribute("includePath").toString(),
            _resourceResolver,
            _sling,
            WCMMode.DISABLED)%>

    <cq:text property="tableData"
                 escapeXml="false"
                 placeholder="<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-table-placeholder\" alt=\"\" />"
        />
</div>

