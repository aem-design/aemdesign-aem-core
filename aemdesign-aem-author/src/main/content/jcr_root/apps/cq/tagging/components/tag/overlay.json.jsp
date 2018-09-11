<%@page session="false"%><%--
Author: Max Barrass
URL: http://aem.design
Description Compiles a JSON-formatted list of child tags
--%><%@page session="false"
        pageEncoding="utf-8"
        contentType="application/json; charset=UTF-8"
        import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.Arrays" %>
<%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%
%><sling:defineObjects /><%
    sling.forward(slingRequest.getRequestPathInfo().getResourcePath()  + ".infinity.json");
%>