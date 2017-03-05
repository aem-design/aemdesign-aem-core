<%@ page import="com.day.cq.i18n.I18n" %>
<%@page session="false"%>
<%!

    public static String getDefaultLabelIfEmpty(String currentLabel, String currentCategory, String defaultCode, String defaultCategory, com.day.cq.i18n.I18n i18n, String... params) {

        if (StringUtils.isEmpty(currentLabel)) {
            return i18n.get(defaultCode, defaultCategory, params);
        } else {
            return i18n.get(currentLabel, currentCategory, params);
        }

    }

%>
