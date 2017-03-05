<%@ page import="static org.apache.commons.lang3.StringUtils.join" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isBlank" %>
<%@ page import="java.util.Map" %>

<%@ page session="false" %>
<%!
    /**
     * compile css classes
     * @param classes
     * @return
     */
    public static String addClasses(String... classes) {
        //don't return blank space and remove double spaces
        String sreturn = join(classes, " ").trim().replace("  "," ");
        if ("".equals(sreturn)) {
            return "";
        }
        return sreturn;
    }

%>

