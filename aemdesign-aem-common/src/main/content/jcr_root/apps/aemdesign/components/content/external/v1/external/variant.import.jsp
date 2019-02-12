<div ${componentProperties.componentAttributes} import>
    <c:catch var="exception">
        <c:import url="${componentProperties.target}" />
    </c:catch>
    <c:if test="${ exception != null }">
        <div class="import-error">Import Error<br>${exception.message}<br>${exception.stackTrace}</div>
    </c:if>
</div>
