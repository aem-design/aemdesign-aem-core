<%@ page import="com.day.cq.tagging.TagConstants"%>
<%@ page import="com.google.common.base.Throwables" %>

<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>

<%
    // init
    Page thisPage = (Page) request.getAttribute("badgePage");

    String componentPath = "./"+PATH_DEFAULT_CONTENT+"/contactdetails";

    String img = this.getPageImgReferencePath(thisPage);
    img = getThumbnail(img, SMALL_IMAGE_PATH_SELECTOR, _resourceResolver);

    String url = getPageUrl(thisPage);

    Object[][] componentFields = {
            //Page Title can be override from component
            {"title", getPageNavTitle(thisPage)},
            {TagConstants.PN_TAGS, new String[]{}}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);
        componentProperties.put("componentAttributes", compileComponentAttributes(_adminTagManager, componentProperties, _component));
        List<Tag> tags = getTags(_adminTagManager, getComponentNode(thisPage,componentPath), TagConstants.PN_TAGS);
        componentProperties.put("tags",tags);

    } catch (Exception ex) {
        out.write( Throwables.getStackTraceAsString(ex) );
    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }

    componentProperties.put("url",mappedUrl(_resourceResolver, url));
    componentProperties.put("img",img);
    componentProperties.put("imgAlt", (_i18n.get("readMoreAboutText","contactdetail") + componentProperties.get("title", String.class)));

%>
    <c:set var="componentProperties" value="<%= componentProperties %>"/>

    <li>
        <div class="col-1 graphics avatar">
        <c:if test="${not empty componentProperties.title && not empty componentProperties.img}">
            <a href="${componentProperties.url}" title="${componentProperties.imgAlt}"><img alt="${componentProperties.imgAlt}" src="${componentProperties.img}" width="60" height="60"></a>
        </c:if>
        </div>

        <div class="col-4 header">
            <h3>
                <a href="${componentProperties.url}" title="${componentProperties.imgAlt}">${componentProperties.title}</a>
            </h3>
        </div>

        <div class="body">
            <p>
                <c:if test="${fn:length(componentProperties.tags) > 0}">
                    ${componentProperties.tags[0].title}
                </c:if>
            </p>
        </div>
    </li>


