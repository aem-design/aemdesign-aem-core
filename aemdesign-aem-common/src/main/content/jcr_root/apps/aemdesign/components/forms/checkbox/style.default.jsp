<div class="${componentProperties.inputCss}">
    <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
    <span><b>${componentProperties.hideTitle == false ? componentProperties.title : ""}</b> <i>${componentProperties['jcr:description']}</i></span>
    <c:if test="${fn:length(componentProperties.displayValues) > 1}">
        <ul>
            <c:forEach var="val" varStatus="status" items="${componentProperties.displayValues}"><!--

        --><li>
            <c:set var="currentId" value="${componentProperties.id }'-'${status.index}" />
            <label>
                <input name="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                <c:if test="${fn:contains(componentProperties.values, val.key)}" >
                       checked="checked"
                </c:if>
                       title="${xss:encodeForHTMLAttr(xssAPI, val.value)}"
                       type="checkbox"
                       value="${xss:encodeForHTMLAttr(xssAPI, val.key)}">
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
                <input name="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                <c:if test="${fn:contains(componentProperties.values, val.key)}" >
                       checked="checked"
                </c:if>
                       title="${xss:encodeForHTMLAttr(xssAPI, val.value)}"
                       type="checkbox"
                       value="${xss:encodeForHTMLAttr(xssAPI, val.key)}">
                    ${val.value}<span></span>
            </label>
        </c:forEach>
    </c:if>
</div>