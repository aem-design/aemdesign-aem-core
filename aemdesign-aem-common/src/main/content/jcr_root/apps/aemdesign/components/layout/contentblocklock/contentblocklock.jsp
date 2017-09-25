<%@page session="false" %>
<%@ page import="org.apache.jackrabbit.api.security.user.Authorizable" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/components/layout/contentblock/contentblockdata.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%

    final String DEFAULT_I18N_CATEGORY = "contentblock";
    final String DEFAULT_I18N_BACKTOTOP_LABEL = "backtotoplabel";
    final String DEFAULT_I18N_BACKTOTOP_TITLE = "backtotoptitle";
    final String DEFAULT_TITLE_TAG_TYPE = "h2";
    final String FIELD_LOCKED = "islocked";



    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"hideTitle", false},
            {"hideTopLink", false},
            {FIELD_LOCKED, true},
            {"linksLeftTitle", ""},
            {"linksRightTitle", ""},
            {"dataTitle", ""},
            {"dataScroll", ""},
            {"linksRight", new String[]{}},
            {"linksLeft", new String[]{}},
            {"titleType", DEFAULT_TITLE_TAG_TYPE},
            {"title", ""},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    componentProperties.put("linksRightList",getContentPageList(_pageManager, componentProperties.get("linksRight", new String[]{})));
    componentProperties.put("linksLeftList",getContentPageList(_pageManager, componentProperties.get("linksLeft", new String[]{})));

    componentProperties.put("topLinkLabel",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_LABEL,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put("topLinkTitle",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_TITLE,DEFAULT_I18N_CATEGORY,_i18n));

    componentProperties.put(COMPONENT_ATTRIBUTES, addComponentBackgroundToAttributes(componentProperties,_resource,DEFAULT_BACKGROUND_IMAGE_NODE_NAME));

    if (componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT).equals("advsection")) {
        String ariaLabelledBy = componentProperties.get(FIELD_ARIA_LABELLEDBY, "");
        if (isEmpty(ariaLabelledBy)) {
            String labelId = "heading-".concat(_currentNode.getName());
            componentProperties.put(FIELD_ARIA_LABELLEDBY, labelId);
            componentProperties.put(COMPONENT_ATTRIBUTES, addComponentAttributes(componentProperties,"aria-labelledby",labelId));
        }

    }

    String instanceName = _component.getCellName();
    if (_currentNode !=null ) {
        instanceName = _currentNode.getName();
    }
    componentProperties.put("instanceName", instanceName);

    final Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);
    final List<String> groups = Arrays.asList(componentProperties.get("groups", new String[]{"administrators"}));

    if (isUserMemberOf(authorizable,groups)) {
        componentProperties.put(FIELD_LOCKED, false);
    }

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${CURRENT_WCMMODE eq WCMMODE_DISABLED or ( CURRENT_WCMMODE eq WCMMODE_EDIT and not componentProperties.islocked )}">
        <c:choose>
            <c:when test="${componentProperties.variant eq 'descriptionlist'}">
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.descriptionlist.jsp" %>
            </c:when>
            <c:when test="${componentProperties.variant eq 'fieldset'}">
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.fieldset.jsp" %>
            </c:when>
            <c:when test="${componentProperties.variant eq 'advsection'}">
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.advanced.jsp" %>
            </c:when>
            <c:when test="${componentProperties.variant eq 'floating'}">
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.floating.jsp" %>
            </c:when>
            <c:when test="${componentProperties.variant eq 'container'}">
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.container.jsp" %>
            </c:when>
            <c:otherwise>
                <%@ include file="/apps/aemdesign/components/layout/contentblock/variant.default.jsp" %>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <%
        String defDecor =_componentContext.getDefaultDecorationTagName();

        try {

            disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

            %><cq:include path="par" resourceType="foundation/components/parsys"/><%
        }
        catch (Exception ex) {
            %><p class="cq-error">Missing content.</p><%
        }
        finally {

            enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

        }
        %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
