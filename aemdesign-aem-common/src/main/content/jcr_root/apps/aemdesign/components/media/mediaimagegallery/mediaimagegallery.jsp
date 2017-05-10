<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/images.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="/apps/aemdesign/global/media.jsp" %>
<%@ include file="gallerydata.jsp" %>
<%@ include file="/apps/aemdesign/components/layout/navbar/mainnavdata.jsp" %>

<%
    // init
    String galleryPath = _properties.get("galleryLocation", (String) null);
    String galleryType = _properties.get("galleryType", "");
    String showPrices = _properties.get("showPrices", "");
    String sliderModules = properties.get("sliderModules", StringUtils.EMPTY);

    List<Map> pictures = null;

    if (galleryPath != null) {

        Resource gallery = _resourceResolver.getResource(galleryPath);
        if (gallery != null) {
            pictures = this.getPicturesFromResource(_resourceResolver, gallery);
        }
    }

    List<Tag> sortTags = getTags(_tagManager,_currentNode,"sortTags");
    List<Tag> filterTags = getTags(_tagManager,_currentNode,"filterTags");
    List<Tag> dropdownFilterValues = getTags(_tagManager, _currentNode, "dropdownFilterTags");
    List<Tag> namespaceFilterTags = new ArrayList<Tag>();

    if(dropdownFilterValues != null) {
        for (Tag t : dropdownFilterValues) {
            if (!namespaceFilterTags.contains(t.getNamespace())) {
                namespaceFilterTags.add(t.getNamespace());
            }
        }
    }

    // pull in the list of pages
    List<Map> tabPagesInfo=null;
    String[] menuPages =null;
    if ( galleryType.equals("pageGallery") ){

        menuPages = _properties.get("menuPages", new String[0]);
        tabPagesInfo = this.getMenuPageList(_pageManager, menuPages);

        //out.write(tabPagesInfo.toString());
    }

    Map<String, Object> gallery = new HashMap<String, Object>();
    gallery.put("listClass", _properties.get("cssClass", (String) null));
    gallery.put("sortTags", sortTags);
    gallery.put("filterTags", filterTags);
    gallery.put("dropdownFilterTags", dropdownFilterValues);
    gallery.put("namespaceFilterTags", namespaceFilterTags);
    gallery.put("galleryLocation", galleryPath);
    gallery.put("galleryType", galleryType);
    gallery.put("showPrices", showPrices);


%>
<%!
    //String galleryTheme = mappedUrl(GALLERIA_THEME);
    public Object getComponentProperty(PageContext pageContext, String name, Object defaultValue) {
        if (pageContext == null) {
            return "";
        }
        return getComponentProperty(pageContext, name, defaultValue, false);
    }

    /**
     * Read properties for the Component, use component style to override properties if they are not set
     * @param pageContext current page context
     * @param name name of the property
     * @param defaultValue default value for the property
     * @param useStyle use styles properties if property is missing
     * @return
     */
    public Object getComponentProperty(PageContext pageContext, String name, Object defaultValue, Boolean useStyle) {
        //quick fail
        if (pageContext == null) {
            return "";
        }

        ValueMap properties = (ValueMap) pageContext.getAttribute("properties");

        if (useStyle) {
            Style currentStyle = (Style) pageContext.getAttribute("currentStyle");

            return properties.get(name, currentStyle.get(name, defaultValue));
        } else {

            return properties.get(name, defaultValue);
        }
    }
%>

<c:set var="gallery" value="<%= gallery %>" />
<c:set var="menuItems" value="<%= tabPagesInfo %>" />

