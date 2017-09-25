<%@ page import="java.util.Iterator" %>
<%@ page import="org.apache.commons.lang3.*" %>
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

    //need to retrieve the pages again
    Iterator<Page> items = list.getPages();

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
                    "class=\"list_row " + (splitList ? "split-list " : "") + _xssAPI.encodeForHTML(cls) + " " + cssClass + "\"" +
                    componentProperties.get("componentAttributes", String.class) +
                    " >";

    String listEndTag = list.isOrdered() ? "</ol>" : "</ul>";

    String listItemClass = null;



%>
<%= printStructure?listStartTag:"" %>
    <%
        while (items.hasNext()) {

        Page listItem = items.next();

        String badgeSelector = _properties.get("detailsBadge", "default");
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

        String category = StringUtils.EMPTY;
        if (listItem.getContentResource().adaptTo(Node.class) != null){
            String[] categoryMapTags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

            LinkedHashMap<String, Map> categoryMap = getTagsAsAdmin(_sling, categoryMapTags, _slingRequest.getLocale());

            if (categoryMap != null && categoryMap.size() > 0){
                //reformat the filter for attribute
                category = StringUtils.join(categoryMap.keySet(), " ");
                category = category.replaceAll("[^a-zA-Z ]+", "-");
            }
        }

        listItemClass = listItemClass + " " + cssClassItem + " " + StringEscapeUtils.escapeXml(category) + " ";

        String badgeBase = getPageBadgeBase(listItem, "news-details");

        if (badgeBase == null) {
            badgeBase = PATH_DEFAULT_BADGE_BASE;
        }

        String script = badgeBase + String.format("badge.%s.jsp", badgeSelector);

        String defDecor =_componentContext.getDefaultDecorationTagName();

        request.setAttribute("badgePage", listItem);

        if (printStructure) {
        %><li class="<%= listItemClass %>"><%
        }
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

        if (printStructure) {
        %></li><%
                }
    }

            %>
<%= printStructure?listEndTag:"" %>




