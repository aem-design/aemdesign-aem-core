<c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
    <cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
</c:if>
<article id="main" role="main">
    <div class="wrapper visible">
        <cq:include path="article" resourceType="aemdesign/components/layout/article"/>
    </div>
</article>
