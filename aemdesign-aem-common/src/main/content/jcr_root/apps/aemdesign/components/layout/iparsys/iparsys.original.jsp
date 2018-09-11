<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Inheritance Paragraph System

--%><%@ page import="java.util.HashSet,
                     java.util.Set,
                     com.day.cq.commons.jcr.JcrConstants,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.cq.wcm.api.components.IncludeOptions,
                     com.day.cq.wcm.foundation.Paragraph,
                     com.day.cq.wcm.foundation.ParagraphSystem" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    if(editContext!=null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        editContext.getEditConfig().setOrderable(false);
    }

    String newType = resource.getResourceType() + "/new";
    boolean hasColumns = false;
    boolean hasFake = false;
    ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);
    for (Paragraph par: parSys.paragraphs()) {
        Resource r = (Resource) par;
        if (r.getResourceType().endsWith("/iparsys/par")) {
            hasFake = true;
            IncludeOptions.getOptions(request, true).forceCellName("");
            %><div class="iparys_inherited"><cq:include path="<%= r.getPath() %>" resourceType="<%= r.getResourceType() %>"/></div><%
        } else {
            switch (par.getType()) {
                case START:
                    if (hasColumns) {
                        // close in case missing END
                        %></div></div><%
                    }
                    if (editContext != null) {
                        // draw 'edit' bar
                        Set<String> addedClasses = new HashSet<String>();
                        addedClasses.add("section");
                        addedClasses.add("colctrl-start");
                        IncludeOptions.getOptions(request, true).getCssClassNames().addAll(addedClasses);
                        %><sling:include resource="<%= par %>"/><%
                    }
                    // open outer div
                    %><div class="parsys_column <%= par.getBaseCssClass()%>"><%
                    // open column div
                    %><div class="parsys_column <%= par.getCssClass() %>"><%
                    hasColumns = true;
                    break;
                case BREAK:
                    if (editContext != null) {
                        // draw 'new' bar
                        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                        %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                    }
                    // open next column div
                    %></div><div class="parsys_column <%= par.getCssClass() %>"><%
                    break;
                case END:
                    if (editContext != null) {
                        // draw new bar
                        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                        %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                    }
                    if (hasColumns) {
                        // close divs and clear floating
                        %></div></div><div style="clear:both"></div><%
                        hasColumns = false;
                    }
                    if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
                        // draw 'end' bar
                        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                        %><sling:include resource="<%= par %>"/><%
                    }
                    break;
                case NORMAL:
                    // include 'normal' paragraph
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");

                    // draw anchor if needed
                    if (currentStyle.get("drawAnchors", false)) {
                        String path = par.getPath();
                        path = path.substring(path.indexOf(JcrConstants.JCR_CONTENT)
                                + JcrConstants.JCR_CONTENT.length() + 1);
                        String anchorID = path.replace("/", "_").replace(":", "_");
                        %><a name="<%= xssAPI.encodeForHTMLAttr(anchorID) %>" style="visibility:hidden"></a><%
                    }
                    %><sling:include resource="<%= par %>"/><%
                    break;
            }
        }
    }
    if (hasColumns) {
        // close divs in case END missing. and clear floating
        %></div></div><div style="clear:both"></div><%
    }
    // include fake inheritance if not present in the content
    if (!hasFake) {
        // draw new bar before inherited ones
        %><div class="section"><cq:include path="*" resourceType="<%= newType %>"/></div><%

        IncludeOptions.getOptions(request, true).forceCellName("");
        //String path = resource.getPath() + "/iparsys_fake_par";
        String path = resource.getPath() + "/iparsys_fake_par";
        String resType = resource.getResourceType() + "/par";
        %><div class="iparys_inherited"><cq:include path="<%= path %>" resourceType="<%= resType %>"/></div><%
    } else {
        // new bar
        if (editContext != null) {
            editContext.setAttribute("currentResource", null);
            // draw 'new' bar
            IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
            %><cq:include path="*" resourceType="<%= newType %>"/><%
        }
    }

%>

