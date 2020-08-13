package design.aem.models.v2.layout;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.components.Toolbar;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static design.aem.utils.components.CommonUtil.tryParseInt;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class Columns extends BaseComponent {
    private static final String COLUMN_CLASS = "col-sm"; //gets added to cols
    private static final String ROW_CLASS = "row"; //gets added to rows
    private static final String COLUMNS_CLASS = "parsys_column"; //gets added to rows and columns
    private static final String FIELD_LAYOUT = "layout";
    private static final String FIELD_NUMBER_OF_COLUMNS = "numCols";
    private static final String COLUMN_CLASS_FORMAT = "{0} {1} {2} {3}";

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

    public void ready() {
        I18n i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "columns";
        final String DEFAULT_LAYOUT = "1;colctrl-1c";
        final String COMPONENT_NAMESPACE = "aemdesign.components.layout.colctrl";
        final String COMPONENT_NAMESPACE_PROPERTIES = ".componentProperties";
        final String COMPONENT_NAMESPACE_CURRENTCOLUMN = ".currentColumn";

        /*
          Component Fields Helper

          Structure:
          1 required - property name,
          2 required - default value,
          3 optional - name of component attribute to add value into
          4 optional - canonical name of class for handling multivalues, String or Tag
         */
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_LAYOUT, DEFAULT_LAYOUT},
            {DETAILS_COLUMNS_LAYOUT_CLASS_SMALL, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_COLUMNS_LAYOUT_CLASS_LARGE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE, new String[]{}, "", Tag.class.getCanonicalName()},
            {DETAILS_COLUMNS_LAYOUT_ROW_CLASS, new String[]{}, "", Tag.class.getCanonicalName()},
        });


        placeholderText = "";

        ValueMap resourceProperties = getResource().adaptTo(ValueMap.class);
        String controlTypeString = "";
        if (resourceProperties != null) {
            controlTypeString = resourceProperties.get("controlType", "");
        }

        controlType = Type.fromString(controlTypeString);

        //disable component decoration when in disabled mode
        if (!getWcmMode().isEdit()) {
            getComponentContext().setDecorate(false);
            getComponentContext().setDecorationTagName("");
        }


        String columnClassSmall = "";
        String columnClassMedium = "";
        String columnClassLarge = "";
        String columnClassXLarge = "";
        String aRowClass = "";
        String aColumnClass = "";

        switch (controlType) {
            case START:


                componentProperties = ComponentsUtil.getComponentProperties(
                    this,
                    getResource(),
                    componentFields,
                    DEFAULT_FIELDS_STYLE,
                    DEFAULT_FIELDS_ACCESSIBILITY);

                String currentLayout = componentProperties.get(FIELD_LAYOUT, DEFAULT_LAYOUT);
                if (currentLayout.contains(";")) {
                    //remove first number which is the number of columns
                    componentProperties.put(FIELD_LAYOUT, currentLayout.substring(currentLayout.indexOf(';') + 1));
                    String numColsString = currentLayout.split(";")[0];
                    numCols = tryParseInt(numColsString, 0);
                }

                componentProperties.put(FIELD_NUMBER_OF_COLUMNS, numCols);

                placeholderText = getDefaultLabelIfEmpty("placeholderTextStart", DEFAULT_I18N_CATEGORY, "Start of {0} Columns", i18n, Integer.toString(numCols));

                columnClassSmall = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_SMALL, "");
                columnClassMedium = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM, "");
                columnClassLarge = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_LARGE, "");
                columnClassXLarge = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE, "");
                aRowClass = componentProperties.get(DETAILS_COLUMNS_LAYOUT_ROW_CLASS, "");
                aColumnClass = MessageFormat.format(COLUMN_CLASS_FORMAT, columnClassSmall, columnClassMedium, columnClassLarge, columnClassXLarge).trim();

                columnClass = getColumnClass(currentColumn, componentProperties, aColumnClass);
                columnsClass = getColumnsClass(numCols);
                rowClass = getRowClass(aRowClass);

                componentProperties.attr.add("class", columnsClass);

                componentProperties.put(COMPONENT_ATTRIBUTES,
                    buildAttributesString(componentProperties.attr.getAttributes(), xss));

                componentProperties.put("rowClass", rowClass);
                componentProperties.put("columnsClass", columnsClass);


                getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES), componentProperties);
                getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);

                if (!getWcmMode().isDisabled()) {
                    getEditContext().getEditConfig().getToolbar().add(0, new Toolbar.Separator());
                    getEditContext().getEditConfig().getToolbar().add(0, new Toolbar.Label(placeholderText));
                    // disable ordering to get consistent behavior
                    getEditContext().getEditConfig().setOrderable(false);

                }


                break;
            case END:

                componentProperties = (ComponentProperties) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));

                currentColumn = ((Integer) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)));

                numCols = componentProperties.get(FIELD_NUMBER_OF_COLUMNS, numCols);

                placeholderText = getDefaultLabelIfEmpty("placeholderTextEnd", DEFAULT_I18N_CATEGORY, "End of {0} Columns", i18n, Integer.toString(numCols));

                getRequest().removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                getRequest().removeAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN));

                if (!getWcmMode().isDisabled()) {
                    getEditContext().getEditConfig().getToolbar().clear();
                    // disable ordering to get consistent behavior
                    getEditContext().getEditConfig().setOrderable(false);
                }

                break;
            case BREAK:
                if (getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES)) != null &&
                    getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)) != null) {

                    componentProperties = (ComponentProperties) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_PROPERTIES));
                    currentColumn = ((Integer) getRequest().getAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN)));


                    columnClassSmall = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_SMALL, "");
                    columnClassMedium = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_MEDIUM, "");
                    columnClassLarge = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_LARGE, "");
                    columnClassXLarge = componentProperties.get(DETAILS_COLUMNS_LAYOUT_CLASS_XLARGE, "");
                    aColumnClass = MessageFormat.format(COLUMN_CLASS_FORMAT, columnClassSmall, columnClassMedium, columnClassLarge, columnClassXLarge).trim();

                    columnClass = getColumnClass(currentColumn, componentProperties, aColumnClass);

                    numCols = componentProperties.get(FIELD_NUMBER_OF_COLUMNS, numCols);

                    getRequest().setAttribute(COMPONENT_NAMESPACE.concat(COMPONENT_NAMESPACE_CURRENTCOLUMN), currentColumn + 1);

                    placeholderText = getDefaultLabelIfEmpty("placeholderTextBreak", DEFAULT_I18N_CATEGORY, "Columns Break {0} of {1}", i18n, Integer.toString(currentColumn + 1), Integer.toString(numCols - 1));


                }
                break;
            case NORMAL:
                break;
        }


    }

    @SuppressWarnings("Duplicates")
    final String getColumnClass(Integer colNumber, ComponentProperties componentProperties, String columnClassStyle) {


        List<String> columnsFormat = new ArrayList<>();
        String defaultFormat = "1;colctrl-1c"; //alt: col-md-,2,3,2,3,2
        String columnsClassName = "colctrl";


        if (componentProperties != null) {
            columnsFormat = Arrays.asList(componentProperties.get(FIELD_LAYOUT, defaultFormat).split(";"));
            columnsClassName = componentProperties.get("class", columnsClassName);
        }

        if (!columnsFormat.isEmpty() && columnsFormat.get(0).contains(",")) {
            //take the [0] = [col-md-] and add to it width by current column number
            StringBuilder columnClassBuilder = new StringBuilder();
            for (int i = 0; i < columnsFormat.size(); i++) {
                String spacer = (i == columnsFormat.size() - 1 ? "" : " ");
                columnClassBuilder.append(columnsFormat.get(i).split(",")[0]);
                columnClassBuilder.append(columnsFormat.get(i).split(",")[colNumber + 1]);
                columnClassBuilder.append(spacer);
            }
            return MessageFormat.format(COLUMN_CLASS_FORMAT, COLUMNS_CLASS, COLUMN_CLASS, columnClassBuilder, columnClassStyle); //EXTENDED
        } else {
            return MessageFormat.format(COLUMN_CLASS_FORMAT, COLUMNS_CLASS, COLUMN_CLASS, columnsClassName, columnClassStyle); //ORIGINAL
        }
    }

    final String getColumnsClass(Integer numCols) {

        return MessageFormat.format("colctrl-{0}c", numCols);

    }

    final String getRowClass(String rowClass) {

        return MessageFormat.format("{0} {1} {2}", COLUMNS_CLASS, ROW_CLASS, rowClass);
    }

    private enum Type {
        START("start"),
        END("end"),
        BREAK("break"),
        NORMAL("");

        private final String value;

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
