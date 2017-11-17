<div ${componentProperties.componentAttributes}>
    <div class="video">
        <video ${fn:join(componentProperties.componentBooleanAttrs, " ")}>
        <c:forEach var="renditionVideo" items="${componentProperties.renditionsVideo}">
            <source src="${renditionVideo.key}" type="${renditionVideo.value}"/>
        </c:forEach>
        </video>
    </div>
    <cq:include path="par" resourceType="aemdesign/components/common/parsys"/>
</div>