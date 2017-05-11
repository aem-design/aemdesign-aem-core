<c:choose>
    <c:when test="${componentProperties.htmlInputTag eq 'inputHiddenWithLabelOnly'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${componentProperties.name}" />
            <input type="hidden" disabled name="${formatedName}">
            ${textFormatMap[status.index]}
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputText'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${componentProperties.name}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <label class="" for="${currentId}" ${fn:length(componentProperties.singleFieldErrorMessages) > 0 ? " class=\"failure\"" : ""}>
                <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
                <c:if test="${componentProperties.hideTitle == false}" >
                    <span>${componentProperties.title}</span>
                </c:if>
                <input class="${componentProperties.inputCss}"
                       id="${currentId}"
                       name="${formatedName}"
                       value="${val}"
                       size="${componentProperties.cols}"
                       placeholder="${componentProperties.placeHolder}"
                       type="${componentProperties.type}"
                <c:if test="${not empty componentProperties.width}">
                       style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
                </c:if>
                       onkeydown="${componentProperties.mrChangeHandler}"
                       title="${componentProperties.title}" >
            </label>
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'textArea'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${componentProperties.name}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <label for="${currentId}" ${fn:length(componentProperties.singleFieldErrorMessages) > 0 ? " class=\"failure\"" : ""}>
                <mark role="log">${fn:join(componentProperties.singleFieldErrorMessages, ',')}</mark>
                <c:if test="${componentProperties.hideTitle == false}" >
                    <span>${componentProperties.title}</span>
                </c:if>
                <textarea id="${currentId}"
                          name="${formatedName}"
                          class="${componentProperties.textAreaCss}"
                          rows="${componentProperties.rows}"
                          cols="${componentProperties.cols}"
                        <c:if test="${not empty componentProperties.width}">
                            style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
                        </c:if>
                          onkeydown="${componentProperties.mrChangeHandler}"
                          title="${componentProperties.title}">
                </textarea>
            </label>
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputSubmit'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${componentProperties.name}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <input class="${componentProperties.inputCss}"
                   id="${currentId}"
                   name="${formatedName}"
                   value="${formatedName}"
                   size="${componentProperties.cols}"
                   type="${componentProperties.type}"
            <c:if test="${not empty componentProperties.width}">
                   style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            >
        </c:forEach>
    </c:when>
    <c:when test="${componentProperties.htmlInputTag eq 'inputHidden'}">
        <c:forEach var="val" items="${componentProperties.values}" varStatus="status">
            <c:set var="formatedName" value="${componentProperties.name}" />
            <c:set var="currentId" value="${status.index == 0 ? componentProperties.id : componentProperties.id + '-' + status.index}" />
            <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT}" >
                <span>${componentProperties.title}</span>
            </c:if>
            <input class="${componentProperties.inputCss}"
                   id="${currentId}"
                   name="${formatedName}"
                   value="${formatedName}"
                   size="${componentProperties.cols}"
                   type="${componentProperties.type}"
            <c:if test="${not empty componentProperties.width}">
                   style="width:${xss:getValidInteger(xssAPI,componentProperties.width, 100)}px;"
            </c:if>
            >
        </c:forEach>
    </c:when>
</c:choose>