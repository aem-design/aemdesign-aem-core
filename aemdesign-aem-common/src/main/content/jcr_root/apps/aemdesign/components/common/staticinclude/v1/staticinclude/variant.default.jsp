<!--staticinclude: start-->
<c:if test="${not empty componentProperties.hasContent}">
    <c:if test="${componentProperties.showContent}"><c:out value="${componentProperties.includeContents}" escapeXml="false"/></c:if>
</c:if>
<!--staticinclude: end-->