<c:choose>
    <%-- no pictures --%>
    <c:when test="<%= pictures == null || pictures.size() == 0 %>">
        <p class="cq-info">
            Please configure the gallery component to point at a folder with pictures.
        </p>
    </c:when>


    <%-- main showroom gallery, at top of the page --%>
    <c:when test="<%= galleryType.equals("showRoomGallery") %>">
        <div data-modules='gallery' class="hero-360" data-animation-speed="500" data-namespace="hero-360-">
            <ul class="slides" >

                <c:set var="cstring" value="hero-360-active-slide"  />
                <c:set var="opacity" value="1"  />
                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li style="width: 100%; float: left; margin-right: -100%; position: relative; display: block; z-index: 1; opacity:${opacity}; height: 365px;" class="${cstring}">
                        <img alt="" src="${picture.image}" draggable="false" style="height: 365px; width: 700px;">
                    </li>

                    <c:set var="cstring" value=""  />
                    <c:set var="opacity" value="0"  />
                </c:forEach>
            </ul>
            <ol class="hero-360-control-nav hero-360-control-paging">


                <c:set var="counter" value="1"  />
                <c:set var="cstring" value="hero-360-active"  />

                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li><a class="${cstring}">${counter}</a></li>

                    <c:set var="counter" value="${counter + 1}" />
                    <c:set var="cstring" value=""  />

                </c:forEach>
            </ol>
            <ul class="hero-360-direction-nav"><li><a href="#" class="hero-360-prev">Previous</a></li><li><a href="#" class="hero-360-next">Next</a></li></ul>

        </div>

    </c:when>


    <%-- magic seats gallery --%>
    <c:when test="<%= galleryType.equals("showRoomGalleryTiny") %>">
        <div data-animation-speed="500" data-namespace="magic-seats-" class="magic-seats">
            <ul class="slides">
                <c:set var="cstring" value="magic-seats-active"  />
                <c:set var="opacity" value="1"  />

                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">

                    <li style="width: 100%; float: left; margin-right: -100%; position: relative; display: block; z-index: 2; opacity: ${opacity};" class="${cstring}">
                        <img alt="" src="${picture.thumbnail}" draggable="false" width="1710" height="700">
                    </li>

                    <c:set var="cstring" value=""  />
                    <c:set var="opacity" value="0"  />
                </c:forEach>


            </ul>
            <ul class="magic-seats-direction-nav"><li><a href="#" class="magic-seats-prev magic-seats-disabled" tabindex="-1">Previous</a></li><li><a href="#" class="magic-seats-next">Next</a></li></ul>
        </div>
        <div class="flexslider-controls">
            <h5 class="small-module-heading">Magic Seat Modes</h5>
            <ol class="flex-control-nav">

                <c:set var="cstring" value="magic-seats-active"  />
                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li class="${cstring}"><img alt="" src="${picture.thumbnail}" width="92" height="92"><span>${picture.title}</span></li>

                    <c:set var="cstring" value=""  />
                    <c:set var="opacity" value="0"  />
                </c:forEach>
            </ol>
        </div>
    </c:when>


    <%-- cars landing gallery --%>
    <c:when test="<%= galleryType.equals("carLandingGallery") %>">
        <div class="hero-carousel" data-namespace="hero-carousel__" data-animation="slide" data-modules="Slider">
            <div class="hero-carousel__viewport" style="overflow: hidden; position: relative;">
                <ul class="slides" >

                    <c:if test="${not empty menuItems}">

                        <c:forEach items="${menuItems}" var="link">
                        <h1>${link.title}</h1>
                        <c:if test="${not empty link.parsysPath}">
                            <li>
                                    <%

                                        String defDecor =_componentContext.getDefaultDecorationTagName();

                                        disableEditMode(_componentContext, IncludeOptions.getOptions(request, true), _slingRequest);

                                        String path = resource.getPath();
                                        String key = "apps.aemdesign.components.default.mainnav" + path;
                                            try {
                                                if (request.getAttribute(key) == null || Boolean.FALSE.equals(request.getAttribute(key))) {
                                                    request.setAttribute(key,Boolean.TRUE);
                                                } else {
                                                    throw new IllegalStateException("Reference loop: " + path);
                                                }
                                        %>
                                        <cq:include path="${link.parsysPath}" resourceType="foundation/components/parsys" />
                                        <%
                                        } catch (Exception ex) {
                                            // Log errors always
                                            //log.error("Reference component error", e);
                                            if (CURRENT_WCMMODE == WCMMode.EDIT) {
                                        %><p class="cq-error">Content error (<%= xssAPI.encodeForHTML(ex.toString()) %>)</p><%
                                    }
                                }
                                finally {

                                    enableEditMode(CURRENT_WCMMODE, _componentContext, defDecor, IncludeOptions.getOptions(request, true), _slingRequest);

                                }
                                %>
                                </li>
                            </c:if>


                        </c:forEach>
                    </c:if>
                </ul>
            </div>

            <ol class="hero-carousel__control-nav hero-carousel__control-paging">
                    <li><a class="hero-carousel__active">1</a></li>
                    <li><a class="">2</a></li>
                    <li><a class="">3</a></li>
             </ol>
             <ul class="hero-carousel__direction-nav">
                    <li><a href="#" class="hero-carousel__prev">Previous</a></li>
                    <li><a href="#" class="hero-carousel__next">Next</a></li>
             </ul>

        </div>
    </c:when>



    <%-- cars landing gallery small images--%>
    <c:when test="<%= galleryType.equals("carLandingSmallGallery") %>">

            <div class="container">
                <ul class="promos sub">
                    <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                        <li><a style="background-image: url('${picture.thumbnail}')" href="#"><span>title</span></a></li>
                    </c:forEach>
                </ul>
            </div>

    </c:when>


    <%-- magic seats gallery --%>
    <c:when test="<%= galleryType.equals("showRoomGalleryMagicSeats") %>">
        <div data-animation-speed="500" data-namespace="magic-seats-" class="magic-seats">
            <ul class="slides" >

                <c:set var="cstring" value="magic-seats-active-slide"  />
                <c:set var="opacity" value="1"  />
                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li style="width: 100%; float: left; margin-right: -100%; position: relative; display: block; z-index: 2; opacity: 1;" class="${cstring}">
                        <img alt="" src="${picture.thumbnail}" draggable="false" style="height: 92px; width: 92px;">
                    </li>

                    <c:set var="cstring" value=""  />
                    <c:set var="opacity" value="0"  />
                </c:forEach>
            </ul>
            <ol class="hero-360-control-nav hero-360-control-paging">


                <c:set var="counter" value="1"  />
                <c:set var="cstring" value="hero-360-active"  />

                <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                    <li><a class="${cstring}">${counter}</a></li>

                    <c:set var="counter" value="${counter + 1}" />
                    <c:set var="cstring" value=""  />

                </c:forEach>
            </ol>
            <ul class="hero-360-direction-nav"><li><a href="#" class="hero-360-prev">Previous</a></li><li><a href="#" class="hero-360-next">Next</a></li></ul>

        </div>
    </c:when>


    <%-- main gallery on gallery page --%>
    <c:otherwise>
    <div data-page-type="model-showroom">
      <div id="accessories" >
        <div class="gallery-filters">
          <div class="wrapper">

                  <%-- dropdown filters --%>
           <c:if test="<%= namespaceFilterTags != null && namespaceFilterTags.size() > 0%>">
            <div class="filter-container float-left accessories-filter-container">
              <span class="filter-label">View By:</span>
             <c:forEach items="<%= namespaceFilterTags%>" var="tag" varStatus="tStatus">
                <div class="filter-select filter-select--packs js-filter-dropdown" data-filter-group="pack">
                    <span id="filtered-options" class="filter-selected-option">${tag.description}</span>
                      <div class="filter-options">
                          <a href="#"  class="filter-option js-isotope-filter" data-filter-value="">${tag.description}</a>
                          <c:forEach items="<%= dropdownFilterValues%>" var="filterValue" varStatus="fStatus">
                               <c:if test="${filterValue.description eq tag.description}">
                                    <a href="#"  class="filter-option js-isotope-filter" data-filter-value=".${fn:toLowerCase(filterValue.name)}">${filterValue.title}</a>
                               </c:if>
                        </c:forEach>
                      </div>
                 </div>
              </c:forEach>
                <a id="reset-packs" href="#"  class="js-isotope-filter selected" data-filter-value="">Reset</a>
            </div>
            </c:if>
            <c:if test="<%= sortTags != null && sortTags.size() > 0%>">
            <div class="filter-container float-left">
                <span class="filter-label gallery-container">Sort by:</span>
                <div class="filter-select" data-filter-group="sort-by">
                    <a href="#" class="filter-link js-isotope-filter" data-filter-value="">All</a>
                    <c:forEach items="<%= sortTags%>" var="tag" varStatus="tStatus">
                            <a href="#" class="filter-link js-isotope-filter" data-filter-value=".${fn:toLowerCase(tag.title)}">${tag.title}</a>
                    </c:forEach>
                </div>
            </div>
            </c:if>

            <div class="filter-container  gallery-right">
                <span class="filter-label">Filter by:</span>
                <div class="filter-select" data-filter-group="filter-by">
                    <c:forEach items="<%= filterTags%>" var="tag" varStatus="tStatus">
                            <a href="#"  class="filter-link icon-${fn:toLowerCase(tag.name)} js-isotope-filter" data-filter-value=".${fn:toLowerCase(tag.name)}">${tag.title}</a>
                    </c:forEach>
                    <a href="#"  class="filter-reset js-isotope-filter selected" data-filter-value="">View All</a>
                </div>
            </div>
          </div>
        </div>


            <div  class="gallery isotope">
                 <ul data-height="${gallery.galleryHeight}"
                     data-speed="${gallery.gallerySpeed}"
                     data-autoplay="${gallery.galleryAutoPlayValue}"
                     data-transition="${gallery.galleryTransition}"
                     data-thumbnails="${gallery.galleryThumbnails}"
                     data-show="${gallery.galleryShow}"
                     data-responsive="true"
                     data-mouseover="${gallery.galleryMouseOverNav}">

                     <div class="gallery-inner">
                        <c:forEach items="<%= pictures %>" var="picture" varStatus="pStatus">
                            <li>
                                <a rel="all" class="gallery-thumb image ${ picture.tags } isotope-item" href="${picture.image}"
                                   data-option-type="Accessory option" data-option-models="" data-disclaimer="" data-type="${picture.tags}" data-description="${fn:escapeXml(picture.description)}" data-title="${picture.title}">
                                    <img src=${picture.image} alt="${picture.title}"  class="gallery-thumb-img" />
                                    <div class="gallery-thumb-caption">
                                        <span class="gallery-thumb-title">${picture.title}  </span>
                                        <c:if test="${gallery.showPrices}">
                                            <hr>
                                            Price
                                        </c:if>
                                    </div>

                                    <div class="gallery-thumb-hover">
                                        <div class="gallery-thumb-hover-text">
                                                ${picture.description}

                                            <div class="gallery-thumb-disclaimer">
                                            </div>
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </c:forEach>
                     </div>
                    </ul>
            </div>
        </div>
    </div>
    </c:otherwise>

</c:choose>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>
