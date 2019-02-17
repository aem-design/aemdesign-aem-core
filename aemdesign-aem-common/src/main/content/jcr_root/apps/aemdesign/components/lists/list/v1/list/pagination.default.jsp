<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%

    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");


    int maxItem = list.size();

    int pageEnd = Math.min(
                            list.getPageStart() + list.getPageMaximum(),
                            maxItem
                    );


    ComponentProperties componentProperties = new ComponentProperties();
    componentProperties.put("list", list);
    componentProperties.put("maxItem", maxItem);
    componentProperties.put("pageEnd", pageEnd);

%>

<c:set var="componentProperties" value="<%=componentProperties %>"/>
<div class="pagination">
    <c:if test="${not empty componentProperties.list.nextPageLink}">
        <div class="next">
            <a href="<c:out value="${componentProperties.list.nextPageLink}"/>">Next</a>
        </div>
    </c:if>
    <c:if test="${not empty componentProperties.list.previousPageLink}">
        <div class="previous">
            <a href="<c:out value="${componentProperties.list.previousPageLink}"/>">Previous</a>
        </div>
    </c:if>
    <div class="label">[${componentProperties.list.pageStart + 1} - ${componentProperties.pageEnd}] of ${componentProperties.maxItem}</div>
</div>