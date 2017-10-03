<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/i18n.jsp" %>
<%
    final String DEFAULT_I18N_CATEGORY = "redirectnotification";
    final String DEFAULT_I18N_LABEL_REDIRECT_IS_SET = "redirectIsSet";
    final String DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET = "redirectIsNotSet";

    // {
    //   1 required - property name,
    //   2 required - default value,
    //   3 optional - compile into a data-{name} attribute
    // }
    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"redirectTitle", "Invalid content path"},
            {"redirectUrl", "#"},
            {"redirectTarget", _pageProperties.get("redirectTarget", "")},
    };

    ComponentProperties componentProperties = getComponentProperties(
            pageContext,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

    String redirectTarget = componentProperties.get("redirectTarget", "");
    String currentPageTitle = getPageTitle(_currentPage);

    if (StringUtils.isNotEmpty(redirectTarget) && redirectTarget.startsWith("/content")) {
        Page targetPage = _pageManager.getPage(redirectTarget);

        if (targetPage != null) {
            componentProperties.put("redirectUrl",redirectTarget.concat(DEFAULT_EXTENTION));
            componentProperties.put("redirectTitle",currentPageTitle);
        }
    } else if (StringUtils.isNotEmpty(redirectTarget)) {
        componentProperties.put("redirectTitle",currentPageTitle);
        componentProperties.put("redirectUrl",redirectTarget);
    } else {
        componentProperties.put("redirectTitle",currentPageTitle);
    }

    componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_SET,getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL_REDIRECT_IS_SET,DEFAULT_I18N_CATEGORY,_i18n));
    componentProperties.put(DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET,getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL_REDIRECT_IS_NOT_SET,DEFAULT_I18N_CATEGORY,_i18n));


%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:if test="${(CURRENT_WCMMODE eq WCMMODE_EDIT or CURRENT_WCMMODE eq WCMMODE_PREVIEW) and (not empty componentProperties.redirectTarget)}">
    <c:choose>
        <c:when test="${componentProperties.variant eq DEFAULT_VARIANT}">
            <%@ include file="variant.default.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="variant.default.jsp" %>
        </c:otherwise>
    </c:choose>
</c:if>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>

