<%@ page session="false" %><%
%><%@ page import="java.util.Date,
                   com.day.cq.commons.Externalizer,
                   java.util.ArrayList,
                   com.day.cq.tagging.Tag,
                   com.day.cq.wcm.api.Page,
                   com.day.cq.wcm.api.PageManager,
                   com.day.cq.wcm.foundation.ParagraphSystem,
                   com.day.cq.wcm.foundation.Paragraph,
                   com.day.cq.wcm.foundation.Download,
                   org.apache.sling.api.resource.ResourceResolver,
                   com.day.cq.wcm.api.WCMMode"%>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %><%
%><cq:defineObjects /><%

// todo: forward to jcr:content node
//    RequestDispatcherOptions opts = new RequestDispatcherOptions();
//    opts.setForceResourceType("cq/PageContent");
//    Resource content = resourceResolver.getResource(resource.getPath() + "/" + JcrConstants.JCR_CONTENT);
//    slingRequest.getRequestDispatcher(content, opts).forward(request, response);

    try {
        WCMMode.DISABLED.toRequest(request);
        ResourceResolver rr = slingRequest.getResourceResolver();
        Page entryPage = rr.adaptTo(PageManager.class).getPage(currentNode.getPath());

        Externalizer externalizer = sling.getService(Externalizer.class);
        String url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), entryPage.getPath());

        String link = url.concat(DEFAULT_EXTENTION);
        String title = entryPage.getTitle() !=null ?
                entryPage.getTitle() : currentNode.getName();
        String desc = entryPage.getDescription() != null ?
                entryPage.getDescription() : (String)properties.get("jcr:description", null);
        Date udate = entryPage.getLastModified() != null ?
                entryPage.getLastModified().getTime() : properties.get("jcr:created", Date.class);
        Date pdate = properties.get("cq:lastReplicated", udate);
        String authorName = entryPage.getLastModifiedBy() != null ?
                entryPage.getLastModifiedBy() : properties.get("jcr:createdBy", "unknown");
        String authorEmail = "noemail@gmail.com.au";
        Tag[] tags = entryPage.getTags();

        String[] contentTypes = new String[]{"title", "text", "image", "chart", "table" };
        String[] mediaTypes = new String[]{"download", "flash"};

        ArrayList<Paragraph> contentPars = new ArrayList<Paragraph>();
        ArrayList<Paragraph> mediaPars = new ArrayList<Paragraph>();
        try {
            ParagraphSystem parsys = new ParagraphSystem(entryPage.getContentResource("par"));
            for (Paragraph par : parsys.paragraphs()) {
                for (String ct : contentTypes) {
                    if (par.getResourceType().endsWith(ct)) {
                        contentPars.add(par);
                        break;
                    }
                }
                for (String mt : mediaTypes) {
                    if (par.getResourceType().endsWith(mt)) {
                        mediaPars.add(par);
                        break;
                    }
                }
            }
        } catch (Exception e) {}


        %><atom:entry
               id="<%= url %>"
               updated="<%= udate %>"
               published="<%= pdate %>"><%
            %><atom:title><%= title %></atom:title><%
            %><atom:link href="<%= link %>"/><%
            %><atom:author name="<%= authorName %>" email="<%= authorEmail %>"/><%
            Image image = new Image(entryPage.getContentResource(),"image");
            if (image.hasContent()) {
                String logourl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), image.getHref());
                %><atom:link
                    href="<%= logourl %>"
                    rel="logo"
                    type="<%= image.getMimeType() %>"/><%
            }

            if (mediaPars.size() > 0) {
                for (Paragraph par : mediaPars) {
                    Download dld = new Download(par);
                    url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), dld.getHref());
                    %><atom:link
                            href="<%= url %>"
                            rel="enclosure"
                            type="<%= dld.getMimeType() %>"/><%
                }
            }

            if (contentPars.size() > 0) {
                %><atom:content><%
                int i = 0;
                for (Paragraph par : contentPars) {
                    String path = par.getPath().concat(".html");
                    %><sling:include path="<%= path %>"/><%
                    if (++i == 4) {
                        break;
                    }
                }
                %></atom:content><%
            }

            for (Tag tag : tags) {
                %><atom:category term="<%= tag.getTitle() %>"/><%
            }

            if (desc != null) {
                %><atom:summary><%= desc %></atom:summary><%
            }

        %></atom:entry><%

    } catch (Exception e) {
        log.error("error while rendering feed entry for page", e);
    }
%>