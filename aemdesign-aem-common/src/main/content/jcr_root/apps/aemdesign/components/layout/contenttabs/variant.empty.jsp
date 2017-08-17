<c:if test="${componentProperties.tabPosition == 'top'}">
    <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item">
            <a class="nav-link active" data-toggle="tab" href="#empty" role="tab">Empty</a>
        </li>
    </ul>
</c:if>
<div class="tab-content">
    <div class="tab-pane active" id="empty" role="tabpanel">Empty</div>
</div>
<c:if test="${componentProperties.tabPosition == 'bottom'}">
    <ul class="nav nav-tabs" role="tablist">
        <li class="nav-item">
            <a class="nav-link active" data-toggle="tab" href="#empty" role="tab">Empty</a>
        </li>
    </ul>
</c:if>
