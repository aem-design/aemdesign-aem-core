<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@page session="false"%><%@ page import="java.util.HashSet,
                    com.day.cq.commons.jcr.JcrConstants,
                     java.util.Set,
                     com.day.cq.wcm.api.WCMMode,
                     com.day.cq.wcm.api.components.IncludeOptions,
                     com.day.cq.wcm.foundation.Paragraph,
                     com.day.cq.wcm.foundation.ParagraphSystem,
                     org.apache.commons.lang3.StringUtils" %><%
%><%@include file="/apps/aemdesign/global/global.jsp"%>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/utils.jsp" %>
<%@include file="/apps/aemdesign/global/paragraph.jsp"%><%

    if(editContext!=null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        editContext.getEditConfig().setOrderable(false);
    }

    String newType = resource.getResourceType() + "/new";
    boolean hasColumns = false;
    boolean hasFake = false;
    ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);

    ComponentProperties componentProperties = new ComponentProperties();

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"layout", ""},
    };

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
                            DEFAULT_FIELDS_COLUMNS,
                            DEFAULT_FIELDS_STYLE,
                            DEFAULT_FIELDS_ACCESSIBILITY);

                    openRow(parSys,par,out, componentProperties);
                    openCol(parSys,par,out, componentProperties);

                    hasColumns = true;
                    break;
                case BREAK:
                    if (editContext != null) {
                        // draw 'new' bar
                        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                        %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                    }

                    closeCol(par,out);
                    openCol(parSys,par,out, componentProperties);

                    break;
                case END:
                    if (editContext != null) {
                        // draw new bar
                        IncludeOptions.getOptions(request, true).getCssClassNames().add("section");
                        %><sling:include resource="<%= par %>" resourceType="<%= newType %>"/><%
                    }
                    if (hasColumns) {
                        closeCol(par,out);
                        closeRow(par,out,false);
                        componentProperties = new ComponentProperties();
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
    }
    if (hasColumns) {
        closeCol(null,out);
        closeRow(out, true);
        componentProperties = new ComponentProperties();
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
