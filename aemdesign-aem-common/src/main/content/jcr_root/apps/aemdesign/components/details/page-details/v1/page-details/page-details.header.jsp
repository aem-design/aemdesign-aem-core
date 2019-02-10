<c:if test="${componentProperties.showBreadcrumb}">
    <cq:include path="breadcrumb" resourceType="aemdesign/components/layout/breadcrumb"/>
</c:if>
<c:if test="${componentProperties.showToolbar}">
    <cq:include path="toolbar" resourceType="aemdesign/components/layout/navbar"/>
</c:if>
<c:if test="${componentProperties.showPageDate}">
    <cq:include path="pagedate" resourceType="aemdesign/components/content/pagedate"/>
</c:if>

