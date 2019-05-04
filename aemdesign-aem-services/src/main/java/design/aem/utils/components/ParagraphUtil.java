package design.aem.utils.components;

import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.components.Toolbar;
import com.day.cq.wcm.foundation.Paragraph;
import com.day.cq.wcm.foundation.ParagraphSystem;
import com.day.cq.wcm.foundation.Placeholder;
import design.aem.components.ComponentProperties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static design.aem.utils.components.ConstantsUtil.*;

public class ParagraphUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParagraphUtil.class);

    public static final String COLUMN_CLASS = "col-sm"; //gets added to cols
    public static final String ROW_CLASS = "row"; //gets added to rows
    public static final String COLUMNS_CLASS = "parsys_column"; //gets added to rows and columns

    /**
     * advanced open row
     * @param parSys list of pars being processed
     * @param par current par child of parSys being processed
     * @param out html output
     * @param componentProperties component properties
     * @throws IOException
     */
    public static final void openRow(Object parSys, Paragraph par, JspWriter out, ComponentProperties componentProperties, String rowClass) throws IOException {

        openRow(par.getNumCols(),out,componentProperties, rowClass);

    }


    /**
     * advanced open row
     * @param numCols number of los
     * @param out html output
     * @param componentProperties component properties
     * @throws IOException
     */
    public static final void openRow(Integer numCols, JspWriter out, ComponentProperties componentProperties, String rowClass) throws IOException {

        final String numColsCSS = MessageFormat.format("colctrl-{0}c",numCols);

        if (componentProperties!=null) {
            componentProperties.attr.add("class", numColsCSS);

            final String componentAttributes = componentProperties.attr.build().replaceAll("&#x20;", " ");

            //columns start div
            out.write(MessageFormat.format("<div{0}>", componentAttributes));

        } else {
            out.write(MessageFormat.format("<div class=\"{}\">", numColsCSS));

        }


        // row start
        out.write(MessageFormat.format("<div class=\"{0} {1} {2}\">",COLUMNS_CLASS, ROW_CLASS, rowClass));
    }

    /**
     * print close of row
     * @param par current par child of parSys being processed
     * @param out html output
     * @param clearFix
     * @throws IOException
     */
    public static final void closeRow(Paragraph par, JspWriter out, boolean clearFix) throws IOException {
        closeRow(out, clearFix);
    }

    /**
     * print close of row
     * @param out html output
     * @param clearFix
     * @throws IOException
     */
    public static final void closeRow(JspWriter out, boolean clearFix) throws IOException {

        //row end
        out.write("</div>");
        //column end
        out.write("</div>");

        if (clearFix) {
            out.write("<div style=\"clear:both\"></div>");
        }
    }


    /**
     * used by classic column component, write opening tags for the column for Paragraph System, also check if base alignment is required based
     * @param parSys list of pars being processed
     * @param par current par child of parSys being processed
     * @param out html output
     * @throws IOException
     */
    public static final void openCol(Object parSys, Paragraph par, JspWriter out, ComponentProperties componentProperties, String columnClassStyle) throws IOException {

        openCol(Arrays.asList(par.getBaseCssClass().split(",")),par.getColNr(),par.getCssClass(),out,componentProperties, columnClassStyle);

    }

    /**
     * used by classic column component, write opening tags for the column for Paragraph System, also check if base alignment is required based
     * @param columnsFormat columns format
     * @param colNumber current column
     * @param columnClass column class
     * @param out html output
     * @param componentProperties columns componentProperties
     * @throws IOException
     */
    public static final void openCol(List<String> columnsFormat, Integer colNumber, String columnClass, JspWriter out, ComponentProperties componentProperties, String columnClassStyle) throws IOException {

        //print column start
        if (columnsFormat.size() >= 1 && columnsFormat.get(0).contains(",")) {
            //take the [0] = [col-md-] and add to it width by current column number
            StringBuilder columnClassBuilder = new StringBuilder();
            for(int i=0; i < columnsFormat.size(); i++){
                String spacer = (i == columnsFormat.size()-1 ? "" : " ");
                columnClassBuilder.append(columnsFormat.get(i).split(",")[0]);
                columnClassBuilder.append(columnsFormat.get(i).split(",")[colNumber + 1]);
                columnClassBuilder.append(spacer);
            }
            out.write(MessageFormat.format("<div class=\"{0} {1} {2} {3}\">",COLUMNS_CLASS, COLUMN_CLASS, columnClassBuilder.toString(), columnClassStyle)); //EXTENDED
        } else {
            //out.write("<div class='parsys_column " + par.getBaseCssClass() + " col-" + n + "'>");
            out.write(MessageFormat.format("<div class=\"{0} {1} {2} {3}\">",COLUMNS_CLASS, COLUMN_CLASS, columnClass, columnClassStyle)); //ORIGINAL
        }
    }

    /**
     * used by classic column component, write opening tags for the column for Paragraph System, also check if base alignment is required based
     * @param colNumber current column number
     * @param out html output
     * @throws IOException
     */
    public static final void openCol(Integer colNumber, JspWriter out, ComponentProperties componentProperties, String columnClassStyle) throws IOException {

        //String[] columnsFormat = new String[0];
        List<String> columnsFormat = new ArrayList<String>();
        String defaultFormat = "1;colctrl-1c"; //alt: col-md-,2,3,2,3,2
        String columnClass = "colctrl";

        if (componentProperties != null) {
            columnsFormat = Arrays.asList(componentProperties.get("layout",defaultFormat).split(";"));
            columnClass = componentProperties.get("class",columnClass);
        }

        openCol(columnsFormat, colNumber, columnClass, out, componentProperties, columnClassStyle);
    }

    /**
     * print end of column
     * @param par current par child of parSys being processed
     * @param out html output
     * @throws IOException
     */
    public static final void closeCol(Paragraph par, JspWriter out) throws IOException {
        //col end
        out.write("</div>");
    }


    /**
     * look ahead try find the end/break of col and return component before it if there is one
     * @param parSys list of pars being processed
     * @param par current par child of parSys being processed
     * @param out html output
     * @return last item in the column
     * @throws IOException
     */
    public static final Paragraph getLastItemInColumn(Object parSys, Paragraph par, JspWriter out) throws IOException {
        Paragraph lastItemPar = null;

        int parPosition = getListPosition(parSys, par);
        int listSize = getListSize(parSys);

        int lookAheadIndex = -1;

        for (int i = parPosition + 1; i < listSize; i++) {
            lookAheadIndex = i;
            Paragraph lpar = getListPar(parSys, i);

            //check if the component is a column, if it is stop
            Resource rpar = lpar.getResource();
            if (rpar.getResourceType().endsWith("/colctrl")) {
                break;
            }

        }

        //found end, lets get item before this one
        if (lookAheadIndex >= parPosition + 2) {
            //this is the position of last item in the column
            int lastColumnItemIndex = lookAheadIndex - 1;

            //grab the item from list
            lastItemPar = getListPar(parSys, lastColumnItemIndex);
        }

        return lastItemPar;
    }

    /**
     * return position of a par in the list.
     * @param parSys parent
     * @param par par to check in parent
     * @return return index of parsys in parent
     */
    public static int getListPosition(Object parSys, Paragraph par) throws IOException {

        if (parSys instanceof ParagraphSystem) {
            return ((ParagraphSystem) parSys).paragraphs().indexOf(par);
        }

        if (parSys instanceof LinkedList) {
            return ((LinkedList) parSys).indexOf(par);
        }

        return -1;
    }

    /**
     * return the size of the par list.
     * @param parSys parsys to check
     * @return size of parsys
     */
    public static int getListSize(Object parSys) throws IOException {

        if (parSys instanceof ParagraphSystem) {
            return ((ParagraphSystem) parSys).paragraphs().size();
        }

        if (parSys instanceof LinkedList) {
            return ((LinkedList) parSys).size();
        }

        return -1;
    }

    /**
     * return a par from par list at an index.
     * @param parSys gets item from parsys
     * @param index index to return
     * @return result parsys or null
     */
    public static Paragraph getListPar(Object parSys, int index) throws IOException {

        if (parSys instanceof ParagraphSystem) {
            return ((ParagraphSystem) parSys).paragraphs().get(index);
        }

        if (parSys instanceof LinkedList) {
            return (Paragraph) ((LinkedList) parSys).get(index);
        }

        return null;
    }

    /**
     * Force output of a Start/Top Edit bar/placeholder for a component
     * @param resource Current component resource being processed
     * @param slingRequest current sling request
     * @param slingResponse current sling response
     * @param editContext current cq edit context
     * @param out current jsp output
     * @param componentStyle component style
     * @param title title to use for the toolbar and placeholder
     * @throws IOException
     * @throws ServletException
     */
    public static final void includeEditRowStart(Resource resource, SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse, EditContext editContext, JspWriter out, Map<String, Object> componentStyle, String title) throws IOException, ServletException {
        //draw edit start placeholder
        WCMMode currentMode = WCMMode.fromRequest(slingRequest);

        //SHOW EDIT BAR IN EDIT MODE AND IN TOUCH-PREVIEW MODE
        if (editContext != null && (currentMode.equals(WCMMode.EDIT) || (currentMode.equals(WCMMode.PREVIEW) && UIMode(slingRequest).toUpperCase().equals(WCM_AUTHORING_MODE_COOKIE_VALUE_TOUCH)))) {
            // force draw 'edit' bar
            out.write("<div class=\"section\">");

            out.flush();

            editContext.setAttribute("currentResource", resource);

            out.write(Placeholder.getDefaultPlaceholder(slingRequest, title, "", "cq-marker-start"));

            editContext.getEditConfig().getToolbar().add(0, new Toolbar.Separator());
            editContext.getEditConfig().getToolbar().add(0, new Toolbar.Label(title));
            // disable ordering to get consistent behavior
            editContext.getEditConfig().setOrderable(false);

            out.flush();

            //linguistically should be includeProlog, but this does not give right behaviour
            editContext.includeEpilog(slingRequest, slingResponse, WCMMode.EDIT);

            out.write("</div>");
        }

    }

    /**
     * Force output of a End/Bottom Edit bar/placeholder for a component
     * @param resource Current component resource being processed
     * @param slingRequest current sling request
     * @param slingResponse current sling response
     * @param editContext current cq edit context
     * @param out current jsp output
     * @param componentStyle component style
     * @param title title to use for the toolbar and placeholder
     * @throws IOException
     * @throws ServletException
     */
    public static final void includeEditRowEnd(Resource resource, SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse, EditContext editContext, JspWriter out, Map<String, Object> componentStyle, String title) throws IOException, ServletException {
        //draw edit start placeholder
        WCMMode currentMode = WCMMode.fromRequest(slingRequest);

        //SHOW EDIT BAR IN EDIT MODE AND IN TOUCH-PREVIEW MODE
        if (editContext != null && (currentMode.equals(WCMMode.EDIT) || (currentMode.equals(WCMMode.PREVIEW) && UIMode(slingRequest).toUpperCase().equals(WCM_AUTHORING_MODE_COOKIE_VALUE_TOUCH)))) {
            // force draw 'edit' bar

            out.write("<div class=\"section\">");
            out.flush();

            editContext.setAttribute("currentResource", resource);

            //String textEnd = I18n.get(slingRequest, "End of Row with {0} Cells", "{0} is the placeholder for the nr of columns", numCols);
            out.write(Placeholder.getDefaultPlaceholder(slingRequest, title, "", "cq-marker-end"));

            editContext.getEditConfig().getToolbar().clear();
            editContext.getEditConfig().getToolbar().add(
                    new Toolbar.Label(title));

            // disable ordering to get consistent behavior
            editContext.getEditConfig().setOrderable(false);
            // set the content path to a fake one, since this editbar
            // is not used for editing and we need to avoid collisions with
            // the 'insert' bar with the same content path.
            editContext.setContentPath(resource.getPath() + "_fake");

            out.flush();

            editContext.includeEpilog(slingRequest, slingResponse, WCMMode.fromRequest(slingRequest));

            out.write("</div>");
        }

    }

    /**
     * Print CQ edit controls
     * @param resource
     * @param slingRequest
     * @param slingResponse
     * @param editContext
     * @param out
     * @param componentStyle
     * @param title
     * @throws IOException
     * @throws ServletException
     */
    public static final void includeEditContext(Resource resource, SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse, EditContext editContext, JspWriter out, Map<String, Object> componentStyle, String title) throws IOException, ServletException {

        out.flush();

        editContext.includeEpilog(slingRequest, slingResponse, WCMMode.fromRequest(slingRequest));

    }

    /**
     * return count of child element in iterator.
     * @param itr children iterator
     * @return count of children
     */
    public static Integer getChildCount(final Iterator<Resource> itr) {
        Integer count = 0;
        while (itr.hasNext()) {
            count++;
            itr.next();
        }
        return count;
    }

    /**
     * return last element in the iterator.
     * @param itr iterator to check
     * @return last resource
     */
    public static Resource getLastElement(final Iterator<Resource> itr) {
        Resource lastResource = null;
        if (itr != null) { //quick fail
            if (itr.hasNext()) { //quick fail
                lastResource = itr.next();
                while (itr.hasNext()) {
                    lastResource = itr.next();
                }
            }
        }
        return lastResource;
    }

    /**
     * return first element in the iterator.
     * @param itr iterator to check
     * @return first element
     */
    public static Resource getFirstElement(final Iterator<Resource> itr) {
        Resource lastResource = null;
        if (itr != null) { //quick fail
            if (itr.hasNext()) { //quick fail
                lastResource = itr.next();
            }
        }
        return lastResource;
    }


    /**
     * write opening tags for the column for Resource component, also check if base alignment is required based
     * @param resContainer container for the current col
     * @param resCol current resource
     * @param out html output
     * @param componentStyle style of container resource
     * @param currColNumber current column number
     * @throws IOException
     */
    public static final void openCol(Resource resContainer, Resource resCol, JspWriter out, HashMap<String, Object> componentStyle, Integer currColNumber) throws IOException, RepositoryException {
        String layout = (String) componentStyle.get(COL_CTL_LAYOUT);

        //expected width format: col-md-,4,4,4
        String[] width = layout.split(","); // parContainer.getBaseCssClass().split(",");

        //print column start
        if (width.length > 1) {
            // take the [0] = [col-md-] and add to it width by current column number
            out.write("<div class='"+ COLUMN_CLASS +" " + width[0] + width[currColNumber] + "'>");

        } else {
            // take the [0] = [col-md-] and add to it width by current column number
            out.write("<div class='"+ COLUMN_CLASS + " " + width[0] + "'>");
        }
    }



    /**
     * return current paragraph control type.
     * @param resource current paragpraph resource
     * @return return type of parsys
     */
    public static Paragraph.Type currentControlType(Resource resource) {
        ValueMap resProp = resource.adaptTo(ValueMap.class);
        if (resProp != null) {
            return Paragraph.Type.valueOf(resProp.get(COL_CTL_TYPE, "start").toUpperCase());
        }
        return Paragraph.Type.NORMAL;
    }

    /**
     * return current UI Mode, Classic or Touch
     * @param slingRequest
     * @return
     */
    public static String UIMode(SlingHttpServletRequest slingRequest) {
        if (slingRequest.getCookie(WCM_AUTHORING_MODE_COOKIE) != null) {
            return slingRequest.getCookie(WCM_AUTHORING_MODE_COOKIE).getValue();
        }
        return "";

    }
}
