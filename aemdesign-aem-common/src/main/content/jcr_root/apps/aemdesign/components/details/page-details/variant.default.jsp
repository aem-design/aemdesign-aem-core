<div class="content visible">
    <c:if test="${componentProperties.showBreadcrumb}">
        <cq:include path="breadcrumb" resourceType="aemdesign/components/layout/breadcrumb"/>
    </c:if>


    <c:if test="${componentProperties.showToolbar}">
       <cq:include path="toolbar" resourceType="aemdesign/components/layout/navbar"/>
     </c:if>
</div>

<div class="content">
    <div ${componentProperties.componentAttributes}>
        <header class="page_header">
            <c:if test="${not componentProperties.hideSiteTitle}">
                <div class="hgroup ${componentProperties.cssClassRow}">
                    <h1>${componentProperties.titleFormatted}</h1>
                </div>
            </c:if>
        </header>
    </div>
</div>
