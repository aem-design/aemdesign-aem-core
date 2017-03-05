<div ${componentProperties.componentAttributes}>
    <div class="banner video" role="banner">
        <div class="figure">
            <a class="caption lightbox media" href="${componentProperties.href}" title="${componentProperties.msg}" data-group="gallery"
               data-description="&lt;b&gt;${componentProperties.metaTitle}&lt;/b&gt;&lt;br&gt;&lt;b&gt;${componentProperties.metaDesc}&lt;/b&gt;&lt;br&gt;&lt;p&gt;${componentProperties.metaCreator}&lt;/p&gt;&lt;span&gt;${componentProperties.metaCopyRight}&lt;/span&gt;"
               <c:if test="${not empty componentProperties.lightboxWidth &&  not empty componentProperties.lightboxHeight}">
                   data-width="${componentProperties.lightboxWidth}%" data-height="${componentProperties.lightboxHeight}%"
               </c:if>
               <c:if test="${empty componentProperties.lightboxWidth ||  empty componentProperties.lightboxHeight}">
                  data-width="${componentProperties.videoWidth}" data-height="${componentProperties.videoHeight}"
               </c:if>

            ></a>
            <img src="${componentProperties.thumbnail}"  style="width:${componentProperties.thumbnailWidth}px; height:${componentProperties.thumbnailHeight}px;" />
        </div>
    </div>
</div>