<div ${componentProperties.componentAttributes} include>
    <c:if test="${WCMMODE_EDIT == CURRENT_WCMMODE}">
        &lt;!--#include virtual="${componentProperties.target}" --&gt;
    </c:if>
    <c:if test="${WCMMODE_EDIT != CURRENT_WCMMODE}">
        <!--#include virtual="${componentProperties.target}" -->
    </c:if>
</div>
