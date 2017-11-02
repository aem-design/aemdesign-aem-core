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

  Inheritance Paragraph System - Par

--%><%@ page import="com.day.text.Text,
            java.util.Iterator,
            java.util.LinkedList, java.util.List,
            org.apache.sling.api.SlingHttpServletRequest,
            com.day.cq.commons.jcr.JcrConstants,
            com.day.cq.wcm.api.WCMMode,
            com.day.cq.wcm.api.components.Toolbar,
            com.day.cq.wcm.api.components.IncludeOptions,
            com.day.cq.wcm.foundation.Paragraph,
            com.day.cq.wcm.foundation.ParagraphSystem,
            com.day.cq.wcm.foundation.Placeholder,
            com.day.cq.wcm.api.Page" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    // if inheritance is canceled, do nothing
    if (properties != null && properties.get("inheritance", "").equals("cancel")) {
        if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
            String text = "Inheritance disabled";
            editContext.getEditConfig().getToolbar().set(0,
                    new Toolbar.Label(text));
        %><%= Placeholder.getDefaultPlaceholder(slingRequest, text, "", "cq-marker-start") %><%
        }
        return;
    }
    // get page content relative path to the parsys
    String parSysPath = Text.getRelativeParent(resource.getPath(), 1);
    //resource page can be null for pages like /content/dam/geometrixx/documents/GeoMetrixx_Banking.indd/jcr:content/renditions/page
	if (resourcePage != null) {
    	parSysPath = parSysPath.substring(resourcePage.getContentResource().getPath().length() + 1);
    } else {
        parSysPath = parSysPath.substring(currentPage.getContentResource().getPath().length() + 1);
    }

    // Note the resourcePage vs currentPage inheritance https://issues.adobe.com/browse/CQ5-3910
    Page parent = currentPage.getParent();

    LinkedList<Paragraph> paras = new LinkedList<Paragraph>();
    if (!addParas(paras, parent, parSysPath, slingRequest)) {
        if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
            String text = "Parent canceled inheritance";
            editContext.getEditConfig().getToolbar().set(0,
                    new Toolbar.Label(text));
        %><%= Placeholder.getDefaultPlaceholder(slingRequest, text, "", "cq-marker-start") %><%
        }
        return;
    } else {
        %><%= Placeholder.getDefaultPlaceholder(slingRequest, "Inherited Paragraph System", "", "cq-marker-start") %><%
    }

    // disable WCM for inherited components
    WCMMode mode = WCMMode.DISABLED.toRequest(request);
    boolean hasColumns = false;
    try {
        for (Paragraph par: paras) {
            switch (par.getType()) {
                case START:
                    if (hasColumns) {
                        // close in case missing END
                        %></div></div><%
                    }
                    // open outer div
                    %><div class="parsys_column <%= par.getBaseCssClass()%>"><%
                    // open column div
                    %><div class="parsys_column <%= par.getCssClass() %>"><%
                    hasColumns = true;
                    break;
                case BREAK:
                    // open next column div
                    %></div><div class="parsys_column <%= par.getCssClass() %>"><%
                    break;
                case END:
                    if (hasColumns) {
                        // close divs and clear floating
                        %></div></div><div style="clear:both"></div><%
                        hasColumns = false;
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
                        %><a name="<%= anchorID %>" style="visibility:hidden"></a><%
                    }
                    %><sling:include resource="<%= par %>"/><%
                    break;
            }
        }
    } finally {
        mode.toRequest(request);
    }
    if (hasColumns) {
        // close divs in case END missing. and clear floating
        %></div></div><div style="clear:both"></div><%
    }

%><%!

    private boolean addParas(List<Paragraph> paras, Page parent,
                             String parSysPath, SlingHttpServletRequest req) {
        if (parent == null) {
            return true;
        }
        // get the parent parsys
        Resource parSysRes = parent.getContentResource(parSysPath);

        // check if parent parsys canceled inheritance to its children
        if (parSysRes != null && parSysRes.adaptTo(ValueMap.class).get("inheritance", "").equals("cancel")) {
            return false;
        }

        // iterate over paras
        boolean hasFake = false;
        if (parSysRes != null) {
            ParagraphSystem parSys = ParagraphSystem.create(parSysRes, req);
            for (Paragraph par: parSys.paragraphs()) {
                Resource r = (Resource) par;
                if (r.getResourceType().endsWith("/iparsys/par")) {
                    hasFake = true;
                    // if inheritance is canceled, do nothing
                    ValueMap properties = r.adaptTo(ValueMap.class);
                    if (!properties.get("inheritance", "").equals("cancel")) {
                        addParas(paras, parent.getParent(), parSysPath, req);
                    }
                } else {
                    paras.add(par);
                }
            }
        }
        if (!hasFake) {
            // if not fake paragraph is initialized, try to inherit anyways.
            addParas(paras, parent.getParent(), parSysPath, req);
        }
        return true;
    }


%>