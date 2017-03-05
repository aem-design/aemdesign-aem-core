<%@ page session="false" %>
<%@ include file="/apps/aemdesign/global/global.jsp"%>

    com.day.cq.wcm.foundation.List list = (com.day.cq.wcm.foundation.List)request.getAttribute("list");

    int maxItem = list.size();

    int pageSize = 10;
    if(list.getPageMaximum()>0){
      pageSize = list.getPageMaximum();
    }

    int totalPageNum = (int)Math.ceil((double)maxItem / pageSize);

    int groupSize = _properties.get("pageGroupSize", (Integer)10).intValue();
    
    int starts = list.getPageStart();
    int curPageNum = starts/pageSize + 1;
    int curGroupNum = (int)Math.ceil((double)curPageNum/groupSize);
    int curGroupStart = (curGroupNum - 1) * pageSize * groupSize;
    int firstPageOfCurGroup = curGroupStart/pageSize + 1;
    int lastPageOfCurGroup = firstPageOfCurGroup + groupSize - 1;
%>
<div class="paging">
	<ul class="pagination">
	<%
	if(firstPageOfCurGroup>1){
		list.setPageStart((firstPageOfCurGroup-1)*pageSize);
	%>
	<li><a href="<%=list.getPreviousPageLink()%>"><span>&laquo;</span></a><span class="previous">Previous <%=groupSize %> pages</span></li>
	<%
	}else{
		%>
		<li class="disabled"><a href="#"><span>&laquo;</span></a><span class="previous">Previous <%=groupSize %> pages</span></li>
		<%
	}
	String styleCurPageFlag = "";
	for(int i = firstPageOfCurGroup;i<firstPageOfCurGroup+groupSize;i++){
		if(curPageNum == i){
			styleCurPageFlag = "active";
		}else{
			styleCurPageFlag = "";
		}
		if(i==1){
			list.setPageStart(i*pageSize);
			if(list.getPreviousPageLink() != null){
				%>
				<li class="<%=styleCurPageFlag %>"><a href="<%=list.getPreviousPageLink()%>"><%=i%></a></li>
				<%
				}
		}else{
			list.setPageStart((i-2)*pageSize);
			if(list.getNextPageLink() != null){
			%>
			<li class="<%=styleCurPageFlag %>"><a href="<%=list.getNextPageLink()%>"><%=i%></a></li>
			<%
			}
		}
	}
	
	
	if(lastPageOfCurGroup < totalPageNum){
		list.setPageStart((lastPageOfCurGroup-1)*pageSize);
	%>
	<li><span class="next">Next <%=groupSize %> pages</span><a href="<%=list.getNextPageLink()%>"><span>&raquo;</span></a></li>
	 <%}else{
		 %>
			<li class="disabled"><span class="next">Next <%=groupSize %> pages</span><a href="#"><span>&raquo;</span></a></li>
		 <% 
	 }	
	%>
	 </ul>
</div>