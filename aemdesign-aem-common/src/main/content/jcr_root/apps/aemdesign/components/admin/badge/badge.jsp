<%--
This is the base component for badge components.
Need to set badgeResourceType. 
eg. to set: aemdesign/components/<group>/<component name>
[code]
request.setAttribute("badgeResourceType", "aemdesign/components/<group>/<component name>");
[/code].

This component will only get the first instance of detail within the par.
--%>
<%@ include file="/apps/aemdesign/global/global.jsp"%>
<%@ include file="/apps/aemdesign/global/components.jsp"%>
<%@ page import="java.util.Iterator,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.foundation.Image"%>
<%

	// init
	String badgeResourceType = (String) request.getAttribute("badgeResourceType"); // to be set by child components

	final String pathToPage = _properties.get("pathToPage", "");
	final String variant = ".badge.".concat(_properties.get("variant", "column")); // default to column badge

	badgeResourceType = (null == badgeResourceType) ? "" : badgeResourceType;

	Page targetPage = _pageManager.getPage(pathToPage);
	Resource targetResource = targetPage.getContentResource("par");
	Iterator<Resource> targetChildren = targetResource.listChildren();

	// init temp
	Resource tempResource = null;
	Boolean isFound = false;
	while (targetChildren.hasNext()) {
		tempResource = targetChildren.next();
		if (tempResource.getResourceType().contentEquals(badgeResourceType)) {
			isFound = true;
			break;
		}
	}

    // render variant
	if (isFound) {
		// eg. /content/<path/to/page>/jcr:content/par/<resource>.badge.<variant>

		String defDecor =_componentContext.getDefaultDecorationTagName();

		disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

        %><sling:include path="<%=tempResource.getPath().concat(variant) %>" /><%

		enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

	} else {
		if (CURRENT_WCMMODE.equals(WCMMode.EDIT)) {
	        %>The path to resource is invalid.<%
		}
	}
%>