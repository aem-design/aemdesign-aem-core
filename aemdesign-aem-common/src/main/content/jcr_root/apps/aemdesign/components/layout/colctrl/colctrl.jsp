<%@page session="false" %>
<%@page import="com.day.cq.commons.jcr.JcrConstants,
                com.day.cq.wcm.api.Page,
                com.day.cq.wcm.api.WCMMode,
                com.day.cq.wcm.api.components.IncludeOptions,
                com.day.cq.wcm.api.components.Toolbar,
                com.day.cq.wcm.foundation.Paragraph,
                com.day.cq.wcm.foundation.ParagraphSystem,
                java.util.HashSet" %>
<%@ page import="java.util.Set" %>
<%
%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@include file="/apps/aemdesign/global/components.jsp" %>
<%@include file="/apps/aemdesign/global/utils.jsp" %>
<%@include file="/apps/aemdesign/global/paragraph.jsp" %>
<%

    final String DEFAULT_LAYOUT = "1;colctrl-1c";
    ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);
    String newType = resource.getResourceType() + "/new";

    ComponentProperties componentProperties = getNewComponentProperties(pageContext);

    Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"layout", DEFAULT_LAYOUT},
    };

    String COMPONENT_NAMESPACE = "aemdesign.components.layout.colctrl";
    String COMPONENT_NAMESPACE_PROPERTIES = ".componentProperties";
    String COMPONENT_NAMESPACE_CURRENTCOLUMN = ".currentColumn";

    //this is being used some other way, Touch UI ajax
    Boolean isForceEditContext = false;

    String uiType = "";
    WCMMode editMode = WCMMode.fromRequest(request);
    String layout = defaultLayout;
    Integer numCols = 0;
    Integer currentColumn = 0;
    Paragraph.Type controType = Paragraph.Type.NORMAL;
    String path = resource.getPath();

    isForceEditContext = isForceEditContext(_slingRequest);
    uiType = UIMode(_slingRequest);

    //Paragraph par = (Paragraph) resource;

    if (resource instanceof Paragraph) {
        Paragraph par = (Paragraph) resource;
        numCols = par.getNumCols();
        controType = par.getType();
        path = par.getPath();

        componentProperties = getComponentProperties(
                pageContext,
                _currentPage,
                par.getPath().replace(_currentPage.getPath() + "/" + JcrConstants.JCR_CONTENT, "."),
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

    } else {
        ValueMap resProp = resource.adaptTo(ValueMap.class);

        controType = Paragraph.Type.valueOf(resProp.get(COL_CTL_TYPE, "start").toUpperCase());

        //get current layout and number of columns
        layout = resProp.get(COL_CTL_LAYOUT, defaultLayout);


        int i = layout.indexOf(';');
        if (i > 0) {
            try {
                numCols = Integer.parseInt(layout.substring(0, i));
            } catch (NumberFormatException e) {
                // ignore
            }
            layout = layout.substring(i + 1);
        }
    }

    String[] columnsFormat = new String[0];

    if (editContext != null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
        switch (controType) {
            case START: {
                String text = I18n.get(slingRequest, "Columns with {0} Columns", "{0} is the placeholder for the number of columns", numCols);
                %><%= Placeholder.getDefaultPlaceholder(slingRequest, text, "", "cq-marker-start") %><%
                if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
                    editContext.getEditConfig().getToolbar().add(0, new Toolbar.Separator());
                    editContext.getEditConfig().getToolbar().add(0, new Toolbar.Label(text));
                    // disable ordering to get consistent behavior
                    editContext.getEditConfig().setOrderable(false);
                }

                //openRow(numCols,out, componentProperties);
                //openCol(columnsFormat,0,"columnstart",out, componentProperties);

                out.write("<!--columns e start:-->");

                componentProperties = getComponentProperties(
                        pageContext,
                        _currentPage,
                        componentFields,
                        DEFAULT_FIELDS_STYLE,
                        DEFAULT_FIELDS_ACCESSIBILITY);
                componentProperties.put("numCols",numCols);

                String currentLayout = componentProperties.get("layout",DEFAULT_LAYOUT);
                if (currentLayout.contains(";")) {
                    componentProperties.put("layout",currentLayout.split(";")[1]);
                }

                request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES), componentProperties);

//                openRow(numCols, out, componentProperties);
//                openCol(currentColumn, out, componentProperties);

                currentColumn = currentColumn + 1;
                request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);


                break;
            }
            case END: {
                String text = I18n.get(slingRequest, "End of Columns");
                %><%= Placeholder.getDefaultPlaceholder(slingRequest, text, "", "cq-marker-end") %><%
                if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
                    editContext.getEditConfig().getToolbar().clear();
                    editContext.getEditConfig().getToolbar().add(
                            new Toolbar.Label("End of Columns"));
                    // disable ordering to get consistent behavior
                    editContext.getEditConfig().setOrderable(false);
                    // set the content path to a fake one, since this editbar
                    // is not used for editing and we need to avoid collisions with
                    // the 'insert' bar with the same content path.
                    editContext.setContentPath(path + "_fake");
                }


                componentProperties = (ComponentProperties) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                request.removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                request.removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN));

//                closeCol(null, out);
//                closeRow(out, false);


                out.write("<!--columns e end:-->");
                break;
            }
            case BREAK: {

                //dont print anything if break is on a page on its own
                if (request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES)) != null &&
                        request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)) != null) {

                    componentProperties = (ComponentProperties) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                    currentColumn = (Integer) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN));

                    numCols = componentProperties.get("numCols",numCols);

//                    closeCol(null, out);
//                    openCol(currentColumn, out, componentProperties);

                    request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);

                    String text = I18n.get(slingRequest, "Columns Break {0} of {1}", "Break", currentColumn, numCols-1);
                    %><%= Placeholder.getDefaultPlaceholder(slingRequest, text, "", "cq-marker-break") %><%

                }

                out.write("<!--columns e break:-->");
                break;
                }
            case NORMAL: {
                out.write("<!--columns e normal:-->");
                break;
            }
        }
    } else {
        switch (controType) {
            case START: {
                out.write("<!--columns start:-->");

                componentProperties = getComponentProperties(
                        pageContext,
                        _currentPage,
                        componentFields,
                        DEFAULT_FIELDS_STYLE,
                        DEFAULT_FIELDS_ACCESSIBILITY);
                componentProperties.put("numCols",numCols);

                String currentLayout = componentProperties.get("layout",DEFAULT_LAYOUT);
                if (currentLayout.contains(";")) {
                    componentProperties.put("layout",currentLayout.split(";")[1]);
                }

                numCols = componentProperties.get("numCols",numCols);

                request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES), componentProperties);

                openRow(numCols, out, componentProperties);
                openCol(currentColumn, out, componentProperties);
                request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);
                break;
            }
            case END: {

                componentProperties = (ComponentProperties) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                request.removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                request.removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN));

                closeCol(null, out);
                closeRow(out, false);
                out.write("<!--columns end:-->");
                break;
            }
            case BREAK:

                //dont print anything if break is on a page on its own
                if (request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES)) != null &&
                        request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)) != null) {

                    componentProperties = (ComponentProperties) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                    currentColumn = (Integer) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)) ;


                    closeCol(null, out);
                    openCol(currentColumn, out, componentProperties);

                    request.setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);
                }
                out.write("<!--column break:-->");

                break;
            case NORMAL:

                componentProperties = (ComponentProperties) request.getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));

                out.write("<!--columns content:-->");
                break;
        }

    }
%>
