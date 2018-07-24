<div id="hero">
    <div class="content">
        <img src="${componentProperties.path}" alt="${componentProperties.title}" />
        <div class="content">
            <a class="hero_anchor" href="#" title="${componentProperties.titleAltReadMore}">${componentProperties.titleAltReadMore}</a>
        </div>
    </div>
</div>

<c:if test="${componentProperties.showCopyrightOwner && not empty componentProperties.copyrightOwner}" >
    <div class="content visible">
        <div class="copyright">&copy; ${componentProperties.copyrightOwner}</div>
    </div>
</c:if>