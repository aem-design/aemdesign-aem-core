<div ${componentProperties.componentAttributes}>
    <c:if test="${not empty componentProperties.title}">
    <h1>${componentProperties.title}</h1>
    </c:if>
    <c:if test="${not empty componentProperties.author}">
    <div class="author">${componentProperties.author}</div>
    </c:if>
    <c:if test="${not empty componentProperties.tags}">
        <div class="tags">
        <c:forEach items="${templateProperties.keywordsList}" var="tag">
            <span class="tag badge badge-default" data-value="${tag.value}">${tag.title}</span>
        </c:forEach>
        </div>
    </c:if>
    <c:if test="${not empty componentProperties.newsDateStatusText}">
    <div class="published">${componentProperties.newsDateStatusText}</div>
    </c:if>
    <c:if test="${not componentProperties.showBreadcrumb and not componentProperties.showToolbar}">
    <div class="tools">
        <c:if test="${componentProperties.showBreadcrumb }">
            <cq:include path="breadcrumb" resourceType="aemdesign/components/layout/breadcrumb"/>
        </c:if>
        <c:if test="${componentProperties.showToolbar }">
            <cq:include path="toolbar" resourceType="aemdesign/components/layout/navbar"/>
        </c:if>
    </div>
    </c:if>
</div>
