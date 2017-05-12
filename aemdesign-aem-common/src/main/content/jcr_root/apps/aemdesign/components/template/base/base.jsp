<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/security.jsp" %>
<%@include file="/apps/aemdesign/global/tags.jsp" %>
<%@page session="false"
        contentType="text/html; charset=utf-8"
        import="com.day.cq.wcm.foundation.ELEvaluator" %>
<%@ page import="com.google.common.base.Throwables" %>

<%
    final String LANGUAGE_TAG_PATH = "/etc/tags/language";
    final String LANGUAGE_DEFAULT = Locale.ENGLISH.getLanguage();
    final String HTML_ATTR_LANGUAGE = "lang";
    final String PAGE_PROP_REDIRECT = "redirectTarget";

    /**
     * Locale in BCP 47
     * http://www.w3.org/International/articles/bcp47/
     */
    String bcp47Lang = "";

    // read the redirect target from the 'page properties' and perform the
    // redirect if WCM is disabled.
    String location = _properties.get(PAGE_PROP_REDIRECT, "");
    // resolve variables in path
    location = ELEvaluator.evaluate(location, _slingRequest, pageContext);
    boolean wcmModeIsDisabled = WCMMode.fromRequest(request) == WCMMode.DISABLED;
    boolean wcmModeIsPreview = WCMMode.fromRequest(request) == WCMMode.PREVIEW;

    if ( (location.length() > 0) && ((wcmModeIsDisabled) || (wcmModeIsPreview)) ) {
        // check for recursion
        if (_currentPage != null && !location.equals(_currentPage.getPath()) && location.length() > 0) {
            // check for absolute path
            final int protocolIndex = location.indexOf(":/");
            final int queryIndex = location.indexOf('?');
            final String redirectPath;
            if ( protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
                redirectPath = location;
            } else {
                redirectPath = _slingRequest.getResourceResolver().map(request, location).concat(DEFAULT_EXTENTION);
            }
            response.sendRedirect(redirectPath);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return;
    }
    // set doctype
    if (_currentDesign != null) {
        _currentDesign.getDoctype(_currentStyle).toRequest(request);
    }

    ResourceResolver adminResourceResolver  = this.openAdminResourceResolver(_sling);

    try {

        //try to get an override value for language as per BCP47 spec
        Resource rootLanguage = adminResourceResolver.getResource(LANGUAGE_TAG_PATH);

        String localeString = LANGUAGE_DEFAULT;
        Locale pageLocale = _currentPage.getLanguage(true);
        if (pageLocale != null) {
            localeString = pageLocale.toString();
        }

        Resource res = rootLanguage.getChild(localeString);

        if (res == null) {
            res = rootLanguage.getChild(LANGUAGE_DEFAULT);
        }

        Tag lang = res.adaptTo(Tag.class);

        if (lang != null){
            Node langProperty = lang.adaptTo(Node.class);

            String valueString = lang.getName();
            if (langProperty.hasProperty(TAG_VALUE)) {
                valueString = langProperty.getProperty(TAG_VALUE).getValue().getString();
            } else {
                _log.warn( getError(ERROR_BCP47_OVERRIDE,ERROR_CATEGORY_GENERAL,_i18n,localeString,rootLanguage.getPath()) );
            }

            bcp47Lang += HTML_ATTR_LANGUAGE+"=\"";
            bcp47Lang += _xssAPI.encodeForHTMLAttr(valueString);
            bcp47Lang += "\"";

        }

    } catch (Exception ex) {
        throw ex;
        //out.write( Throwables.getStackTraceAsString(ex) );

    } finally {
        this.closeAdminResourceResolver(adminResourceResolver);
    }

%><!DOCTYPE html>
<html <%= bcp47Lang %>>
<cq:include script="head.jsp"/>
<cq:include script="body.jsp"/>
<%--<cq:include path="timing" resourceType="foundation/components/timing"/>--%>
</html>