<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "searchlist";

    ComponentProperties componentProperties = (ComponentProperties) request.getAttribute("componentProperties");

    //override properties
    componentProperties.put("listItemLinkText",
            getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TEXT, DEFAULT_I18N_CATEGORY,_i18n));

    componentProperties.put("listItemLinkTitle",
            getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TITLE, DEFAULT_I18N_CATEGORY,_i18n));

    request.setAttribute("componentProperties", componentProperties);
    request.setAttribute("COMPONENT_DETAILS_SUFFIX", new String[] { "page-details" });

%>
