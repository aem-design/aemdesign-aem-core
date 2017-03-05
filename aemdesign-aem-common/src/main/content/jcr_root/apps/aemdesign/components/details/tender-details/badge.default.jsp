<%@ page session="false" import="com.day.cq.tagging.TagConstants"%>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    // init
    Page thisPage = (Page) request.getAttribute("badgePage");
    Boolean hideThumbnail = (Boolean) request.getAttribute("hideThumbnail");
    String cssClassItemLink = (String) request.getAttribute("cssClassItemLink");


    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/tender-details";


//    String img = this.getPageImgReferencePath(thisPage);
//    img = getThumbnail(img, DEFAULT_IMAGE_PATH_SELECTOR, _resourceResolver);


    String url = getPageUrl(thisPage);

    //no lambada is available so this is the best that can be done


    Object[][] componentFields = {
            {"title", getPageNavTitle(thisPage)},
            {"description", thisPage.getProperties().get("jcr:description", "")},
            {"publishDate", Calendar.getInstance()},
            {"closingDate", Calendar.getInstance()},
            {TagConstants.PN_TAGS, new String[]{}}

    };


    ComponentProperties componentProperties = getComponentProperties(thisPage, componentPath, componentFields);

    //get proper title
    //componentProperties.put("title",getPageNavTitle(thisPage));

    List<Tag> tags = getTags(_tagManager, getComponentNode(thisPage,componentPath), TagConstants.PN_TAGS);

    String closingDateLabel = StringUtils.EMPTY;


    if (componentProperties.get("closingDate") != null){
        Date date = ((Calendar)componentProperties.get("closingDate")).getTime();
        boolean isClosingDate = date.after(new Date(System.currentTimeMillis()));
        if (isClosingDate){
            closingDateLabel = _i18n.get("Closing date: ",  "tenderdetail");

        }else{
            closingDateLabel = _i18n.get("Closed ",  "tenderdetail");

        }

        componentProperties.put("isClosingDate", isClosingDate);
    }

    componentProperties.put("url", mappedUrl(url));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="closingDateLabel" value="<%= closingDateLabel %>"/>
<c:set var="tags" value="<%= tags %>"/>

<li>
    <div class="col-1 date">
        <time datetime="<fmt:formatDate value="${componentProperties.publishDate.time}" pattern="yyyy-MM-dd" />" pubdate="pubdate"><fmt:formatDate value="${componentProperties.publishDate.time}" pattern="dd.MM.yyyy" /></time>
    </div>
    <div class="col-4 header">
        <h3>
            <a href="${componentProperties.url}" title="${componentProperties.title}">${componentProperties.title}</a>
        </h3>
    </div>
    <div class="body">
        <p>${componentProperties.description}</p>
    </div>
    <div class="col-2 footer">
        <p>
            <c:choose>
                <c:when test="${componentProperties.isClosingDate}">
                    ${closingDateLabel} <time datetime="<fmt:formatDate value="${componentProperties.closingDate.time}" pattern="yyyy-MM-dd" />"><fmt:formatDate value="${componentProperties.closingDate.time}" pattern="dd.MM.yyyy HH:mm" /></time>
                </c:when>
                <c:otherwise>
                    ${closingDateLabel}
                </c:otherwise>
            </c:choose>
        </p>
    </div>
</li>
