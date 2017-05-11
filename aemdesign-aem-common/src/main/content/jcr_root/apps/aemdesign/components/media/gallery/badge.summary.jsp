<%@ page import="java.util.*" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>
<%@ include file="/apps/aemdesign/global/components.jsp" %>
<%@ include file="gallerydata.jsp" %>
<%
    Node gallery = (Node)request.getAttribute("gallery");
    String menuColor = (String)request.getAttribute("menuColor");

     String listFrom = gallery.hasProperty("listFrom") ? gallery.getProperty("listFrom").getValue().toString() : "" ;

    List<Map> listItems = new ArrayList<Map>();

    if ( listFrom.equals("static")  ) {
        Value[] values = gallery.getProperty("listItems").getValues();
        String[] items = new String[values.length];
        for(int i=0;i <values.length; i++){
            items[i] = values[i].toString();
        }
        listItems = getPicturesFromFixedList(_resourceResolver,items);
    } else if ( listFrom.equals("children")  ) {
        String listPath = gallery.getProperty("listPath") == null ? "" :gallery.getProperty("listPath").getValue().toString();
        if(StringUtils.isNotEmpty(listPath)) {
        Resource listPathR = _resourceResolver.resolve(listPath);
            if (listPathR != null) {
            listItems = getPicturesFromResource(_resourceResolver, listPathR);
            }
        }

    }

    int imageCount = 0;
    int videoCount = 0;
    int audioCount = 0;

    for(Map row:listItems){
        if((Boolean)row.get("isvideo")){
            videoCount++;
        }else if((Boolean)row.get("isimage")){
            imageCount++;
        }else if((Boolean)row.get("isaudio")){
            audioCount++;
        }
    }
    pageContext.setAttribute("imageCount", imageCount);
    pageContext.setAttribute("videoCount", videoCount);
    pageContext.setAttribute("audioCount", audioCount);
    pageContext.setAttribute("menuColor",  menuColor);

%>
<c:if test="${imageCount > 0 || videoCount > 0 || audioCount > 0}">
    <ul
        <c:if test="${not empty menuColor}">class="${menuColor}"</c:if>>

    <c:if test="${imageCount > 0 }">
        <li class="image">
            <c:out value="${imageCount}"/><small>Images</small>
        </li>
    </c:if>
    <c:if test="${videoCount > 0 }">
        <li class="video">
            <c:out value="${videoCount}"/><small>Videos</small>
        </li>
    </c:if>
    <c:if test="${audioCount > 0 }">
        <li class="audio">
            <c:out value="${audioCount}"/><small>Audios</small>
        </li>
    </c:if>
    </ul>
</c:if>