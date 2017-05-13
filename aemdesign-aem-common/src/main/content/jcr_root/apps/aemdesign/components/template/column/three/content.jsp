<c:if test="${CURRENT_WCMMODE ne WCMMODE_DISABLED}">
    <cq:include path="redirectnotification" resourceType="aemdesign/components/common/redirectnotification" />
</c:if>
<article id="main" role="main">
    <div class="wrapper visible">
        <cq:include path="aside-left" resourceType="aemdesign/components/layout/aside"/>
        <cq:include path="article" resourceType="aemdesign/components/layout/article"/>
        <cq:include path="aside-right" resourceType="aemdesign/components/layout/aside"/>
    </div>
</article>
