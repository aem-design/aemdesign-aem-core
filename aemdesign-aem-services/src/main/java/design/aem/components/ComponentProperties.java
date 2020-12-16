package design.aem.components;

import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static design.aem.components.ComponentField.FIELD_VALUES_ARE_ATTRIBUTES;
import static design.aem.utils.components.ComponentsUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ComponentProperties extends ValueMapDecorator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ComponentProperties.class);

    public AttrBuilder attr; //NOSONAR use simpler patter as a util
    public ArrayList<ComponentField> expressionFields; //NOSONAR used by components to evaluate values

    /***
     * <p>Created empty map.</p>
     */
    @SuppressWarnings("unchecked")
    public ComponentProperties() {
        super(new HashMap());
    }

    /***
     * <p>Creates a new wrapper around a given map.</p>
     *
     * @param base wrapped object
     */
    public ComponentProperties(Map<String, Object> base) {
        super(base);
    }

    /**
     * override default equals
     * @param obj source componentProperties
     * @return true of same as self
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        return Objects.deepEquals(this, obj);
    }

    /**
     * generate object hashcode with seed
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int result = 31 * (this.expressionFields != null ? this.expressionFields.hashCode() : 0);
        result = 31 * result + (this.attr != null ? this.attr.hashCode() : 0);
        return result;
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        if (map != null) {
            super.putAll(map);
        }
    }

    /***
     * <p>put map into existing map.</p>
     * @param map new entries
     * @param update update entries, skip if non blank value exist
     */
    @SuppressWarnings({"squid:S135"})
    public void putAll(Map<? extends String, ?> map, Boolean update) { //NOSONAR some things need to
        // be complicated
        if (map != null) {
            if (update == null || !update) {
                //on null or not update do putAll
                super.putAll(map);
                return;
            }
            for (Entry<? extends String, ?> entry : map.entrySet()) {
                Object updatedValue = null;
                if (super.containsKey(entry.getKey())) {
                    //skip if non blank value exist
                    Object currentValue = super.get(entry.getKey());
                    Object newValue = entry.getValue();
                    if (currentValue != null && newValue != null) {
                        if (newValue.getClass().isArray()) {
                            // if newValue is empty don't do anything
                            if (ArrayUtils.getLength(newValue) == 0) {
                                continue;
                            } else {
                                //test if currentValue is not empty
                                if (currentValue.getClass().isArray() && ArrayUtils.getLength(currentValue) != 0) {
                                    //if its an empty array
                                    LOGGER.warn("skip: {} current value it not empty array, merging", entry.getKey());
                                    Object[] currentValueArray = (Object[])currentValue;
                                    Object[] newValueArray =  (Object[])newValue;

                                    List<Object> updatedValueList = new ArrayList<>();
                                    Collections.addAll(updatedValueList,currentValueArray);

                                    for (Object newValueItem : newValueArray){
                                        if (!updatedValueList.contains(newValueItem)) {
                                            updatedValueList.add(newValueItem);
                                        }
                                    }
//                                    continue;
                                    updatedValue = updatedValueList.toArray();
                                }
                            }
                        } else {
                            // if newValue is empty don't do anything
                            if (StringUtils.isEmpty(newValue.toString())) {
                                continue;
                            }
                        }
                    }
                }
                super.put(entry.getKey(), (updatedValue == null ? entry.getValue() : updatedValue));
            }
        }
    }

    /**
     * evaluate expression fields in component properties that have default value as expression
     */
    @SuppressWarnings({"squid:S3776"})
    public void evaluateExpressionFields() {

        JexlEngine jexl = new JexlBuilder().create();
        JxltEngine jxlt = jexl.createJxltEngine();
        JexlContext jc = new MapContext(this);

        if (expressionFields != null) {
            for (ComponentField field : expressionFields) {

                //remember field default value expression
                String defaultValueExpression = "";
                Object fieldDefaultValue = field.getDefaultValue();
                if (fieldDefaultValue instanceof String) {
                    defaultValueExpression = fieldDefaultValue.toString();
                }

                try {
                    //process non-multi value elements
                    if (field.getValue() != null) {
                        if (!field.getValue().getClass().isArray()) {

                            //get current field value
                            Object fieldValue = this.get(field.getFieldName(), null);

                            //use default expression if item does not have expression
                            String valueExpression = defaultValueExpression;
                            //if field value is expression use it
                            if (isStringRegex((String)fieldValue)) {
                                valueExpression = (String)fieldValue;
                            }

                            Object expressonResult = evaluateExpressionWithValue(jxlt, jc, valueExpression, fieldValue);

                            if (expressonResult != null) {

                                //update field value
                                this.put(field.getFieldName(), expressonResult);
                                //save field value into data attribute
                                this.attr.set(field.getDataAttributeName(), (String) expressonResult);
                            }
                        } else {

                            //get current field value
                            String[] values = (String[])field.getValue();

                            if (!ArrayUtils.isEmpty(values)) {
                                //loop though array and evaluate each item value
                                for (int i = 0; i < values.length; i++) {
                                    //use default expression if item does not have expression
                                    String valueExpression = defaultValueExpression;
                                    //if item value is expression use it over default
                                    if (isStringRegex(values[i])) {
                                        valueExpression = values[i];
                                    }
                                    values[i] = (String) evaluateExpressionWithValue(jxlt, jc, valueExpression, values[i]);
                                }
                            } else {
                                values = ArrayUtils.add(values,(String) evaluateExpressionWithValue(jxlt, jc, defaultValueExpression, null));
                            }

                            //update evaluated values
                            this.put(field.getFieldName(), values);

                            //add values to data attributes if required
                            if (isNotEmpty(field.getDataAttributeName())) {
                                if (field.getDataAttributeName().equals(FIELD_VALUES_ARE_ATTRIBUTES)) {
                                    for (String value : values) {
                                        if (!value.contains("=")) {
                                            this.attr.add(value, "true");
                                        } else {
                                            String[] items = value.split("=");
                                            this.attr.add(items[0], StringUtils.substringBetween(items[1], "\"", "\""));
                                        }
                                    }
                                } else {
                                    //save values as comma delimited array
                                    this.attr.add(field.getDataAttributeName(), StringUtils.join((String[]) field.getValue(), FIELD_DATA_ARRAY_SEPARATOR));
                                }

                            }


                        }
                    }
                } catch (JexlException jex) {
                    LOGGER.error("evaluateExpressionFields: field={},JexlException={}", field, jex);
                } catch (Exception ex) {
                    LOGGER.error("evaluateExpressionFields: field={},Exception={}", field, ex);
                }

            }
        }
    }

    /***
     * get ordered list of items
     */
    public TreeMap<String, Object> ordered() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (Entry<? extends String, Object> entry : super.entrySet()) {
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return treeMap;
    }
}
