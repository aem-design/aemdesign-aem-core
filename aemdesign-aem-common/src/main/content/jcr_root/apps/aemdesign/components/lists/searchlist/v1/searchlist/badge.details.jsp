<div class="card">
    <div class="card-body">
        <${componentProperties.badgeTitleType} class="card-title">Details: ${resultItem.title}</${componentProperties.badgeTitleType}>

        <c:if test="${not empty resultItem.excerpt}">
            <div class="card-text">${resultItem.excerpt}</div>
        </c:if>
    </div>
</div>
