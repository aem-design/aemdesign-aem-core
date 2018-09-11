<%@include file="/apps/aemdesign/global/global.jsp" %>
<c:set var="templateProperties" value="<%= request.getAttribute("templateProperties") %>"/>

<%--GENERIC SECTION--%>

<meta property="og:title" content="${templateProperties.pageTitle}"/>
<meta property="og:description" content="${templateProperties.description}"/>
<meta property="og:url" content="${templateProperties.canonicalUrl}"/>
<meta property="og:image" content="${templateProperties.imageUrl}"/>
<meta property="og:image:secure_url" content="${templateProperties.imageUrlSecure}"/>

<meta property="thumbnailUrl" content="${templateProperties.imageUrl}"/>
<meta property="headline" content="${templateProperties.pageTitle}"/>

<%--DATES METADATA--%>

<c:if test="${not empty templateProperties['jcr:created']}">
    <meta property="dateCreated" content="${templateProperties['jcr:created']}"/>
</c:if>
<c:if test="${not empty templateProperties['cq:lastReplicated']}">
    <meta property="datePublished" content="${templateProperties['cq:lastReplicated']}"/>
</c:if>
<c:choose>
    <c:when test="${not empty templateProperties['cq:lastModified']}">
        <meta property="dateModified" content="${templateProperties['cq:lastModified']}"/>
    </c:when>
    <c:when test="${not empty templateProperties['jcr:lastModified']}">
        <meta property="dateModified" content="${templateProperties['jcr:lastModified']}"/>
    </c:when>
</c:choose>

<%--CONTENT TYPE SPECIFIC SECTION--%>

<c:if test="${not empty templateProperties.metadataContentType}">
    <meta property="og:type" content="${templateProperties.metadataContentType}"/>
    <c:if test="${not empty templateProperties.keywordsList}">
        <c:choose>
            <c:when test="${templateProperties.metadataContentType eq 'article'}">
                <c:forEach items="${templateProperties.keywordsList}" var="tag">
                    <meta property="${templateProperties.metadataContentType}:tag" content="${tag.value}" />
                </c:forEach>
            </c:when>
        </c:choose>
    </c:if>
</c:if>
