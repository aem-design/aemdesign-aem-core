<%@page session="false"%>
<%@page import="com.day.cq.commons.jcr.JcrConstants,
                    com.day.cq.wcm.api.Page,
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.api.components.IncludeOptions,
                    com.day.cq.wcm.foundation.Paragraph,
                    com.day.cq.wcm.foundation.ParagraphSystem,
                    java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%
%><%@include file="/apps/aemdesign/global/global.jsp"%>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/utils.jsp" %>
<%@include file="/apps/aemdesign/global/paragraph.jsp"%><%

    ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);
    String newType = resource.getResourceType() + "/new";

    ComponentProperties componentProperties = new ComponentProperties();

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"layout", ""},
    };

    boolean hasColumns = false;
    for (Paragraph par: parSys.paragraphs()) {
        if (editContext != null) {
            editContext.setAttribute("currentResource", par);
        }
        switch (par.getType()) {
            case START:
                if (hasColumns) {
                    // close in case missing END
                    closeCol(null,out);
                    closeRow(par,out,false);
                    componentProperties = new ComponentProperties();
                }
                if (editContext != null) {
                    // draw 'edit' bar
                    Set<String> addedClasses = new HashSet<String>();
                    addedClasses.add("section");
                    addedClasses.add("colctrl-start");
                    IncludeOptions.getOptions(request, true).getCssClassNames().addAll(addedClasses);
                    %><sling:include resource="<%= par %>"/><%
                }

                componentProperties = getComponentProperties(
                        pageContext,
                        _currentPage,
                        par.getPath().replace(_currentPage.getPath()+"/"+JcrConstants.JCR_CONTENT,"."),
                        componentFields,
                        DEFAULT_FIELDS_STYLE,
                        DEFAULT_FIELDS_ACCESSIBILITY);

                openRow(parSys,par,out, componentProperties,"");
                openCol(parSys,par,out, componentProperties,"");

                hasColumns = true;
                break;
            case BREAK:
                if (editContext != null) {
                    // draw 'new' bar
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                    %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                }

                closeCol(par,out);

                openCol(parSys,par,out, componentProperties,"");

                break;
            case END:
                if (editContext != null) {
                    // draw new bar
                    IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                    %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                }
                if (hasColumns) {
                    // close divs
                    closeCol(null,out);
                    closeRow(par,out,false);
                    hasColumns = false;
                    componentProperties = new ComponentProperties();
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
    if (hasColumns) {
        // close divs in case END missing. and clear floating
        closeCol(null,out);
        closeRow(out,false);
    }
    if (editContext != null) {
        editContext.setAttribute("currentResource", null);
        // draw 'new' bar
        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
        %><cq:include path="*" resourceType="<%= newType %>"/><%
    }
%>
