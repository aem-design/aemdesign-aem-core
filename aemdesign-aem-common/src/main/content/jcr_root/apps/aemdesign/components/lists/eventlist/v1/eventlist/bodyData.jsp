<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "eventlist";

    ComponentProperties componentProperties = (ComponentProperties)request.getAttribute(COMPONENT_PROPERTIES);

    //override properties
    componentProperties.put("listItemLinkText", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TEXT,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("listItemLinkTitle", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TITLE,DEFAULT_I18N_CATEGORY,_i18n));

    request.setAttribute(COMPONENT_PROPERTIES, componentProperties);
    request.setAttribute(REQUEST_COMPONENT_DETAILS_SUFFIX, new String[] {"event-details"});

%>
