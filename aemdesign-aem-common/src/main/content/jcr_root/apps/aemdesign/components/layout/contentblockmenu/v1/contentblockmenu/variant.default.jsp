<nav ${componentProperties.componentAttributes}>
    <ul>
    <c:forEach items="${componentProperties.contentBlockList}" var="entry" varStatus="entryStatus">
        <li><a href="#${entry.key}">${entry.value}</a></li>
    </c:forEach>
    </ul>
</nav>