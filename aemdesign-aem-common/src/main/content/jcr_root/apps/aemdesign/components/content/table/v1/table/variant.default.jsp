<div ${componentProperties.componentAttributes}>
    <c:choose>
        <c:when test="${not empty componentProperties.text}">
            <cq:text property="text"
                     escapeXml="false"
                     placeholder="<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-table-placeholder\" alt=\"\" />"
            />
        </c:when>
        <c:otherwise>
            <cq:text property="tableData"
                     escapeXml="false"
                     placeholder="<img src=\"/apps/settings/wcm/design/aemdesign/blank.png\" class=\"cq-table-placeholder\" alt=\"\" />"
            />
        </c:otherwise>
    </c:choose>
</div>

