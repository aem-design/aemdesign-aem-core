<div class="wrapper visible">
    <c:if test="${componentProperties.showBreadcrumb }">
        <cq:include path="breadcrumb" resourceType="aemdesign/components/admin/breadcrumb"/>
    </c:if>

    <c:if test="${componentProperties.showToolbar }">
        <cq:include path="toolbar" resourceType="aemdesign/components/admin/toolbar"/>
    </c:if>
</div>

<div class="wrapper">
    <div ${componentProperties.componentAttributes}>
        <header class="page_header">
            <div class="hgroup">
                <h1>${componentProperties.title}</h1>
                <h2>
                    <c:if test="${not empty componentProperties.tags}">
                        <c:forEach items="${componentProperties.tags}" var="tag">
                            ${tag.description}
                            <br>
                        </c:forEach>
                    </c:if>
                </h2>
            </div>
        </header>
    </div>
</div>
