<c:choose>
    <c:when test="${componentProperties.htmlInputTag eq 'inputHiddenWithLabelOnly'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <input type="hidden" disabled name="${formatedName}">
            ${textFormatMap[status.index]}
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputText'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <label class="" for="${xss:encodeForHTMLAttr(xssAPI, currentId)}" ${fn:length(componentProperties.singleFieldErrorMessages) > 0 ? " class=\"failure\"" : ""}>
                <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
                <c:if test="${componentProperties.hideTitle == false}" >
                    <span>${componentProperties.title}</span>
                </c:if>
                <input class="${componentProperties.inputCss}"
                       id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                       name="${formatedName}"
                       value="${xss:encodeForHTMLAttr(xssAPI, val)}"
                       size="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
                       placeholder="${xss:encodeForHTMLAttr(xssAPI,componentProperties.placeHolder)}"
                       type="${xss:encodeForHTMLAttr(xssAPI, componentProperties.type)}"
                <c:if test="${not empty componentProperties.width}">
                       style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
                </c:if>
                       onkeydown="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrChangeHandler)}"
                       title="${xss:encodeForHTMLAttr(xssAPI,componentProperties.title)}" >
            </label>
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'textArea'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <label for="${xss:encodeForHTMLAttr(xssAPI, currentId)}" ${fn:length(componentProperties.singleFieldErrorMessages) > 0 ? " class=\"failure\"" : ""}>
                <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
                <c:if test="${componentProperties.hideTitle == false}" >
                    <span>${componentProperties.title}</span>
                </c:if>
                <textarea id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                          name="${formatedName}"
                          class="${componentProperties.textAreaCss}"
                          rows="${xss:encodeForHTMLAttr(xssAPI,componentProperties.rows)}"
                          cols="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
                        <c:if test="${not empty componentProperties.width}">
                            style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
                        </c:if>
                          onkeydown="${xss:encodeForHTMLAttr(xssAPI,componentProperties.mrChangeHandler)}"
                          title="${xss:encodeForHTMLAttr(xssAPI,componentProperties.title)}">
                </textarea>
            </label>
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputSubmit'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <input class="${componentProperties.inputCss}"
                   id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                   name="${formatedName}"
                   value="${formatedName}"
                   size="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
                   type="${xss:encodeForHTMLAttr(xssAPI, componentProperties.type)}"
            <c:if test="${not empty componentProperties.width}">
                   style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            >
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputHidden'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${xss:encodeForHTMLAttr(xssAPI, componentProperties.name)}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}" >
                <span>${componentProperties.title}</span>
            </c:if>
            <input class="${componentProperties.inputCss}"
                   id="${xss:encodeForHTMLAttr(xssAPI, currentId)}"
                   name="${formatedName}"
                   value="${formatedName}"
                   size="${xss:encodeForHTMLAttr(xssAPI,componentProperties.cols)}"
                   type="${xss:encodeForHTMLAttr(xssAPI, componentProperties.type)}"
            <c:if test="${not empty componentProperties.width}">
                   style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            >
        </c:forEach>
    </c:when>
</c:choose>