<div id="hero">
    <div>
        <img src="${componentProperties.path}" alt="${componentProperties.title}" />
        <div class="content">
            <a class="hero_anchor" href="#" title="${componentProperties.titleAltReadMore}">${componentProperties.titleAltReadMore}</a>
        </div>
    </div>
</div>

<c:if test="${componentProperties.showCopyrightOwner && not empty componentProperties.copyrightOwner}" >
    <div class="content visible">
        <div class="content page_info_note">
            <small>&copy; ${componentProperties.copyrightOwner}</small>
        </div>
    </div>
</c:if>