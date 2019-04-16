<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="listnavdata.jsp" %>
<%@ include file="data.jsp" %>

    <%

        Object[][] componentFields = {
            //list
            {"listFrom", "children"},
            //page
            {"searchInPath", StringUtils.EMPTY},
            {"filterPage", _currentPage.getPath()},
            //dam asset
            {"collection", StringUtils.EMPTY},
            {"assetViewerPagePath", _currentPage.getPath()},
            {"backPage", StringUtils.EMPTY},
            {"numOfFetchItems", 1},
            {"hiddenText","List Navigation is hidden"},
            {"missingFilterText","Missing Filter Page or Wrong Content Type"},
            {"noResultsText","No result found"},
        };

        ComponentProperties componentProperties = getComponentProperties(
                pageContext,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        Page searchInPath = _pageManager.getPage(componentProperties.get("searchInPath", String.class));
        Page filterPage = _pageManager.getPage(componentProperties.get("filterPage", String.class));
        int numOffetchItems = componentProperties.get("numOfFetchItems", Integer.class);


        String collection = componentProperties.get("collection", String.class);
        String assetViewerPagePath = componentProperties.get("assetViewerPagePath", String.class);


        List<String> nextPages = new ArrayList<String>();
        List<String> previousPages = new ArrayList<String>();

        try
        {

            Node filterDetailComponent = findDetailNode(filterPage);

            if (filterDetailComponent == null){
                _log.error("filterPage [" + filterPage.getPath() + "] does not contain detail components [" + NODE_DETAILS+"]");
                componentProperties.put("filterPage", StringUtils.EMPTY);
            }

            if ("children".equals(componentProperties.get("listFrom", String.class)) && componentProperties.get("searchInPath", String.class).isEmpty() == false && filterDetailComponent != null) {


                if (_log.isDebugEnabled()){
                    _log.debug("Search In Path : " + componentProperties.get("searchInPath"));
                    _log.debug("Filter Page  : " + componentProperties.get("filterPage"));
                }

                QueryBuilder queryBuilder = _sling.getService(QueryBuilder.class);

                javax.jcr.Session jcrSession = _slingRequest.getResourceResolver().adaptTo(javax.jcr.Session.class);

                List <Page> pageList = new ArrayList<Page>();
                //pageList = this.getPreviousPages(searchInPath, pageList, filterPage, numOffetchItems, queryBuilder, jcrSession, _log) ;
                //Collections.reverse(pageList);

                //pageList = this.getNextPages(searchInPath, pageList, filterPage, numOffetchItems, queryBuilder, jcrSession, _log) ;
                pageList= getPagesByPath(searchInPath, pageList, filterPage, queryBuilder, jcrSession, _log);

                for (int i = 0; i < pageList.size(); i++){

                    String p = pageList.get(i).getPath();
                    _log.info("ListNav - pageList index " + i + " :" + p);
                    if (p.equals(filterPage.getPath())){
                        String tmp = StringUtils.EMPTY;
                        //previous pages
                        if (i > 0){
                            tmp = getPageUrl(pageList.get(i-1));
                            previousPages.add(mappedUrl(_resourceResolver, tmp));
                            componentProperties.put("previousPages", previousPages);
                        }

                        //next pages
                        if (i < (pageList.size()-1)){
                            tmp = getPageUrl(pageList.get(i+1));
                            nextPages.add(mappedUrl(_resourceResolver, tmp));
                            componentProperties.put("nextPages", nextPages);

                        }
                    }

                }
                String searchInPage = componentProperties.get("searchInPath", String.class);
                if (_pageManager.getPage(searchInPage) != null){
                    searchInPage = mappedUrl(_resourceResolver, getPageUrl(_pageManager.getPage(searchInPage)));
                }

                componentProperties.put("backPage",  searchInPage);

            }else if ("collection".equals(componentProperties.get("listFrom", String.class)) && componentProperties.get("collection", String.class).isEmpty() == false) {

                if(StringUtils.isNotEmpty(collection))
                {
                    Resource listPathR = _resourceResolver.resolve(collection);

                    List<Map> listItems = null;

                    if (!ResourceUtil.isNonExistingResource(listPathR) && (listPathR.getResourceType().equals("dam/collection"))) {

                        listItems = getPicturesFromCollection(_sling, _resourceResolver, listPathR.getPath(), assetViewerPagePath, "montage.assetviewer");
                        componentProperties.put("listPathR",listItems);

                        for (int i = 0; i < listItems.size(); i++){

                            String p = listItems.get(i).get("href").toString();


                            if (StringUtils.contains(_slingRequest.getPathInfo(), p)){

                                //previous pages
                                if (i > 0){

                                    previousPages.add(listItems.get(i-1).get("href").toString());
                                    componentProperties.put("previousPages", previousPages);
                                }

                                //next pages
                                if (i < (listItems.size()-1)){
                                    nextPages.add(listItems.get(i+1).get("href").toString());
                                    componentProperties.put("nextPages", nextPages);

                                }

                            }

                        }

                    }

                }

                String backPage = componentProperties.get("backPage", String.class);
                if (_pageManager.getPage(backPage) != null){
                    backPage = mappedUrl(_resourceResolver, getPageUrl(_pageManager.getPage(backPage)));
                }

                componentProperties.put("backPage",  backPage);

            }

            componentProperties.put("prevLinkText", _i18n.get("prevLinkText", "listnav"));
            componentProperties.put("backLinkText", _i18n.get("backLinkText", "listnav"));
            componentProperties.put("nextLinkText", _i18n.get("nextLinkText", "listnav"));


        }catch (Exception e){
            out.write( Throwables.getStackTraceAsString(e) );
            _log.error("Failed to perform List Navigation "+e.getMessage(), e);
        }

    %>
<c:set var="componentProperties" value="<%= componentProperties %>"/>
<c:choose>
    <c:when test="${(fn:length(componentProperties.nextPages) > 0 || fn:length(componentProperties.previousPages) > 0)}">
        <%@include file="variant.default.jsp" %>
    </c:when>
    <c:otherwise>
        <%@ include file="variant.empty.jsp"  %>
    </c:otherwise>
</c:choose>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
