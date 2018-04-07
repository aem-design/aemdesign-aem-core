<%@ page session="false" %><%
%><%@ page import="com.adobe.granite.license.ProductInfo,
                   com.adobe.granite.license.ProductInfoService,
                   com.day.cq.wcm.foundation.Download" %>
<%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%
%><%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %><%
%><%@ include file="/apps/aemdesign/global/global.jsp" %><%
%><%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="init.jsp"  %><%

try {
    WCMMode.DISABLED.toRequest(request);
    request.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);

    Externalizer externalizer = sling.getService(Externalizer.class);
    String url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentPage.getPath());

    String link = url + ".feed";
    String title = currentPage.getTitle() != null ?
            currentPage.getTitle() : currentNode.getName();
    String description = currentPage.getDescription() != null ?
            currentPage.getDescription() : (String)properties.get(JcrConstants.JCR_DESCRIPTION, null);
    %><atom:feed id="<%= url %>"><%
        %><atom:title><c:out value="<%= title %>"/></atom:title><%
        if (description != null) {
            %><atom:summary><c:out value="<%= description %>"/></atom:summary><%
        }
    %><atom:link href="<%= link %>" rel="self"/><%

    final ProductInfo[] infos = sling.getService(ProductInfoService.class).getInfos();
    final ProductInfo pi = null != infos && infos.length > 0 ? infos[0] : null;
    if (null != pi) {
        String genUri = pi.getUrl();
        String genName = pi.getName();
        String genVersion = pi.getShortVersion();
        %><atom:generator uri="<%= genUri %>" version="<%= genVersion%>"><%= genName %></atom:generator><%
    }

    //com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(slingRequest);
    Iterator<Page> i = list.getPages();
    while (i.hasNext()) {
        final Page p = i.next();
        String path = p.getPath() + ".feedentry";
        if(list.getType() == com.day.cq.wcm.foundation.List.SOURCE_CHILDREN){
            if(p.isHideInNav()){
                continue;
            }
        }

        Page entryPage = p;
        ValueMap entryProps = entryPage.getProperties();

        //Externalizer externalizer = sling.getService(Externalizer.class);
        String itemurl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), entryPage.getPath());

        String itemlink = itemurl + ".html";
//        String itemtitle = entryPage.getTitle() !=null ?
//                entryPage.getTitle() : currentNode.getName();

        String itemtitle =
                htmlToXmlEntities(
                        StringEscapeUtils.escapeHtml4(getPageTitle(entryPage))
                );

        String desc =
                htmlToXmlEntities(
                        StringEscapeUtils.escapeHtml4(getPageDescription(entryPage))
                );

//        String desc = entryPage.getDescription() != null ?
//                entryPage.getDescription() : (String)properties.get("jcr:description", null);
        Date udate = entryPage.getLastModified() != null ?
                entryPage.getLastModified().getTime() : entryProps.get("jcr:created", Date.class);

        Date pdate = entryProps.get("cq:lastReplicated", udate);
        String authorName = entryPage.getLastModifiedBy() != null ?
                entryPage.getLastModifiedBy() : entryProps.get("jcr:createdBy", "unknown");
        String authorEmail = "noemail@noemail.org";
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
                id="<%= itemurl %>"
                updated="<%= udate %>"
                published="<%= pdate %>"><%
        %><atom:title><%= xssAPI.encodeForXML(itemtitle) %></atom:title><%
        %><atom:link href="<%= itemlink %>"/><%
        %><atom:author name="<%= authorName %>" email="<%= authorEmail %>"/><%

            if (mediaPars.size() > 0) {
                for (Paragraph par : mediaPars) {
                    Download dld = new Download(par);
                    itemurl = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), dld.getHref());
        %><atom:link
                href="<%= itemurl %>"
                rel="enclosure"
                type="<%= dld.getMimeType() %>"/><%
                }
            }

            if (contentPars.size() > 0) {
        %><atom:content><%
            int ci = 0;
            for (Paragraph par : contentPars) {
                String cpath = par.getPath() + ".html";
        %><sling:include path="<%= cpath %>"/><%
                if (++ci == 4) {
                    break;
                }
            }
        %></atom:content><%
            }

            for (Tag tag : tags) {
        %><atom:category term="<%= tag.getTitle() %>"/><%
            }

            if (desc != null) {
        %><atom:summary><%= xssAPI.encodeForXML(desc) %></atom:summary><%
            }

        %></atom:entry><%


    }

    %></atom:feed><%

} catch (Exception e) {
    log.error("error rendering feed for list", e);
}
%>