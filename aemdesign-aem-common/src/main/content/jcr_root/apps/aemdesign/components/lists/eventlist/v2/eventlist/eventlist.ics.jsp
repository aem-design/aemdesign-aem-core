<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ page import="com.day.cq.search.PredicateConverter" %>
<%@ page import="com.day.cq.search.PredicateGroup" %>
<%@ page import="com.day.cq.search.Query" %>
<%@ page import="com.day.cq.search.QueryBuilder" %>
<%@ page import="com.day.cq.search.result.SearchResult" %>
<%@ page import="com.day.cq.wcm.api.PageFilter" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isEmpty" %>
<%@ page import="design.aem.components.list.HitBasedPageIterator" %>
<%@ page import="org.apache.jackrabbit.JcrConstants" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION" %>
<%@ page import="static design.aem.utils.components.ResolverUtil.mappedUrl" %>
<%@ page import="static design.aem.utils.components.CommonUtil.getPageTitle" %>
<%@ page import="static design.aem.utils.components.CommonUtil.*" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.*" %>
<%@ page import="static design.aem.utils.components.ComponentsUtil.*" %>
<%@ page import="static design.aem.utils.components.ComponentsUtil.*" %>
<%@ page import="static design.aem.utils.components.ComponentDetailsUtil.getPageInfo" %>
<%@ page import="static design.aem.utils.components.ConstantsUtil.DEFAULT_ICS_DATE_FORMAT" %>
<%@ page import="static design.aem.utils.components.ConstantsUtil.*" %>
<%@ page import="design.aem.utils.components.TagUtil" %>
<%@ page import="org.apache.sling.api.resource.ResourceResolver" %>
<%@ page import="design.aem.services.ContentAccess" %>
<%@ page import="org.jsoup.nodes.Document" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.jsoup.safety.Whitelist" %>
<%@ page import="org.apache.commons.lang.WordUtils" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %><%


    boolean showHidden = _properties.get("showHidden", false);

    com.day.cq.wcm.foundation.List list = new com.day.cq.wcm.foundation.List(_slingRequest, new PageFilter(false, showHidden));

    //for Children list set the current page as the Parent Page if the property is not set
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, com.day.cq.wcm.foundation.List.SOURCE_CHILDREN))) {
        String parentPage = _properties.get(com.day.cq.wcm.foundation.List.PARENT_PAGE_PROPERTY_NAME,"");
        if (isEmpty(parentPage)) {
            list.setStartIn(_resource.getPath());
        }
    }
    //allow passing of simple list query
    if (com.day.cq.wcm.foundation.List.SOURCE_CHILDREN.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
        if (_slingRequest.getRequestParameter("q") !=null) {
            String escapedQuery = _slingRequest.getRequestParameter("q").toString();
            list.setQuery(escapedQuery);
        }
    }
    //allow passing of querybuilder queries
    if (com.day.cq.wcm.foundation.List.SOURCE_QUERYBUILDER.equals(_properties.get(com.day.cq.wcm.foundation.List.SOURCE_PROPERTY_NAME, ""))) {
        if (_slingRequest.getRequestParameter("q") !=null) {
            String escapedQuery = _slingRequest.getRequestParameter("q").toString();
            try {
                String unescapedQuery = URLDecoder.decode(escapedQuery,"UTF-8");
                QueryBuilder queryBuilder = (QueryBuilder)_resourceResolver.adaptTo(QueryBuilder.class);
                PageManager pm = (PageManager)_resourceResolver.adaptTo(PageManager.class);
                //create props for query
                java.util.Properties props = new java.util.Properties();
                //load query candidate
                props.load(new ByteArrayInputStream(unescapedQuery.getBytes()));
                //create predicate from query candidate
                PredicateGroup predicateGroup = PredicateConverter.createPredicates(props);
                boolean allowDuplicates = (Boolean)_properties.get("allowDuplicates", false);
                javax.jcr.Session jcrSession = _slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);
                Query query = queryBuilder.createQuery(predicateGroup,jcrSession);
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

    Map<String, Object> info = new HashMap<String, Object>();

    info.put("startDateStamp","");

    info.put("summary","");
    info.put("description",_currentPage.getDescription());
    info.put("urlID",""); //hostname/homepageUrl
    info.put("urlEvent",""); //hostname/matchURL
    info.put("location","");
//    info.put("startDateStamp",formatDate(startDate, FMT_ICAL_STAMP));
    info.put("endDateStamp","");
    info.put("eventCreatedStamp",_currentPage.getDescription());
    info.put("eventUpdatedStamp","");
    info.put("formattedTags",""); //tags for match


    //config
    String pageTitle = getPageTitle(_currentPage , _resource);
    String requestHostName = _slingRequest.getServerName();

    info.put("url",mappedUrl(_resourceResolver, _resource.getPath().concat(DEFAULT_EXTENTION)));
    info.put("link",mappedUrl(_resourceResolver, _resource.getPath() + ".ics"));
    info.put("title",pageTitle);
    info.put("subTitle",getPageDescription(_currentPage, _properties));

    info.put("timezoneId","Australia/Sydney");
    info.put("timezoneLocation","Australia/Sydney");
    info.put("timezoneUrl","http://tzurl.org/zoneinfo-outlook/Australia/Sydney");
    info.put("calId","-//"+requestHostName+"//"+pageTitle+"//EN");
    info.put("TZOFFSETFROM","+1100");
    info.put("TZOFFSETTO","+1000");
    info.put("TZNAME","AEST");
    info.put("TZDTSTART","19700405T030000");
    info.put("RRULE","FREQ=YEARLY;BYMONTH=4;BYDAY=1SU");


    info.put("TZ_DAYLIGHT",true);
    info.put("TZOFFSETFROM_DAYLIGHT","+1000");
    info.put("TZOFFSETTO_DAYLIGHT","+1100");
    info.put("TZNAME_DAYLIGHT","AEDT");
    info.put("DTSTART_DAYLIGHT","19701004T020000");

    //getPageInfo
    List<ComponentProperties> pages = new ArrayList<>();

    Iterator<Page> pageIterator = list.getPages();
    while (pageIterator.hasNext()) {
        Page listPage = pageIterator.next();
        ValueMap listPageVM = listPage.getProperties();
        Resource pageResource = _resourceResolver.getResource(listPage.getPath());

        ComponentProperties listPageCP = getPageInfo(getContextObjects(pageContext), listPage, resourceResolver, DEFAULT_LIST_DETAILS_SUFFIX, DEFAULT_LIST_PAGE_CONTENT, 0);

        String detailsPath = listPageCP.get("detailsPath","");
        Resource detailsComponent = _resourceResolver.getResource(detailsPath);
        if (detailsComponent != null) {
            ValueMap details = detailsComponent.adaptTo(ValueMap.class);

            listPageCP.put("author",details.get("author",""));
            listPageCP.put("eventStartDate",formatDate(details.get("eventStartDate",Calendar.getInstance()),DEFAULT_ICS_DATE_FORMAT));
            listPageCP.put("eventEndDate",formatDate(details.get("eventEndDate",Calendar.getInstance()),DEFAULT_ICS_DATE_FORMAT));
            listPageCP.put("eventLoc",details.get("eventLoc",""));

            String title = details.get("title","");
            listPageCP.put("title",WordUtils.wrap(title,50, "\r\n ", true));

            String description = details.get("description","");


            description = WordUtils.wrap(description,50, "\r\n ", true);
            listPageCP.put("description",description);

            String descritiptionHTML = description;
            String cleanText = Jsoup.clean(descritiptionHTML,"" ,Whitelist.none(),new Document.OutputSettings().outline(true));
            listPageCP.put("descriptionText", WordUtils.wrap(cleanText,50, "\r\n ", true));
            listPageCP.put("descriptionText2", WordUtils.wrap(cleanText.replaceAll("\n", "=0D=0A"),50, "\r\n ", true));

            descritiptionHTML = descritiptionHTML.replaceAll("\n","");
            descritiptionHTML = "\r\n " + WordUtils.wrap(descritiptionHTML,40, "\r\n", true);
            descritiptionHTML = descritiptionHTML.replaceAll("\n","\r\n ");


            listPageCP.put("descriptionHTML",descritiptionHTML);

            String categories = "";

            ContentAccess contentAccess = sling.getService(ContentAccess.class);
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
                categories = TagUtil.getTagsAsValues(_tagManager, adminResourceResolver, ",", (String[]) details.get("cq:tags", new String[]{}));
                if (categories!=null) {
                    listPageCP.put("categories", categories.toUpperCase());
                }
            }

        }

        Calendar lastCreated = listPageVM.get(JcrConstants.JCR_CREATED, Calendar.getInstance());
        Calendar lastModified = listPageVM.get(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());

        listPageCP.put("modified",formatDate(lastModified,DEFAULT_ICS_TIMESTAMP_FORMAT));
        listPageCP.put("created",formatDate(lastCreated,DEFAULT_ICS_TIMESTAMP_FORMAT));
        listPageCP.put("uid",hashMd5(listPage.getPath()));

        pages.add(listPageCP);
    }

    info.put("items",pages);


    //ical format reference https://www.kanzaki.com/docs/ical/

    if (!Arrays.asList(_slingRequest.getRequestPathInfo().getSelectors()).contains("test")) {
        response.setHeader("Content-Type", "text/calendar");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + _currentPage.getName() + ".ics\"");
    }

    pageContext.setAttribute("newLineChar", "\n");

