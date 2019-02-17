package design.aem.models.v2.layout;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.Toolbar;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static design.aem.utils.components.CommonUtil.tryParseInt;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class Columns extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Columns.class);

    private final String COLUMN_CLASS = "col-sm"; //gets added to cols
    private final String ROW_CLASS = "row"; //gets added to rows
    private final String COLUMNS_CLASS = "parsys_column"; //gets added to rows and columns



    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private Type controlType;

    public Type getControlType() {
        return controlType;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private String placeholderText;

    public String getPlaceholderText() {
        return placeholderText;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private String columnClass;

    public String getColumnClass() {
        return columnClass;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private String columnsClass;

    public String getColumnsClass() {
        return columnsClass;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private String rowClass;

    public String getRowClass() {
        return rowClass;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 0)
    private int currentColumn;

    public int getCurrentColumn() {
        return currentColumn;
    }

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 0)
    private int numCols;

    public int getNumCols() {
        return numCols;
    }

    @Override
    public void activate() throws Exception {
        LOGGER.error("columns loading");

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "columns";
        final String DEFAULT_LAYOUT = "1;colctrl-1c";
        final String COMPONENT_NAMESPACE = "aemdesign.components.layout.colctrl";
        final String COMPONENT_NAMESPACE_PROPERTIES = ".componentProperties";
        final String COMPONENT_NAMESPACE_CURRENTCOLUMN = ".currentColumn";

        // {
        //   { name, defaultValue, attributeName, valueTypeClass }
        // }
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"layout", DEFAULT_LAYOUT},
                {DETAILS_COLUMNS_LAYOUT_CLASS_SMALL, new String[]{}, " ", Tag.class.getCanonicalName()},
                {DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM, new String[]{}," ", Tag.class.getCanonicalName()},
                {DETAILS_COLUMNS_LAYOUT_CLASS_LARGE, new String[]{}," ", Tag.class.getCanonicalName()},
                {DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE, new String[]{}," ", Tag.class.getCanonicalName()},
                {DETAILS_COLUMNS_LAYOUT_ROW_CLASS, new String[]{}," ", Tag.class.getCanonicalName()},
        };


        placeholderText = "";

//        String layout = ConstantsUtil.defaultLayout;
        String controlTypeString = getResource().adaptTo(ValueMap.class).get("controlType", "");

        LOGGER.error("columns content controlType: {}",controlTypeString);

        controlType = Type.fromString(controlTypeString);

        LOGGER.error("columns controlType: {}",controlType);

        //disable component decoration when in disabled mode
        if (!getWcmMode().isEdit()) {
            getComponentContext().setDecorate(false);
            getComponentContext().setDecorationTagName("");
//                    getComponentContext().setDefaultDecorationTagName("");
        }

        switch (controlType) {
            case START:


                componentProperties = ComponentsUtil.getComponentProperties(
                        this,
                        getResource(),
                        componentFields,
                        DEFAULT_FIELDS_STYLE,
                        DEFAULT_FIELDS_ACCESSIBILITY);

                componentProperties.put("numCols",numCols);

                String currentLayout = componentProperties.get("layout",DEFAULT_LAYOUT);
                if (currentLayout.contains(";")) {
                    componentProperties.put("layout",currentLayout.split(";")[1]);
                    String numColsString = currentLayout.split(";")[0];
                    numCols = tryParseInt(numColsString, 0);
                    componentProperties.put("numCols",numCols);
                }

                placeholderText = getDefaultLabelIfEmpty("placeholderTextStart", DEFAULT_I18N_CATEGORY, "Start of {0} Columns", _i18n,  Integer.toString(numCols));

                columnClass = getColumnClass(currentColumn, componentProperties, columnClass);
                columnsClass = getColumnsClass(numCols, componentProperties);
                rowClass = getRowClass(componentProperties, rowClass);


                componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
                componentProperties.put("rowClass", rowClass);
                componentProperties.put("columnsClass", columnsClass);


                getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES), componentProperties);
                getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn++);

                if (!getWcmMode().isDisabled()) {
                    getEditContext().getEditConfig().getToolbar().add(0, new Toolbar.Separator());
                    getEditContext().getEditConfig().getToolbar().add(0, new Toolbar.Label(placeholderText));
                    // disable ordering to get consistent behavior
                    getEditContext().getEditConfig().setOrderable(false);

                }


                LOGGER.error("columns start");

                break;
            case END:

                componentProperties = (ComponentProperties) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));

                currentColumn = ((Integer) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)));

                numCols = componentProperties.get("numCols",numCols);

                placeholderText = getDefaultLabelIfEmpty("placeholderTextEnd", DEFAULT_I18N_CATEGORY, "End of {0} Columns", _i18n, Integer.toString(numCols));

                getRequest().removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                getRequest().removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN));

                if (!getWcmMode().isDisabled()) {
                    getEditContext().getEditConfig().getToolbar().clear();
                    getEditContext().getEditConfig().getToolbar().add(0, new Toolbar.Label(placeholderText));
                    // disable ordering to get consistent behavior
                    getEditContext().getEditConfig().setOrderable(false);
                    getEditContext().setContentPath(getResource().getPath().concat("_fake"));
                }

                LOGGER.error("columns end");

                break;
            case BREAK:
                if (getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES)) != null &&
                        getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)) != null) {

                    componentProperties = (ComponentProperties) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                    currentColumn = ((Integer) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)));

                    numCols = componentProperties.get("numCols",numCols);


                    getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);

                    placeholderText = getDefaultLabelIfEmpty("placeholderTextBreak", DEFAULT_I18N_CATEGORY, "Columns Break {0} of {1}", _i18n, Integer.toString(currentColumn + 1), Integer.toString(numCols-1));

                    columnClass = getColumnClass(currentColumn, componentProperties, columnClass);


                }
                LOGGER.error("break");
                break;
            case NORMAL:
                LOGGER.error("normal");
                break;
        }

        LOGGER.error("columns loaded");

    }

    @SuppressWarnings("Duplicates")
    final String getColumnClass(Integer colNumber, ComponentProperties componentProperties, String columnClassStyle) {

        //String[] columnsFormat = new String[0];
        List<String> columnsFormat = new ArrayList<String>();
        String defaultFormat = "1;colctrl-1c"; //alt: col-md-,2,3,2,3,2
        String columnClass = "colctrl";

        if (componentProperties != null) {
            columnsFormat = Arrays.asList(componentProperties.get("layout",defaultFormat).split(";"));
            columnClass = componentProperties.get("class",columnClass);
        }

        if (columnsFormat.size() >= 1 && columnsFormat.get(0).contains(",")) {
            //take the [0] = [col-md-] and add to it width by current column number
            StringBuilder columnClassBuilder = new StringBuilder();
            for(int i=0; i < columnsFormat.size(); i++){
                String spacer = (i == columnsFormat.size()-1 ? "" : " ");
                columnClassBuilder.append(columnsFormat.get(i).split(",")[0]);
                columnClassBuilder.append(columnsFormat.get(i).split(",")[colNumber + 1]);
                columnClassBuilder.append(spacer);
            }
             return MessageFormat.format("{0} {1} {2} {3}",COLUMNS_CLASS, COLUMN_CLASS, columnClassBuilder.toString(), columnClassStyle); //EXTENDED
        } else {
            return MessageFormat.format("{0} {1} {2} {3}",COLUMNS_CLASS, COLUMN_CLASS, columnClass, columnClassStyle); //ORIGINAL
        }
    }

    final String getColumnsClass(Integer numCols, ComponentProperties componentProperties) {

        return MessageFormat.format("colctrl-{0}c",numCols);

    }

    final String getRowClass(ComponentProperties componentProperties, String rowClass) {

        return MessageFormat.format("{0} {1} {2}",COLUMNS_CLASS, ROW_CLASS, rowClass);
    }

    private enum Type {
        START("start"),
        END("end"),
        BREAK("break"),
        NORMAL("");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public static Type fromString(String value) {
            for (Type s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }


}