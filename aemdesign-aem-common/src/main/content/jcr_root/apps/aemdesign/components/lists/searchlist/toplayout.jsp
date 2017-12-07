<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="searchlistdata.jsp" %>
<%

    // get list of months
    List<String> months = this.getMonthsList();

    // get map with all categories
    Map<String, String> categories = this.getActivatedCategories(
                _tagManager,
                _properties.get("categories", new String[0]),
                "Any category"
        );

    // get list of years
    List<String> yearList = null;
    String minYearStr = _properties.get("oldest-year", (String) null);
    if (!StringUtils.isBlank(minYearStr)) {
        yearList = this.getYearList(Integer.parseInt(minYearStr));
    }

    String[] itemCountList = new String[] { "20", "50", "100" };

    // extract the current request parameters
    String currentValue = escapeBody(request.getParameter("query"));
%>
<c:choose>
    <c:when test="<%= yearList == null %>">
        <p class="component searchlist notfound">Please setup the oldest year</p>
    </c:when>

    <c:otherwise>

        <form action="?" method="get" class="search">
            <div class="listingfields">

                <%-- search query field --%>
                <div class="field-block">
                    <label for="searchQuery">Search</label>
                    <c:choose>
                        <c:when test="<%= currentValue == null %>">
                            <input type="text" name="query" id="searchQuery" placeholder="Search &hellip;" />
                        </c:when>
                        <c:otherwise>
                            <input type="text" name="query" id="searchQuery" value="<%= currentValue %>" placeholder="Search &hellip;" />
                        </c:otherwise>
                    </c:choose>
                </div>

                <%-- category box --%>
                <c:choose>

                    <%-- has more than just "Any category" ? --%>
                    <c:when test="<%= categories.size() > 0 %>">
                        <div class="field-block">
                            <label for="category_filter">Category</label>
                            <select id="category_filter" name="category">
                                <option value="">Any category</option>
                                <c:forEach items="<%= categories %>" var="category">

                                    <c:choose>
                                        <c:when test="${category.key == param.category}">
                                            <option value="${category.key}" selected="selected">${category.value}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${category.key}">${category.value}</option>
                                        </c:otherwise>
                                    </c:choose>

                                </c:forEach>
                            </select>
                        </div>
                    </c:when>

                    <%-- just have a default value --%>
                    <c:otherwise>
                        <input type="hidden" name="category" value="" />
                    </c:otherwise>

                </c:choose>

                <%-- month list --%>
                <div class="field-block">
                    <label for="month">Month</label>
                    <select id="month" name="month">
                        <option value="">Any month</option>
                        <c:forEach items="<%= months %>" var="month" varStatus="monthStatus">
                            <c:choose>
                                <c:when test="${monthStatus.index + 1 == param.month}">
                                    <option value="${1 + monthStatus.index}" selected="selected">${month}</option>
                                </c:when>

                                <c:otherwise>
                                    <option value="${1 + monthStatus.index}">${month}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>

                <%-- year list --%>
                <div class="field-block">
                    <label for="year">Year</label>
                    <select id="year" name="year">
                        <option value="">Any year</option>
                        <c:forEach items="<%= yearList %>" var="year">
                            <c:choose>
                                <c:when test="${year == param.year}">
                                    <option value="${year}" selected="selected">${year}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${year}">${year}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                </div>

                <input type="submit" value="Search" />

                <div class="n-items">
                    <div class="wrap">

                        <label for="maxItems">Items:</label>
                        <select id="maxItems" name="maxItems">
                            <c:forEach items="<%= itemCountList %>" var="itemCount">
                                <c:choose>
                                    <c:when test="${itemCount == param.maxItems}">
                                        <option value="${itemCount}" selected="selected">${itemCount}</option>
                                    </c:when>

                                    <c:otherwise>
                                        <option value="${itemCount}">${itemCount}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>

                    </div>
                </div>
            </div>
        </form>


    </c:otherwise>

</c:choose>
