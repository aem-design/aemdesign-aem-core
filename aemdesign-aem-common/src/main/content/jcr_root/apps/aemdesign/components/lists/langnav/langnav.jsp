<%@ page import="com.day.cq.commons.Language" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.sling.api.SlingException" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/i18n.jsp" %>

<%
    //not using lamda is available so this is the best that can be done


    Object[][] componentFields = {
            {"languageSet", new String[]{}},
            {"searchlogic", "showNearestParent"}
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);


    Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();

    String appearanceOption = componentProperties.get("searchlogic", String.class);

    boolean isShowRoot =  ("showRoot").equals(appearanceOption);

    boolean isShowNothing =  ("showNothing").equals(appearanceOption);

    String[] tagsFilterList = componentProperties.get("languageSet", new String[]{});

    LinkedHashMap<String, Map> languageMap = getTagsAsAdmin(_sling, tagsFilterList, _slingRequest.getLocale());

    // out.println("tagsMap : "+tagsMap);

    if (isShowNothing == false && languageMap != null){

        Set<Language> languageSet = new LinkedHashSet<Language>();

        for (String key : languageMap.keySet()){
            Map<String, String> langTag = languageMap.get(key);
            String langName = langTag.get("tagid");
            languageSet.add(new Language(langName));
        }

        if (languageSet != null && languageSet.size() > 0){
            languageToggleMap = this.getLanguageList(_sling, languageSet, _currentPage, _languageManager,  isShowRoot, _pageManager, _i18n);
        }

    }

    componentProperties.put("language", getPageLanguage(_sling,_currentPage));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:set var="languageToggleMap" value="<%= languageToggleMap %>"/>

<c:choose>
    <c:when test="${componentProperties.searchlogic eq 'showRoot' || componentProperties.searchlogic eq 'showNearestParent' }">
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@include file="variant.hidden.jsp" %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
