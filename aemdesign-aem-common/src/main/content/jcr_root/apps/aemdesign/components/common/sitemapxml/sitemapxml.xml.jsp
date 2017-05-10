<%@ page import="java.util.Iterator" %><%@
    page import="java.util.Map" %><%@
    page import="java.util.List" %><%@
    include file="/apps/aemdesign/global/global.jsp" %><%@
    include file="sitemapxmldata.jsp" %>


<%

    // retrieve roots
    String[] roots = _properties.get("sitemapRoot", new String[0]);
    if (roots.length == 0) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return;
    }

    // get an iterator of pages
    List<Map> sitemapPages = this.getSitemapPages(_currentPage, roots);
    if (sitemapPages == null) {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT); //setStatus - sendError
        return;
    }

    response.setContentType("text/xml");

%>
<?xml version="1.0" encoding="utf-8" ?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    <c:forEach items="<%= sitemapPages %>" var="page">
        <url>
            <loc>${page.location}</loc>
            <lastmod>${page.lastModified}</lastmod>
            <changefreq>daily</changefreq>
            <priority>0.5</priority>
        </url>
    </c:forEach>
</urlset>

