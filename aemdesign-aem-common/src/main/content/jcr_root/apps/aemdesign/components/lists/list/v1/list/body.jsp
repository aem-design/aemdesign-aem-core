
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
                    //choose which details component to look for
                    String[] listLookForDetailComponent = (String[])request.getAttribute(REQUEST_COMPONENT_DETAILS_SUFFIX);
                    if (listLookForDetailComponent ==  null) {
                        //first found details component
                        listLookForDetailComponent = DEFAULT_LIST_DETAILS_SUFFIX;
                    }
                    String detailsPath = findComponentInPage((Page)request.getAttribute("listItem"),listLookForDetailComponent);
                    if (isNotEmpty(detailsPath)) {
                        String componentPath = detailsPath + ".badge." + request.getAttribute("listItemBadge");
                        ComponentProperties badgeRequestAttributes = (ComponentProperties) request.getAttribute(BADGE_REQUEST_ATTRIBUTES);
                        out.print(
                            resourceRenderAsHtml(
                                componentPath,
                                _resourceResolver,
                                _sling,
                                BADGE_REQUEST_ATTRIBUTES,
                                badgeRequestAttributes)
                        );
                    } else {
                        %><cq:include script="body-missing-details.jsp" /><%
                    }
                %>
            </c:catch>
            <c:if test="${not empty badgeException}">
                <c:out value="${badgeException}"/>
            </c:if>

            <c:if test="${componentProperties.printStructure}">
                </li>
            </c:if>

            <c:if test="${componentProperties.listSplit}">
                <c:if test="${(itemStatus.index + 1) % componentProperties.listSplitEvery == 0 && !itemStatus.last}">
                    <c:if test="${componentProperties.printStructure}">
                        </${componentProperties.listTag}>
                        <!--split-->
                        <${componentProperties.listTag}>
                    </c:if>
                </c:if>
            </c:if>
        </c:forEach>


        <c:if test="${componentProperties.printStructure}">
            </${componentProperties.listTag}>
        </c:if>
    </c:otherwise>
</c:choose>

