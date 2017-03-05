<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="contentblockmenudata.jsp" %>



<%
    Map<String, String> contentMenu;
    //display mode

    String displayMode = _properties.get("displayType", "page");
    if(displayMode.equalsIgnoreCase("relative"))
    {
        //relative
        contentMenu =  this.getContentBlockMenu( resource.getParent());
    }
    else {
        //page parsys (default)
        contentMenu =  this.getContentBlockMenu( _currentPage.getContentResource("par"));
    }


    boolean hideBullets = _properties.get("hideBullets", false);
    boolean horizontalMenu = _properties.get("horizontalMenu", false);
    boolean tabsMenu = _properties.get("tabsMenu", false);
    boolean verticalMenu = !horizontalMenu && !tabsMenu;

    String cssClass = _properties.get("cssClass", "");

    String pageType = getPageTitle(_currentPage, "");

%>

<c:choose>

    <%-- When there is a contentmenu and it's not empty, show the list --%>
    <c:when test="<%= contentMenu != null && !contentMenu.isEmpty() %>">

        <c:set var="counter" value="1"  />
        <c:set var="cstring" value="current-section"  />

        <nav class="<%= cssClass %>">
            <c:choose>

                <%-- vertical menu display variation --%>
                <c:when test="<%= verticalMenu %>">
                    <ul></ul>
                </c:when>

                <%-- horizontal menu display variation --%>
                <c:when test="<%= horizontalMenu %>">
                    <p class="horizontalMenu">
                        <c:forEach items="<%= contentMenu.entrySet() %>" var="entry" varStatus="entryStatus">
                            <a href="#${entry.key}" title="Go to ${entry.value}">${entry.value}</a>
                            <c:if test="${!entryStatus.last}"><span>|</span></c:if>
                        </c:forEach>
                    </p>
                </c:when>
                <c:when test="<%= tabsMenu %>">
                    <ul class="tabs__nav">
                        <c:forEach items="<%= contentMenu.entrySet() %>" var="entry" varStatus="entryStatus">
                            <li id="${entry.key}" class="tab"> <a>${entry.value}</a> </li>
                        </c:forEach>
                    </ul>
                </c:when>
            </c:choose>
            <a class="fixed-top-link" href="#"><span></span>Back to top</a>
        </nav>

    </c:when>

    <%-- issue warning that this component cannot be displayed --%>
    <c:otherwise>
        <p class="cq-info">No content block components available on this page</p>
    </c:otherwise>
</c:choose>
<script>document.cookie = "carType=<%=pageType%>;path=/";</script>

<%@include file="/apps/aemdesign/global/component-badge.jsp" %>