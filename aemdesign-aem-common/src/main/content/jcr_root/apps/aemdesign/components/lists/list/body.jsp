
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>

<c:choose>
    <c:when test="${componentProperties.isEmpty}">
        <cq:include script="empty.jsp" />
    </c:when>
    <c:otherwise>
        <c:if test="${componentProperties.printStructure}">
            <${componentProperties.listTag}>
        </c:if>

        <c:forEach items="${list.pages}" var="item" varStatus="itemStatus">
            <c:set var="listItem" value="${item}" scope="request"/>
            <c:set var="listItemBadge" value="${componentProperties.detailsBadge}" scope="request"/>

            <c:choose>
                <c:when test="${!itemStatus.last && !itemStatus.first}">
                    <c:set var="listClass" value="item" />
                </c:when>
                <c:when test="${itemStatus.first}">
                    <c:set var="listClass" value="first" />
                </c:when>
                <c:when test="${itemStatus.last}">
                    <c:set var="listClass" value="last" />
                </c:when>
            </c:choose>

            <c:if test="${not empty item.properties.redirectTarget}">
                <c:set var="listClass" value="${listClass} redirectLink" />
            </c:if>
            <c:if test="${componentProperties.printStructure}">
                <li class="${listClass}">
            </c:if>

            <c:catch var="badgeException">
                <%
                    String componentPath = findComponentInPage((Page)request.getAttribute("listItem"),COMPONENT_DETAILS_SUFFIX)+".badge."+request.getAttribute("listItemBadge");
                    Map<String, Object> badgeRequestAttributes = (Map<String, Object>)request.getAttribute("badgeRequestAttributes");
                %>
                <%=resourceRenderAsHtml(
                        componentPath,
                        _resourceResolver,
                        _sling,
                        badgeRequestAttributes)
                %>
            </c:catch>
            <c:if test="${not empty badgeException}">
                <c:out value="${badgeException}"/>
            </c:if>

            <c:if test="${componentProperties.printStructure}">
                </li>
            </c:if>

            <c:if test="${componentProperties.splitList}">
                <c:if test="${itemStatus.index % componentProperties.listSplitEvery == 0 && !itemStatus.last}">
                    <c:if test="${componentProperties.printStructure}">
                        </li>
                        <li class="${listClass}">
                    </c:if>
                </c:if>
            </c:if>
        </c:forEach>


        <c:if test="${componentProperties.printStructure}">
            </${componentProperties.listTag}>
        </c:if>
    </c:otherwise>
</c:choose>

