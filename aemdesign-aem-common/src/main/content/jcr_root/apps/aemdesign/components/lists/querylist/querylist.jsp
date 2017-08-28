<%@page session="false"%><%@ page import="com.day.cq.wcm.api.components.DropTarget"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="querylistdata.jsp" %>

    <%

        String listClass = _properties.get("cssClass", (String) null);

        boolean cssIncludeTags =  _properties.get("cssIncludeTags", false);
        if(cssIncludeTags){
            //there are 3 different types of tag properties on the querylist type components.
            String tagProperties[] = new String[]{"tags", "pageTags", "newsTags"};
            String cssTagClass = "";
            for(int j=0;j<tagProperties.length;j++){
                String tag[] = _properties.get(tagProperties[j], new String[] {});
                for(int i = 0; i < tag.length; i++) {
                    if(cssTagClass.length() > 0){cssTagClass = cssTagClass + " ";}
                    cssTagClass = cssTagClass + tag[i].substring(tag[i].lastIndexOf(":")+1);
                }
            }
            listClass = listClass != null? listClass + " " + cssTagClass:cssTagClass;
        }

        boolean splitList = _properties.get("splitList", false);

        Map<String, Object> gallery = new HashMap<String, Object>();
        gallery.put("galleryHeight", _properties.get("galleryHeight", _currentStyle.get("galleryHeight", (String) "1")));
        gallery.put("galleryMainImageHeight", _properties.get("galleryMainImageHeight", _currentStyle.get("galleryMainImageHeight", (String) "350")));
        gallery.put("galleryMainImageWidth", _properties.get("galleryMainImageWidth", _currentStyle.get("galleryMainImageWidth", (String) "620")));

        gallery.put("gallerySpeed", _properties.get("gallerySpeed", _currentStyle.get("gallerySpeed", (String) "2100")));
        gallery.put("galleryAutoPlay", Boolean.parseBoolean(_properties.get("galleryAutoPlay", _currentStyle.get("galleryAutoPlay", (String) "true"))));
        gallery.put("galleryAutoPlayInterval", _properties.get("galleryAutoPlayInterval", _currentStyle.get("galleryAutoPlayInterval", (String) "5")));

        gallery.put("galleryAutoPlayValue",gallery.get("galleryAutoPlay"));
        if (Boolean.TRUE.equals(gallery.get("galleryAutoPlay"))) {
            if (gallery.get("galleryAutoPlayInterval").toString()!="") {
                gallery.put("galleryAutoPlayValue",gallery.get("galleryAutoPlayInterval"));
            }
        }

        gallery.put("galleryTransition", _properties.get("galleryTransition", _currentStyle.get("galleryTransition", (String) "fade")));
        gallery.put("galleryThumbnails", Boolean.parseBoolean(_properties.get("galleryThumbnails", _currentStyle.get("galleryThumbnails", (String) "empty"))));
        gallery.put("galleryShow", _properties.get("galleryShow", _currentStyle.get("galleryShow", (String) "0")));
        gallery.put("galleryMouseOverNav", Boolean.parseBoolean(_properties.get("galleryMouseOverNav", _currentStyle.get("galleryMouseOverNav", (String) "false"))));
        gallery.put("galleryResponsive", Boolean.parseBoolean(_properties.get("galleryResponsive", _currentStyle.get("galleryResponsive", (String) "false"))));

        String listType = _properties.get("listType", (String) "list");



    %>
    <c:set var="gallery" value="<%= gallery %>" />
    <%

        if (CURRENT_WCMMODE == WCMMode.EDIT) {
            //drop target css class = dd prefix + name of the drop target in the edit config
            String ddClassName = DropTarget.CSS_CLASS_PREFIX + "pages";
            %><div class="<%= ddClassName %>"><%
        }

        boolean feedEnabled = _properties.get("feedEnabled", false);
        String feedType = _properties.get("feedType", "rss");

        if (feedEnabled) {
            if (feedType == "atom") {
            %><link rel="alternate" type="application/atom+xml" title="Atom 1.0 (List)" href="<%= _resource.getPath() %>.feed" /><%
            } else {
            %><link rel="alternate" type="application/rss+xml" title="RSS Feed" href="<%= _resource.getPath() %>.rss"/><%
            }
        }

        try {
            WCMMode.DISABLED.toRequest(request);
            %><cq:include script="init.jsp" /><%
        }
        catch (Exception ex) {
            if (!"JspException".equals(ex.getClass().getSimpleName())) {
                throw ex;
            }
            %><p class="cq-error">List initialize error.</p><%
        }
        finally {
            CURRENT_WCMMODE.toRequest(request);
        }

        //open wrap list
        if (listType.equals("list")) {
            %>
            <div class="<%= listClass != null ? listClass : "" %>">
            <%
        } else {
            %>
            <cq:include script="galleryShow.jsp" />
            <%
            String testx = (String)request.getAttribute("galleriaShowData");
            %>
            <div class="galleria <%= listClass != null ? listClass : "" %>"
                 data-height="${gallery.galleryHeight}"
                 data-speed="${gallery.gallerySpeed}"
                 data-autoplay="${gallery.galleryAutoPlayValue}"
                 data-transition="${gallery.galleryTransition}"
                 data-thumbnails="${gallery.galleryThumbnails}"
                 data-show="<%=testx%>"
                 data-responsive="${gallery.galleryResponsive}"
                 data-mouseover="${gallery.galleryMouseOverNav}">
                <%
        }

        com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List)request.getAttribute("list");

        String title = _properties.get("title", "");
        boolean hasBoxTitle = _properties.get("showTitle", false) && !"".equals(title.trim());


                    // Print out the heading
        if (hasBoxTitle && (!list.isEmpty() || WCMMode.EDIT == CURRENT_WCMMODE)) {

            String cssClass = _properties.get("cssClass", (String) null);
            String titleLink = _properties.get("titleLink", (String) null);
            String imageLink = _properties.get("imageLink", (String) null);
            Page imagePage = _pageManager.getPage(imageLink);
            String imageSource = null;
            if (imagePage != null) {
                imageSource = imagePage.getProperties().get("image/fileReference", (String) null);
            }
            %>

            <!--div class="<%= cssClass != null ? cssClass : "" %>"-->
                <c:choose>
                    <%-- when there is a link but no image --%>
                    <c:when test="<%= titleLink != null && imageSource == null %>">
                        <h2>
                            <a href="<%= titleLink.concat(DEFAULT_EXTENTION) %>"><%= escapeBody(title) %></a>
                        </h2>
                    </c:when>

                    <%-- when there is a link and image --%>
                    <c:when test="<%= titleLink != null && imageSource != null %>">
                        <h2>
                            <a href="<%= titleLink.concat(DEFAULT_EXTENTION) %>"><%= escapeBody(title) %></a>
                            <a href="<%= imagePage.getPath().concat(DEFAULT_EXTENTION) %>">
                                <img src="<%= imageSource %>" alt="<%= escapeBody(getPageTitle(imagePage)) %>" />
                            </a>
                        </h2>
                    </c:when>

                    <%-- any other time --%>
                    <c:otherwise>
                        <h2><%= escapeBody(_properties.get("title", "")) %></h2>
                    </c:otherwise>
                </c:choose>
            <!--/div-->
        <% } %>

        <%
        if (!list.isEmpty()) {
            String cls = list.getType();
            cls = (cls == null) ? "" : cls.replaceAll("/", "");

            // clear
            String listStartTag =
                    (list.isOrdered() ? "<ol " : "<ul ") +
                    "class=\"list_row " + (splitList ? "split-list " : "") + _xssAPI.encodeForHTML(cls) + "\">";

            String listEndTag = list.isOrdered() ? "</ol>" : "</ul>";

            //open wrap list items
            if (listType.equals("list")) {
                %><%= listStartTag %><!--X7b--><%
            }

            Iterator<Page> items = list.getPages();
            String listItemClass = null;
            int itemNumber = 0;
            while (items.hasNext()) {
                Page listItem = items.next();
                request.setAttribute("badgePage", listItem);

                if (null == listItemClass) {
                    listItemClass = "first";
                } else if (!items.hasNext()) {
                    listItemClass = "last";
                } else {
                    listItemClass = "item";
                }
                boolean showRedirectIcon =  _properties.get("redirectIcon", false);
                String redirectLink = StringUtils.defaultString((String) listItem.getProperties().get("redirectTarget"), "");
                if(showRedirectIcon && !StringUtils.isEmpty(redirectLink)){
                    listItemClass = listItemClass + " redirectLink";
                }
                
                request.setAttribute("hideThumbnail", _properties.get("hideThumbnail", Boolean.FALSE));

                String badgeSelector = _properties.get(FIELD_VARIANT, DEFAULT_VARIANT);
                String script = getPageBadgeBase(listItem) + String.format("badge.%s.jsp", badgeSelector);

                //open wrap list item
                if (listType.equals("list")) {
                    %><li class="<%= listItemClass %>"><%
                } else {
                    %><div class="image"><%
                }
                %>
                    <%
                        try {
                            WCMMode.DISABLED.toRequest(request);
                            %><cq:include script="<%= script %>" /><%
                        }
                        catch (Exception ex) {
                            if (!"JspException".equals(ex.getClass().getSimpleName())) {
                                throw ex;
                            }
                            %><p class="cq-error">Variation not found for this content type</p><%
                        }
                        finally {
                            CURRENT_WCMMODE.toRequest(request);


                        }
                //close wrap list item
                if (listType.equals("list")) {
                    %></li><%
                } else {
                    %></div><!--X5--><%
                }

                ++itemNumber;

                // should insert cleared.
                if (itemNumber % 2 == 0 && items.hasNext() && splitList) {

                    if (listType.equals("list")) {
                    %>
                    <%= listEndTag %><!--X3-->
                    <%= listStartTag %><!--X4-->
                    <%
                    }
                }
            }

            //close wrap list items
            if (listType.equals("list")) {
                %><%= listEndTag %><!--X7a--><%
            }

            //show link at bottom of list
            Boolean showListLink = _properties.get("showListLink",  _currentStyle.get("showListLink", (Boolean) false));
            if (showListLink) {

                String searchIn = _properties.get("searchIn", (String) null);
                String linkTitle = _properties.get("listLinkTitle", _currentStyle.get("listLinkTitle", (String) "More"));
                String linkPath = _properties.get("listLinkPath", _currentStyle.get("listLinkPath", searchIn));
                String linkText = _properties.get("listLinkText", _currentStyle.get("listLinkText", (String) "More"));

                %><a href="<%= linkPath %>" title="<%= escapeBody(linkTitle) %>" class="linkMore"><%= escapeBody(linkText) %></a><%
            }

            //show pagination
            if (list.isPaginating()) {
                try {
                    WCMMode.DISABLED.toRequest(request);
                    %><cq:include script="pagination.jsp" /><%
                }
                catch (Exception ex) {
                    if (!"JspException".equals(ex.getClass().getSimpleName())) {
                        throw ex;
                    }
                    %><p class="cq-error">Variation not found for this content type</p><%
                }
                finally {
                    CURRENT_WCMMODE.toRequest(request);


                }
            }
        } else {
            %><cq:include script="empty.jsp"/><%
        }
        //close wrap list
        if (listType.equals("list")) {
            %></div><!--X2--><%
        } else {
            %></div><!--X1--><%
        }
        //clsoe edit mode
        if (CURRENT_WCMMODE == WCMMode.EDIT) {
        %></div><!--X6--><%
        }
    %>
    <c:if test="<%= CURRENT_WCMMODE == WCMMode.EDIT %>">
        <%
            Map<String, Object> debug = new HashMap<String, Object>();

            debug.put("name", _component.getProperties().get(JcrConstants.JCR_TITLE,""));


            debug.put("listFrom", _properties.get("listFrom", ""));
            debug.put("orderBy", _properties.get("orderBy", ""));
            debug.put("variant", _properties.get(FIELD_VARIANT, ""));
            debug.put("searchIn", _properties.get("searchIn", ""));
            debug.put("pageTags", _properties.get("pageTags", ""));
            debug.put("newsTags", _properties.get("newsTags", ""));

            if (feedEnabled) {
                debug.put("feedEnabled", true);
                if (feedType == "atom") {
                    debug.put("feedURL", _resource.getPath() + ".feed");
                } else {
                    debug.put("feedURL", _resource.getPath() + ".rss");
                }
            }

        %>
        <c:set var="debug" value="<%= debug %>" />
        <p class="cq-info">
        <small>Component: ${debug.name}</small>;
        <small>Type: ${debug.listFrom}</small>;
        <small>OrderBy: ${debug.orderBy}</small>;
        <small>Variant: ${debug.variant}</small>;
            <small>Page Tags: ${debug.pageTags}</small>;
            <small>News Tags: ${debug.newsTags}</small>;
            <small>Search In: ${debug.searchIn}</small>;
            <c:if test="${debug.feedEnabled}">
                <small>Feed URL: ${debug.feedURL}</small>
            </c:if>
        </p>
    </c:if>

