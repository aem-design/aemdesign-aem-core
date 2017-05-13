<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@ page import="java.text.MessageFormat" %>

<c:if test="${PRINT_COMPONENT_BADGE}">
    <c:if test="${CURRENT_WCMMODE == WCMMODE_EDIT || CURRENT_WCMMODE == WCMMODE_PREVIEW}">
    <details class="component badge badge-pill cq-info">
        <%
            String originLocation = "";
            String originVariant = "";

            try {
                Resource originalResource = (Resource) request.getAttribute(INHERITED_RESOURCE);
                if (originalResource != null) {
                    String originPath = originalResource.getPath();
                    String originName = originalResource.getName();
                    ValueMap resourceProps = originalResource.getValueMap();
                    if (resourceProps!=null) {
                        originVariant = resourceProps.get("variant","variant not set");
                    }
                    if (originPath.indexOf("/jcr") > 0) {
                        originPath = originPath.substring(0, originPath.indexOf("/jcr"));

                        Page originPage = _pageManager.getPage(originPath);
                        if (originPage != null) {
                            originName = originPage.getTitle();
                        } else {
                            originName = originPath;
                        }
                    }
                    originLocation = MessageFormat.format(" Source: <a href=\"{0}.html\">{1}</a>", originPath, originName);
                    request.removeAttribute(INHERITED_RESOURCE);
                } else {
                    ValueMap resourceProps = _resource.getValueMap();
                    if (resourceProps!=null) {
                        originVariant = resourceProps.get("variant","variant not set");
                    }
                }

            } catch (Exception ex) {}
        %>
        <summary><%=_component.getTitle()%></summary>
        <%--<c:if test="${not empty componentProperties}">--%>
            <%--<c:forEach items="${componentProperties}" var="property" varStatus="status"> <small>${property.key}: ${property.value};</small></c:forEach>--%>
        <%--</c:if>--%>
        <small>(<%=_componentContext.getCell().getName()%> : <%=_resource.getName()%> # <%=originVariant%>)<%=originLocation%></small>
    </details>
    </c:if>
</c:if>
