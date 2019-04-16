<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    Object[][] componentFields = {
            {"displayType", StringUtils.EMPTY},
            {"disclaimer", StringUtils.EMPTY},
            {"imagePath", StringUtils.EMPTY},
            {"cssClass", StringUtils.EMPTY},
            {"Id", StringUtils.EMPTY}
    };
    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    List<Tag> tags = getTags(_tagManager,_currentNode,"tags");
    componentProperties.put("tags", tags);
%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
<c:when test="${componentProperties.displayType == 'list'}">
    <c:if test="${not empty componentProperties.tags}">
        <ul class="${componentProperties.cssClass}">
            <c:forEach items="${componentProperties.tags}" var="tag" varStatus="tStatus">
                <li>
                    <a href="#" class="aStateLink" data-state-abbr="${tag.name}" data-state="${tag.title}">${tag.title}</a>
                </li>
            </c:forEach>
        </ul>
    </c:if>
</c:when>
    <c:when test="${componentProperties.displayType == 'optionList'}">
        <c:if test="${not empty componentProperties.tags}">
            <select id="${componentProperties.Id}" class="${componentProperties.cssClass}">
                <option value="" selected disabled>Select</option>
                <c:forEach items="${componentProperties.tags}" var="tag" varStatus="tStatus">
                    <option value="${tag.name}">${tag.title}</option>
                </c:forEach>
            </select>
        </c:if>
    </c:when>
<c:otherwise>
    <c:if test="${not empty componentProperties.tags}">
        <ul class="content">
            <c:forEach items="${componentProperties.tags}" var="tag" varStatus="tStatus">
                <li class="tag">
                        ${tag.title}
                </li>
            </c:forEach>
            <div class="range-disclaimer">
                <div href="#" class="generic-tooltip right">
                    ^ What you need to know
                    <div style="width: 550px;">
                            ${componentProperties.disclaimer}
                    </div>
                </div>
            </div>
        </ul>
    </c:if>

</c:otherwise>

</c:choose>