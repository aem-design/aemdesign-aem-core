<%@ page import="com.day.cq.commons.Language" %>
<%@ page import="com.google.common.base.Throwables" %>
<%@ page import="org.apache.sling.api.SlingException" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="langnavdata.jsp" %>

<%
    //no lambada is available so this is the best that can be done


    Object[][] componentFields = {
            {"languageSet", new String[]{}},
            {"searchlogic", "showNearestParent"}
    };

    ComponentProperties componentProperties = getComponentProperties(pageContext, componentFields);

    componentProperties.putAll(getComponentStyleProperties(pageContext));


    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

        componentProperties.put("componentAttributes", compileComponentAttributes(_adminTagManager,componentProperties,_component));

        Map<Locale, Map<String, String>> languageToggleMap = new LinkedHashMap<Locale, Map<String, String>>();

        String appearanceOption = componentProperties.get("searchlogic", String.class);

        boolean isShowRoot =  ("showRoot").equals(appearanceOption);

        boolean isShowNothing =  ("showNothing").equals(appearanceOption);

        LinkedHashMap<String, Tag>  languageMap = getTagsMap(_adminTagManager, _currentNode,  "languageSet");


        // out.println("tagsMap : "+tagsMap);

        if (isShowNothing == false && languageMap != null){

            Set<Language> languageSet = new LinkedHashSet<Language>();

            for (String key : languageMap.keySet()){
                Tag langTag = languageMap.get(key);
                String langName = langTag.getName();
                languageSet.add(new Language(langName));
            }

            if (languageSet != null && languageSet.size() > 0){
                languageToggleMap = this.getLanguageList(languageSet, _currentPage, adminResourceResolver, _languageManager,  isShowRoot, _pageManager, _i18n);
            }

        }
        %>
        <c:set var="componentProperties" value="<%= componentProperties %>"/>
        <c:set var="languageToggleMap" value="<%= languageToggleMap %>"/>

        <c:choose>
            <c:when test="${componentProperties.searchlogic eq 'showRoot' || componentProperties.searchlogic eq 'showNearestParent' }">
                <%@include file="style.default.jsp" %>
            </c:when>
            <c:otherwise>
                <%@include file="style.hidden.jsp" %>
            </c:otherwise>
        </c:choose>
        <%
    } catch (Exception ex) {

        out.write( Throwables.getStackTraceAsString(ex) );

    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }
%>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
