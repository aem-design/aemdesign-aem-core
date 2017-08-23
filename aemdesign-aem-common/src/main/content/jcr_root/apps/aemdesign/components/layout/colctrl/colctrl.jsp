<%@page session="false"%>
<%@include file="/apps/aemdesign/global/global.jsp" %>
<%@ page import="com.day.cq.i18n.I18n,
                 com.day.cq.wcm.api.WCMMode,
                 com.day.cq.wcm.api.components.IncludeOptions,
                 com.day.cq.wcm.api.components.Toolbar,
                 com.day.cq.wcm.foundation.Paragraph,
                 com.day.cq.wcm.foundation.Placeholder" %>
<%


    //this is being used some other way, Touch UI ajax
    Boolean isForceEditContext = false;

    String uiType = "";
    WCMMode editMode = WCMMode.fromRequest(request);
    String layout = defaultLayout;
    Integer numCols = 0;
    Paragraph.Type controType = Paragraph.Type.NORMAL;
    String path = resource.getPath();

    isForceEditContext = isForceEditContext(_slingRequest);
    uiType = UIMode(_slingRequest);

    if (resource instanceof Paragraph) {
        Paragraph par = (Paragraph) resource;
        numCols = par.getNumCols();
        controType = par.getType();
        path = par.getPath();
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
                break;
            }
            case BREAK:
                break;
            case NORMAL:
                break;
        }
    }
%>
