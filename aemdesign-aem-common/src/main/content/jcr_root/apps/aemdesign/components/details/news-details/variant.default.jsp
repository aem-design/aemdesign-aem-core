<div class="wrapper visible">
    <c:if test="${componentProperties.showBreadcrumb }">
        <cq:include path="breadcrumb" resourceType="aemdesign/components/layout/breadcrumb"/>
    </c:if>


    <c:if test="${componentProperties.showToolbar }">
        <cq:include path="toolbar" resourceType="aemdesign/components/layout/navbar"/>
    </c:if>
</div>

<div class="wrapper">
    <div ${componentProperties.componentAttributes}>
        <header class="page_header">
            <div class="hgroup">
                <h1>${componentProperties.title}</h1>
            </div>
            <c:if test="${not empty componentProperties.tags}">
                <span class="label">${componentProperties.tags[0].localizedTitles[LOCALE]}</span>
            </c:if>
            ${componentProperties.newsStatusLabel}
        </header>
    </div>
</div>
