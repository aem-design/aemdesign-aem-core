<div ${componentProperties.componentAttributes} hidden>
<c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
    <p class="component notfound">${componentProperties.hiddenText}</p>
</c:if>
</div>
