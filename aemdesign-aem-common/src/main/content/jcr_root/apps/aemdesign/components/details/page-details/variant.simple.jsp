<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%
    Image overlayImage = getScaledProcessedImage(_resource, "overlay", 850);
%>
<c:choose>
    <c:when test="<%= !overlayImage.hasContent() %>">
        <header class="content__header" role="banner">
            <div class="styled-banner">
                <div class="styled-overlay ${componentProperties.varaint}">
                    <c:if test="${not componentProperties.hideSiteTitle}">
                        <h1>${componentProperties.titleFormatted}</h1>
                    </c:if>
                    <c:if test="${not componentProperties.hideSummary}">
                        <div class="b9">
                            <p>${componentProperties.summary}</p>
                        </div>
                    </c:if>
                </div>
            </div>
        </header>
    </c:when>
    <c:otherwise>
        <header class="content__header" role="banner">
            <div class="styled-banner" style="background-image: url('<%= mappedUrl(_resourceResolver, overlayImage.getSrc()) %>')">
                <div class="styled-overlay ${componentProperties.varaint}">
                    <c:if test="${not componentProperties.hideSiteTitle}">
                        <h1>${componentProperties.titleFormatted}</h1>
                    </c:if>
                    <c:if test="${not componentProperties.hideSummary}">
                        <div class="b9">
                            <p>${componentProperties.summary}</p>
                        </div>
                    </c:if>
                </div>
            </div>
        </header>
    </c:otherwise>
</c:choose>