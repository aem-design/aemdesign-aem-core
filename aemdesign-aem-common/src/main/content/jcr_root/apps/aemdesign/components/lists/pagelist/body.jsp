<%@ page import="com.day.cq.commons.Externalizer"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    final Boolean DEFAULT_PRINT_STRUCTURE = true;

    if ( list == null || list.isEmpty() ) {
%><cq:include script="empty.jsp" /><%
        return;
    }

    Object[][] componentFields = {
            {"printStructure", DEFAULT_PRINT_STRUCTURE},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


    boolean splitList = _properties.get("splitList", false);
    String cssClass = _properties.get("cssClassList", "");
    String cssClassItem = _properties.get("cssClassItem", "");
    String cssClassItemLink = _properties.get("cssClassItemLink", "");

    boolean printStructure = componentProperties.get("printStructure", DEFAULT_PRINT_STRUCTURE);

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
<%= printStructure?listStartTag:"" %>
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
        String badgeBase = getPageBadgeBase(listItem, "page-details");
        if (badgeBase == null) {
            badgeBase = PATH_DEFAULT_BADGE_BASE;
        }

        String script = badgeBase + String.format("badge.%s.jsp", badgeSelector);

        if (printStructure) {
        %><li class="<%= listItemClass %>"><%
        }
            String addPageNameAsIcon = _properties.get("addPageNameAsIcon", "");
            if (isNotEmpty(addPageNameAsIcon) && "true".equals(addPageNameAsIcon)) {
                String iconClass = "";
                iconClass = MessageFormat.format("icon icon-{0}", listItem.getName());
                %><span class="<%=iconClass%>"></span><%
            }

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

        if (printStructure) {
        %></li><%
        }

        ++itemNumber;

        // should insert cleared.
        if (splitList && (itemNumber % 2 == 0) && items.hasNext()) {
            %><%= printStructure?listEndTag:"" %>
            <%= printStructure?listStartTag:"" %><%
        }


    }
%>
<%= printStructure?listEndTag:"" %>
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
