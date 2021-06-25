<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<sling2:adaptTo var="pagecomponents" adaptable="${slingRequest}" adaptTo="design.aem.reports.PageComponentsReportCellValue" />
<td is="coral-table-cell">${pagecomponents.components}</td>