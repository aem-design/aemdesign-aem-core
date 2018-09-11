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
                <div class="hgroup">
                    <h1>${componentProperties.titleFormatted}</h1>
                    <c:if test="${componentProperties.showTags}">
                        <span class="label ${componentProperties.menuColor}">${componentProperties.category}</span>
                    </c:if>
                    ${componentProperties.subTitleFormatted}
                </div>
            </c:if>
        </header>
    </div>
</div>
