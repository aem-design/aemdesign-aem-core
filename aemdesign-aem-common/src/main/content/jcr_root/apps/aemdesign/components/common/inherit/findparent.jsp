<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%
final String pageResourcePath = _currentPage.getContentResource().getPath(); // assume that page have resource
final Resource thisResource = _componentContext.getResource();
final String nodeResourceType = thisResource.getResourceType();
final String relativePath = thisResource.getPath().replaceFirst(pageResourcePath.concat("/"),"");
Boolean haveInheritance = false;

// defn of a parent node
// 1. is from parent page
// 2. same sling resource type
// 3. same relative path

Page curPage = _currentPage.getParent();
Resource curResource = null;
Boolean curResourceTypeMatch = false;
Boolean curCancelInheritParent = false;
ValueMap curProperties = null;


while (null != curPage) {
    // find by same relative path

    try{
        curResource = curPage.getContentResource(relativePath);
    }catch(Exception e){
        _log.info("Failed to get  "+ relativePath +" from "+curPage.getContentResource().getPath());
    }

    if (null != curResource) {
        //check for inherit flag + sling resource type
        //Boolean cancelInheritParent = properties.get("cancelInheritParent","").contentEquals("true");

        curProperties = curResource.adaptTo(ValueMap.class);
        curResourceTypeMatch = nodeResourceType.contentEquals(curResource.getResourceType());
        curCancelInheritParent = curProperties.get("cancelInheritParent","").contentEquals("true");

        if (curResourceTypeMatch && curCancelInheritParent) {
            haveInheritance = true;

            //used for displaying in badges
            request.setAttribute(INHERITED_RESOURCE, curResource);

            try {
                out.write(resourceIncludeAsHtml(_componentContext, curResource.getPath(), (SlingHttpServletResponse) response, (SlingHttpServletRequest) request));
            } catch (Exception ex) {
                getLogger().error(MessageFormat.format("{0} could not include resource {1} reason: {2}","inherit/findparent",curResource,ex.getMessage()));
            }

            break;
        }
    }
    curPage = curPage.getParent();
}

if (!haveInheritance) {
    //show this message only in edit and design mode
    if(CURRENT_WCMMODE.equals(WCMMode.EDIT) || CURRENT_WCMMODE.equals(WCMMode.DESIGN)){
        %><p class="cq-info">No parent page specifies how to render this component</p><%
    }
}

%>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
