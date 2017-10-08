<%@ include file="/apps/aemdesign/global/global.jsp" %>

<c:if test="${componentProperties.isPaginating}">

<%

    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    String paginationType = _properties.get("paginationType", "default");

    String script = String.format("pagination.%s.jsp", paginationType);

    int totalSize = list == null ? 0: list.size();
    int pageStart =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getPageStart();
    int pageMax =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getPageMaximum();
    String pageType =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getType();

    int pageNumber = pageStart / pageMax + 1;
    if ( pageMax != -1 && list.isEmpty() == false && (totalSize - pageNumber * pageMax) > 0) {
%>


    <c:catch var="exception">
        <% //disableEditMode(_componentContext, IncludeOptions.getOptions(request, true) , _slingRequest); %>
        <cq:include script="<%=script%>" />
    </c:catch>
    <c:if test="${ exception != null }">
        <p class="cq-error">List initialize error.<br/>${exception.message}<br>${exception.stackTrace}</p>
    </c:if>
    <c:if test="${ exception == null }">
        <% //enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest); %>
    </c:if>
<%
    }
%>

</c:if>