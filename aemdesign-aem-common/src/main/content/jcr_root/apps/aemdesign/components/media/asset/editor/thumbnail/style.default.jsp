<div id="hero">
    <div>
        <img src="${componentProperties.path}" alt="${componentProperties.title}" />
        <div class="wrapper">
            <a class="hero_anchor" href="#" title="${componentProperties.titleAltReadMore}">${componentProperties.titleAltReadMore}</a>
        </div>
    </div>
</div>

<c:if test="${componentProperties.showCopyrightOwner && not empty componentProperties.copyrightOwner}" >
    <div class="wrapper visible">
        <div class="wrapper page_info_note">
            <small>&copy; ${componentProperties.copyrightOwner}</small>
        </div>
    </div>
</c:if>