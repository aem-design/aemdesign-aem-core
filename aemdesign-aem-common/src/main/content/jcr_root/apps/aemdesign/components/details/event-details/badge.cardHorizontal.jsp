<!-- TODO: Refactoring & cleanup, Please refer to `badge.cardIconTitleDateTimeDescriptionAction` changes. -->

<div class="card card-horizontal text-center text-md-left ${badgeClassStyleAttr}">
    <div class="row align-items-center no-gutters">
        <c:if test="${componentProperties.cardIconShow}">
            <div class="col-md-auto">
                <i class="icon ${badgeClassIconAttr}" title="${componentProperties.title}"></i>
            </div>
        </c:if>
        <div class="col p-collapse pt-reset pt-md-collapse pl-reset pr-reset">
            <${componentProperties.badgeTitleType} class="card-title">${componentProperties.pageNavTitle}</${componentProperties.badgeTitleType}>
            <c:if test="${not empty componentProperties.category}">
                <div class="card-category">
                    <ul class="tags">
                        <c:forEach items="${componentProperties.category}" var="tag" varStatus="entryStatus">
                            <li id="${tag.key}">${tag.value}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            <div class="card-text">${componentProperties.description}</div>
        </div>
        <div class="card-action col-md-auto mt-reset mt-md-collapse">
            <a class="card-link ${fn:join(componentProperties.badgeLinkStyle, ' ')}"
                href="${componentProperties.pageUrl}"
                target="${componentProperties.badgeLinkTarget}"
                title="${componentProperties.badgeLinkTitle}"
                ${badgeLinkAttr}>${componentProperties.badgeLinkText}</a>
        </div>
    </div>
</div>

<div component class="eventlist  theme--list-card column-3 cards-horizontal  " id="eventlist2" data-modules="">
    <div class="content">
        <ul>
            <li class="first">
                <div class="card small ">
                    <div class="card-icon">
                        <i class="fa fa-calendar-alt"></i>
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">Wantirna Advice Night</h3>
                        <div class="card-date">
                            <div class="col-1 date">
                                <p>MON 10 SEPTEMBER</br>4pm – 6pm</p>
                            </div>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
    </div>
</div>