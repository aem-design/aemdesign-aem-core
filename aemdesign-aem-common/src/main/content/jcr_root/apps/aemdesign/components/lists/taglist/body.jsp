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
        <nav class="range-nav range-nav--specs js-specs-nav">
            <ul class="content">
                <c:forEach items="${componentProperties.tags}" var="tag" varStatus="tStatus">
                    <li class="range-item range-item--active" data-variant-id="97" data-is-auto="false">
                        <div class="range-item-inner">
                            <img src="${componentProperties.imagePath}" alt="${tag.title}" class="range-item-img"/>
                            <h3 class="range-title">${tag.title}</h3>
                            <div class="range-info">
                                <span class="capacity">1.5L</span>
                                <span class="from" data-bind="visible: variants()[0].basePrice().length > 0" style="display: none;">from</span>
                            <span class="price" data-bind="visible: variants()[0].basePrice().length > 0" style="display: none;">
                                <sup>$</sup>
                                <span data-bind="text: variants()[0].basePrice"/>
                                <sup>^</sup>
                            </span>
                                <img src="/assets/img/loading.gif" class="loading" data-bind="visible: variants()[0].isLoading" style="display: none;"/>
                            </div>
                        </div>
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
        </nav>
    </c:if>

</c:otherwise>

</c:choose>