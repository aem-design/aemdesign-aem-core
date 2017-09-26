<%@ page import="com.day.cq.commons.Externalizer"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.MessageFormat" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    Boolean showEmptyList = true;

    if (list!=null) {
        if (!list.isEmpty()) {
            showEmptyList = false;
        }
    }

    if ( showEmptyList ) {
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
    String listStartTag =
            (list.isOrdered() ? "<ol " : "<ul ") +
            "class=\"list_row " + (splitList ? "split-list " : "") + _xssAPI.encodeForHTML(cls) + " " + cssClass + "\" >";

    String listEndTag = list.isOrdered() ? "</ol>" : "</ul>";


    Iterator<Page> items = list.getPages();
    String listItemClass = null;
    int itemNumber = 0;

%>
<c:if test="${componentProperties.printStructure}">
<%=listStartTag%>
</c:if>
    <%
        while (items.hasNext()) {
            Page listItem = items.next();
            request.setAttribute("badgePage", listItem);
            request.setAttribute("cssClassItemLink", cssClassItemLink);

            if (null == listItemClass) {
                listItemClass = "first";
            } else if (!items.hasNext()) {
                listItemClass = "last";
            } else {
                listItemClass = "item";
            }
            boolean showRedirectIcon =  _properties.get("redirectIcon", false);
            String redirectLink = StringUtils.defaultString((String) listItem.getProperties().get("redirectTarget"), "");
            if(showRedirectIcon && !StringUtils.isEmpty(redirectLink)){
                listItemClass = listItemClass + " redirectLink";
            }

            listItemClass = listItemClass + " " + cssClassItem;


            request.setAttribute("hideThumbnail", _properties.get("hideThumbnail", Boolean.FALSE));

            String badgeSelector = _properties.get("detailsBadge", "default");
            String badgeBase = getPageBadgeBase(listItem);
            if (badgeBase == null) {
                badgeBase = PATH_DEFAULT_BADGE_BASE;
            }

            String script = badgeBase + String.format("badge.%s.jsp", badgeSelector);

            boolean scriptExist = _resourceResolver.getResource(script) != null;

            %><c:if test="${componentProperties.printStructure}">
                <li class="<%= listItemClass %>">
              </c:if><%

                    String defDecor =_componentContext.getDefaultDecorationTagName();

                    try {

                        disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                        %><cq:include script="<%= script %>" /><%
                    }
                    catch (Exception ex) {
                        if (!"JspException".equals(ex.getClass().getSimpleName())) {
                            throw ex;
                        }
                        %><p class="cq-error"><%=getError(ERROR_NOTFOUND_BADGE,ERROR_CATEGORY_GENERAL,_i18n,badgeSelector,badgeBase,script)%></p><%
                    }
                    finally {

                        enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

                    }


            %><c:if test="${componentProperties.printStructure}"></li></c:if><%

            ++itemNumber;

            // should insert cleared.
            if (splitList && (itemNumber % 2 == 0) && items.hasNext()) {
                %>
            <c:if test="${componentProperties.printStructure}"><%=listEndTag%>></c:if>
            <c:if test="${componentProperties.printStructure}"><%=listStartTag%>></c:if>
                <%
            }


        }
%>
<c:if test="${componentProperties.printStructure}">
<%=listEndTag%>
</c:if>
<%
    Boolean showListLink = _properties.get("showListLink",  _currentStyle.get("showListLink", (Boolean) false));

    if (showListLink) {

        String searchIn = _properties.get("searchIn", (String) null);
        String linkTitle = _properties.get("listLinkTitle", _currentStyle.get("listLinkTitle", (String) "More"));
        String linkPath = _properties.get("listLinkPath", _currentStyle.get("listLinkPath", searchIn));
        String linkText = _properties.get("listLinkText", _currentStyle.get("listLinkText", (String) "More"));
		String relativeLink = _externalizer.relativeLink(_slingRequest, linkPath.concat(DEFAULT_EXTENTION));

        %><a href="<%= relativeLink %>" title="<%= escapeBody(linkTitle) %>" class="linkMore"><%= escapeBody(linkText) %></a><%
    }

%>
