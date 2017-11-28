<div ${componentProperties.componentAttributes}>
    <div class="video">
        <video ${fn:join(componentProperties.componentBooleanAttrs, " ")}>
        <c:forEach var="renditionVideo" items="${componentProperties.renditionsVideo}">
            <source src="${renditionVideo.key}" type="${renditionVideo.value}"/>
        </c:forEach>
        </video>
    </div>
    <cq:include script="/apps/aemdesign/components/common/container/container.jsp"/>
</div>