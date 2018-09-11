<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ include file="/apps/aemdesign/global/global.jsp" %>

<%
    Map<String, Object> info = new HashMap<String, Object>();

    String dataHref = _properties.get("data-href", ""),
           dataSize = _properties.get("data-size", "medium"),
           dataAnnotation = _properties.get("data-annotation", "bubble");
    if(dataHref!=null && dataHref.length() > 0)
           info.put("dataHref"," data-href="+dataHref);
    else
           info.put("dataHref","");

    if(dataSize!=null)
       info.put("dataSize"," data-size="+dataSize);
    else
       info.put("dataSize","");

    if(dataAnnotation!=null)
       info.put("dataAnnotation"," data-annotation="+dataAnnotation);
    else
       info.put("dataAnnotation","");


//    <!--script type="text/javascript">
//    (function() {
//    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
//    po.src = 'https://apis.google.com/js/plusone.js';
//    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
//    })();
//    </script-->
%>
<c:set var="info" value="<%= info %>" />
<div class="g-plusone" ${info.dataHref}${info.dataSize}${info.dataAnnotation}></div>
<%@include file="/apps/aemdesign/global/component-badge.jsp" %>