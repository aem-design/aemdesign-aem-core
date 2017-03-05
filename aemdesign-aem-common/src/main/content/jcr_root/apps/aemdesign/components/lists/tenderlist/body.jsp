<%@ page import="com.day.cq.commons.Externalizer"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    if ( list.isEmpty() ) {
%><cq:include script="empty.jsp" /><%
        return;
    }


    boolean splitList = _properties.get("splitList", false);
    String cssClass = _properties.get("cssClassList", "");
    String cssClassItem = _properties.get("cssClassItem", "");
    String cssClassItemLink = _properties.get("cssClassItemLink", "");

    String cls = list.getType();
    cls = (cls == null) ? "" : cls.replaceAll("/", "");

    // clear

    Iterator<Page> items = list.getPages();
    String listItemClass = null;



    String badgeSelector = _properties.get("variant", "default");

%>
<c:set var="componentProperties" value="<%= _componentContext.getAttribute("componentProperties") %>"/>
<c:if test="${componentProperties.printStructure}">
    <ul>
</c:if>
<%
    //need to retrieve the pages again
    items = list.getPages();

    while (items.hasNext()) {

        Page listItem = items.next();

        request.setAttribute("hideThumbnail", _properties.get("hideThumbnail", Boolean.FALSE));

        request.setAttribute("badgePage", listItem);

        request.setAttribute("cssClassItemLink", cssClassItemLink);

        String badgeBase = getPageBadgeBase(listItem);
        if (badgeBase == null) {
            badgeBase = PATH_DEFAULT_BADGE_BASE;
        }

        String script = "/apps/aemdesign/components/details/tender-details/badge.default.jsp";

        String defDecor =_componentContext.getDefaultDecorationTagName();

        try {
            disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

%><cq:include script="<%= script %>" /><%

} catch (Exception ex) {
    if (!"JspException".equals(ex.getClass().getSimpleName())) {
        throw ex;
    }
%><p class="cq-error">Variation not found for <%=badgeSelector%> content type (<%=script%> not found)</p><%
        }finally {
            enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);
        }

    }
%>
<c:if test="${componentProperties.printStructure}">
    </ul>
</c:if>