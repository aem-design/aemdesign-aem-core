<div ${componentProperties.componentAttributes} import>
    <c:if test="${WCMMODE_EDIT == CURRENT_WCMMODE}">
        &lt;c:import url="${componentProperties.target}"/&gt;
    </c:if>
    <c:if test="${WCMMODE_EDIT != CURRENT_WCMMODE}">
        <c:catch var="exception">
            <c:import url="${componentProperties.target}" />
        </c:catch>
        <c:if test="${ exception != null }">
            <div class="import-error">Import Error<br>${exception.message}<br>${exception.stackTrace}</div>
        </c:if>
    </c:if>
</div>