%><c:set var="info" value="<%= info %>" /><c:set var="list" value="<%= list %>" />BEGIN:VCALENDAR
VERSION:2.0
PRODID:${info.calId}
CALSCALE:GREGORIAN
X-WR-CALNAME:${info.title}
METHOD:PUBLISH
BEGIN:VTIMEZONE
TZID:${info.timezoneId}
TZURL:${info.timezoneUrl}
X-LIC-LOCATION:${info.timezoneLocation}
BEGIN:STANDARD
TZOFFSETFROM:${info.TZOFFSETFROM}
TZOFFSETTO:${info.TZOFFSETTO}
TZNAME:${info.TZNAME}
DTSTART:${info.TZDTSTART}
RRULE:${info.RRULE}
END:STANDARD<c:if test="${info.TZ_DAYLIGHT}">
BEGIN:DAYLIGHT
TZOFFSETFROM:${info.TZOFFSETFROM_DAYLIGHT}
TZOFFSETTO:${info.TZOFFSETTO_DAYLIGHT}
TZNAME:${info.TZNAME_DAYLIGHT}
DTSTART:${info.DTSTART_DAYLIGHT}
RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=1SU
END:DAYLIGHT</c:if>
END:VTIMEZONE<c:forEach items="${info.items}" var="event">
BEGIN:VEVENT
LAST-MODIFIED:${event.modified}
CREATED:${event.created}
UID:${event.uid}
DTSTART;VALUE=DATE:${event.eventStartDate}
DTEND;VALUE=DATE:${event.eventEndDate}
LOCATION:${event.eventLoc}
ORGANISER:${event.author}
CONTACT:${event.author}
SUMMARY:${event.title}
DESCRIPTION:${fn:replace(event.descriptionText2, "=0D=0A", "\\n")}
X-ALT-DESC;FMTTYPE=text/html:${event.descriptionHTML}
CATEGORIES:${event.categories}
STATUS:CONFIRMED
END:VEVENT</c:forEach>
END:VCALENDAR