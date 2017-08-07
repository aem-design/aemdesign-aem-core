<a ${componentProperties.componentAttributes} download>
    ${componentProperties.title}
    <c:if test="${ not empty componentProperties.licenseInfo}">
        <span class="license">${componentProperties.licenseInfo}</span>
    </c:if>
</a>