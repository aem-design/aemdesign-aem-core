<%@ page import="com.day.cq.i18n.I18n" %>
<%@page session="false"%>
<%!

    public static String ERROR_NOTFOUND_BADGE = "ERROR_NOTFOUND_BADGE";
    public static String ERROR_BCP47_OVERRIDE = "ERROR_BCP47_OVERRIDE";
    public static String ERROR_CATEGORY_GENERAL = "general";

    public static String getError(String code, String category,  com.day.cq.i18n.I18n i18n, String... params) {
        return i18n.get(code, category, params);
    }

%>
