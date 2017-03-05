<%@ page import="com.day.cq.commons.Externalizer"%>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.MessageFormat" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/components/lists/list/listData.jsp" %>

<%
    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List) request.getAttribute("list");

    int totalSize = list.size();
    int pageStart =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getPageStart();
    int pageMax =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getPageMaximum();
    int pageNumber = pageStart / pageMax + 1;
    String pageType =  ((com.day.cq.wcm.foundation.List)request.getAttribute("list")).getType();
    String componentPath = StringUtils.EMPTY;
    String prefix = StringUtils.EMPTY;
    if ( list.isEmpty() == false && (totalSize - pageNumber * pageMax) > 0) {

        componentPath = getComponentPath(_resourceResolver, _componentContext);

        prefix = generatePrefix(_componentContext, _currentPage);
        int nextStart = pageStart + pageMax;

        _log.debug("pageNumber: "+pageNumber+" pageStart : " + pageStart + "  pageMax : " + pageMax + " totalSize :  " + totalSize + " pageType " + pageType + " nextStart : " + nextStart);
        Map<String, String> queryStringMap = new HashMap<String, String>();
        queryStringMap.put(prefix + "_" + com.day.cq.wcm.foundation.List.PAGE_START_PARAM_NAME, String.valueOf(nextStart));
        //reset the page property
        queryStringMap.put(prefix + "_" + com.day.cq.wcm.foundation.List.PAGE_MAX_PARAM_NAME, _properties.get(com.day.cq.wcm.foundation.List.PAGE_MAX_PROPERTY_NAME, "-1"));
        queryStringMap.put("printStructure", "true");

        Enumeration para = _slingRequest.getParameterNames();
        while(para.hasMoreElements()){
            String paramName=(String)para.nextElement();
            String[] values=request.getParameterValues(paramName);
            for(int i=0;i<values.length;i++){
                if (paramName.startsWith(prefix) == false){
                    queryStringMap.put(paramName, values[i] );
                }
            }
        }
        String queryString = generateQueryString(queryStringMap);

        componentPath += "?";

        componentPath += queryString;

     }

    ComponentProperties componentProperties = new ComponentProperties();
    componentProperties.put("totalSize", totalSize);
    componentProperties.put("pageStart", pageStart);
    componentProperties.put("pageMax", pageMax);
    componentProperties.put("pageType", pageType);
    componentProperties.put("componentPath", componentPath);
    componentProperties.put("prefix", prefix);
    componentProperties.put("loadMoreNews", _i18n.get("loadMoreNewsText",  "newslist"));

%>
<c:set var="componentProperties" value="<%= componentProperties %>"/>

    <div class="page_scrolling wrapper" id="${componentProperties.prefix}">
        <a href="${componentProperties.componentPath}" title="<c:out value="${componentProperties.loadMoreNews}"/>"
           onclick="return WKCD.components.list.linkClick(this, '${componentProperties.prefix}');
                   <%--CQ_Analytics.record({event: 'loadMorePages', values: { listPageStart: '0' },collect:  false, options: { obj: this }, componentPath: 'wcm\/foundation\/components\/list>'});--%>">
            ${componentProperties.loadMoreNews}
        </a>
    </div>
