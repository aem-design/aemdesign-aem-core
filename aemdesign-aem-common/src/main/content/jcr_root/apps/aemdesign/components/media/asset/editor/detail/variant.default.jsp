<div class="wrapper">
    <div class="col-8 left flex">
        <header class="page_header">
            <div class="hgroup">
                <c:if test="${not empty componentProperties.authorDescription}">
                    <h2>${componentProperties.authorDescription}</h2>
                </c:if>
                <h1>${componentProperties.title}</h1>
            </div>
        </header>
    </div>
</div>