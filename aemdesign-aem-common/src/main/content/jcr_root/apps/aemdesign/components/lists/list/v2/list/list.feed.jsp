<%@ page session="false" %>
<%@ page import="com.adobe.granite.license.ProductInfo,
                 com.adobe.granite.license.ProductInfoService,
                 com.day.cq.wcm.foundation.Download" %>
<%@ page import="com.day.cq.wcm.api.*" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isEmpty" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.day.cq.search.QueryBuilder" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="com.day.cq.search.PredicateGroup" %>
<%@ page import="com.day.cq.search.PredicateConverter" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="design.aem.components.list.HitBasedPageIterator" %>
<%@ page import="com.day.cq.wcm.api.components.ComponentContext" %>
<%@ page import="com.day.cq.commons.Externalizer" %>
<%@ page import="org.apache.jackrabbit.vault.util.JcrConstants" %>
<%@ page import="static design.aem.utils.components.CommonUtil.getPageTitle" %>
<%@ page import="static design.aem.utils.components.ComponentsUtil.getPageDescription" %>
<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.wcm.foundation.Paragraph" %>
<%@ page import="java.util.*" %>
<%@ page import="com.day.cq.wcm.foundation.ParagraphSystem" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="atom" uri="http://sling.apache.org/taglibs/atom/1.0" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%
  if (!_properties.get("feedEnabled", false)) {
    response.sendError(404);
  }

  boolean showHidden = _properties.get("showHidden", false);

  com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, showHidden));

  //for Children list set the current page as the Parent Page if the property is not set
  if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, com.day.cq.wcm.foundation.List.SOURCE_CHILDREN))) {
    String parentPage = _properties.get(com.day.cq.wcm.foundation.List.PARENT_PAGE_PROPERTY_NAME, "");
    if (isEmpty(parentPage)) {
      list.setStartIn(_resource.getPath());
    }
  }
  //allow passing of simple list query
  if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
    if (_slingRequest.getRequestParameter("q") != null) {
      String escapedQuery = _slingRequest.getRequestParameter("q").toString();
      list.setQuery(escapedQuery);
    }
  }
  //allow passing of querybuilder queries
  if (com.day.cq.wcm.foundation.List.SOURCE_QUERYBUILDER.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
    if (_slingRequest.getRequestParameter("q") != null) {
      String escapedQuery = _slingRequest.getRequestParameter("q").toString();
      try {
        String unescapedQuery = URLDecoder.decode(escapedQuery, "UTF-8");
        QueryBuilder queryBuilder = _resourceResolver.adaptTo(QueryBuilder.class);
        PageManager pm = _resourceResolver.adaptTo(PageManager.class);
        //create props for query
        java.util.Properties props = new java.util.Properties();
        //load query candidate
        props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));
        //create predicate from query candidate
        PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
        boolean allowDuplicates = _properties.get("allowDuplicates", false);
        javax.jcr.Session jcrSession = _slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);
        Query query = queryBuilder.createQuery(predicateGroup, jcrSession);
        if (query != null) {
          SearchResult result = query.getResult();
          HitBasedPageIterator newList = new HitBasedPageIterator(pm, result.getHits().iterator(), !allowDuplicates, new PageFilter(false, showHidden));
          list.setPageIterator(newList);
        }
      } catch (Exception ex) {
        _log.error("error using querybuilder with query [{}]. {}", escapedQuery, ex);
      }
    }
  }

  request.setAttribute("list", list);

  try {
    WCMMode.DISABLED.toRequest(request);
    request.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);

    Externalizer externalizer = sling.getService(Externalizer.class);
    String url = externalizer.absoluteLink(slingRequest, slingRequest.getScheme(), currentPage.getPath());

    String link = url + ".feed";
    String title = currentPage.getTitle() != null ? currentPage.getTitle() : currentNode.getName();
    String description = currentPage.getDescription() != null ? currentPage.getDescription() : (String) properties.get(JcrConstants.JCR_DESCRIPTION, null);
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
%><atom:generator uri="<%= genUri %>" version="<%= genVersion%>"><%= genName %>
</atom:generator><%
  }

  //com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(slingRequest);
  Iterator<Page> i = list.getPages();
  while (i.hasNext()) {
    final Page p = i.next();
    String path = p.getPath() + ".feedentry";
    if (list.getType() == com.day.cq.wcm.foundation.List.SOURCE_CHILDREN) {
      if (p.isHideInNav()) {
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

    String itemtitle = getPageTitle(entryPage);

    String desc = getPageDescription(entryPage);

//        String desc = entryPage.getDescription() != null ?
//                entryPage.getDescription() : (String)properties.get("jcr:description", null);
    Date udate = entryPage.getLastModified() != null ? entryPage.getLastModified().getTime() : entryProps.get(JcrConstants.JCR_TITLE, Date.class);

    Date pdate = entryProps.get("cq:lastReplicated", udate);
    String authorName = entryPage.getLastModifiedBy() != null ? entryPage.getLastModifiedBy() : entryProps.get(JcrConstants.JCR_CREATED_BY, "unknown");
    String authorEmail = "noemail@noemail.org";
    Tag[] tags = entryPage.getTags();

    String[] contentTypes = new String[]{"title", "text", "image", "chart", "table"};
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
    } catch (Exception e) {
    }

%><atom:entry
  id="<%= itemurl %>"
  updated="<%= udate %>"
  published="<%= pdate %>"><%
%><atom:title><%= xssAPI.encodeForXML(itemtitle) %>
</atom:title><%
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
%><atom:summary><%= xssAPI.encodeForXML(desc) %>
</atom:summary><%
  }

%></atom:entry><%


  }

%></atom:feed><%

  } catch (Exception e) {
    log.error("error rendering feed for list", e);
  }
%>
