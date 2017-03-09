<%@page session="false"%><%@ page import="com.day.cq.wcm.api.Page,
            com.day.cq.wcm.api.components.IncludeOptions,
            com.day.text.Text,
            java.util.List" %><%
%><%@include file="/apps/aemdesign/global/global.jsp"%>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/utils.jsp" %>
<%@include file="/apps/aemdesign/global/paragraph.jsp"%><%

    // if inheritance is canceled, do nothing
    if (properties != null && properties.get("inheritance", "").equals("cancel")) {
        if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
            editContext.getEditConfig().getToolbar().set(0,
                new Toolbar.Label("Inheritance disabled"));
        }
        return;
    }

    // get page content relative path to the parsys
    String parSysPath = Text.getRelativeParent(resource.getPath(), 1);
    parSysPath = parSysPath.substring(currentPage.getContentResource().getPath().length() + 1);
    Page parent = currentPage.getParent();

    LinkedList<Paragraph> paras = new LinkedList<Paragraph>();
    if (!addParas(paras, parent, parSysPath, slingRequest)) {
        if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
            editContext.getEditConfig().getToolbar().set(0,
                new Toolbar.Label("Parent canceled inheritance"));
        }
        return;
    }

    // disable WCM for inherited components
    WCMMode mode = WCMMode.DISABLED.toRequest(request);
    boolean hasColumns = false;
    try {

        HashMap<String, Object> currentRowStyle = new HashMap<String, Object>();

        for (Paragraph par: paras) {
            switch (par.getType()) {
                case START:
                    if (hasColumns) {
                        // close in case missing END
                        closeCol(null,out);
                        closeRow(par,out,false);
                        currentRowStyle.clear();
                    }

                    currentRowStyle.putAll(getRowStyle(par));

                    openRow(paras, par, out, currentRowStyle);
                    openCol(paras, par, out, currentRowStyle);

                    hasColumns = true;
                    break;
                case BREAK:

                    closeCol(par,out);
                    openCol(paras,par,out, currentRowStyle);

                    break;
                case END:
                    if (hasColumns) {
                        // close divs and clear floating
                        closeCol(null,out);
                        closeRow(par,out,false);
                        currentRowStyle.clear();
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

                    String defDecor =_componentContext.getDefaultDecorationTagName();

                    if (REMOVEDECORATION && WCMMode.DISABLED == WCMMode.fromRequest(request)) {
                        forceNoDecoration(_componentContext,IncludeOptions.getOptions(request, true));
                    }

                    %><sling:include resource="<%= par %>"/><%

                    if (REMOVEDECORATION && WCMMode.DISABLED == WCMMode.fromRequest(request)) {
                        setDecoration(_componentContext,IncludeOptions.getOptions(request, true),defDecor);
                    }

                    break;
            }
        }
    } finally {
        mode.toRequest(request);
    }
    if (hasColumns) {
        // close divs in case END missing. and clear floating
        closeCol(null,out);
        closeRow(out,false);
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
