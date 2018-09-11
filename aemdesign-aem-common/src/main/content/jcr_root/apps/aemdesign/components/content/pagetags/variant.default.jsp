<div ${componentProperties.componentAttributes}>
    <ol class="tags">
        <c:forEach var="tag" items="${componentProperties.pagetags}">
            <c:set var="taghref" value="#" scope="request"/>
            <c:if test="${not empty tag.value.href}">
                <c:set var="taghref" value="${tag.value.href}" scope="request"/>
            </c:if>
            <li><a href="${taghref}">${tag.value.title}</a></li>
        </c:forEach>
    </ol>
</div>