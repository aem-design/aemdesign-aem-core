<%@page session="false"%><%--
Author: Max Barrass
URL: http://aem.design
Description Compiles a JSON-formatted list of child tags
--%><%
%><%@ page import="com.day.cq.tagging.Tag,
                   org.apache.commons.lang3.StringUtils,
                   org.apache.sling.api.resource.Resource,
                   org.apache.sling.api.resource.ValueMap,
                   org.apache.sling.commons.json.io.JSONWriter"%>
<%@ page import="javax.jcr.Node" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Iterator" %>
<%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><sling:defineObjects/><%
%><%@include file="/libs/foundation/global.jsp"%><%

    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");

    Boolean returnCurrent = Arrays.asList(slingRequest.getRequestPathInfo().getSelectors()).contains("current");
    Boolean returnPathValue = Arrays.asList(slingRequest.getRequestPathInfo().getSelectors()).contains("pathvalue");

    final JSONWriter w = new JSONWriter(response.getWriter());
    w.setTidy(Arrays.asList(slingRequest.getRequestPathInfo().getSelectors()).contains("tidy"));
    w.array();

    if (!returnCurrent) {
        Iterator<Tag> children = resource.adaptTo(Tag.class).listChildren();

        while (children.hasNext()) {
            Tag tag = children.next();
            Resource childR = tag.adaptTo(Resource.class);
            ValueMap childVM = childR.adaptTo(ValueMap.class);

            w.object();
            w.key("text").value(tag.getTitle());
            w.key("name").value(tag.getName());
            w.key("description").value(StringUtils.trimToEmpty(tag.getDescription()));
            w.key("tagID").value(tag.getTagID());
            w.key("path").value(childR.getPath());
            if (!returnPathValue) {
                w.key("value").value(childVM.get("value", tag.getName()));
            } else {
                w.key("value").value(tag.getPath());
            }
            w.endObject();
        }
    } else {
        Tag tag = resource.adaptTo(Tag.class);
        Resource childR = tag.adaptTo(Resource.class);
        ValueMap childVM = childR.adaptTo(ValueMap.class);

        w.object();
        w.key("text").value(tag.getTitle());
        w.key("name").value(tag.getName());
        w.key("description").value(StringUtils.trimToEmpty(tag.getDescription()));
        w.key("tagID").value(tag.getTagID());
        w.key("path").value(childR.getPath());
        if (!returnPathValue) {
            w.key("value").value(childVM.get("value", tag.getName()));
        } else {
            w.key("value").value(tag.getPath());
        }
        w.endObject();
    }
    w.endArray();
%>
