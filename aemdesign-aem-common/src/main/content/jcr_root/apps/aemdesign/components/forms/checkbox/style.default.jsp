<div class="${componentProperties.inputCss}">
    <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
    <span><b>${componentProperties.hideTitle == false ? componentProperties.title : ""}</b> <i>${componentProperties['jcr:description']}</i></span>
    <c:if test="${fn:length(componentProperties.displayValues) > 1}">
        <ul>
            <c:forEach var="val" varStatus="status" items="${componentProperties.displayValues}"><!--

        --><li>
            <c:set var="currentId" value="${componentProperties.id }'-'${status.index}" />
            <label>
                <input name="${componentProperties.name}" id="${currentId}"
                <c:if test="${fn:contains(componentProperties.values, val.key)}" >
                       checked="checked"
                </c:if>
                       title="${val.value}"
                       type="checkbox"
                       value="${val.key}">
                    ${val.value}<span></span>
            </label>
        </li><!--
      --></c:forEach>
        </ul>
    </c:if>
    <c:if test="${fn:length(componentProperties.displayValues) == 1}">
        <c:forEach var="val" varStatus="status" items="${componentProperties.displayValues}">
            <c:set var="currentId" value="${componentProperties.id }'-'${status.index}" />
            <label>
                <input name="${componentProperties.name}" id="${currentId}"
                <c:if test="${fn:contains(componentProperties.values, val.key)}" >
                       checked="checked"
                </c:if>
                       title="${val.value}"
                       type="checkbox"
                       value="${val.key}">
                    ${val.value}<span></span>
            </label>
        </c:forEach>
    </c:if>
</div>