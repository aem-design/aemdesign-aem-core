<div ${componentProperties.componentAttributes} hidden>
<c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}">
    <div class="component notfound">${componentProperties.hiddenText}</div>
</c:if>
</div>
