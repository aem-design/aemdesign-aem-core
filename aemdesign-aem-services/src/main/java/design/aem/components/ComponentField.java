/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.components;

public class ComponentField {

    private static final long serialVersionUID = -2404935286690975616L;

    public static final String FIELD_VALUES_ARE_ATTRIBUTES = " "; //NOSONAR underscores are fine

    private String fieldName;
    private Object defaultValue;
    private String dataAttributeName;
    private String multiValueClass;
    private String expression;
    private Object value;

    public ComponentField(String fieldName, Object defaultValue, String dataAttributeName, String mutliValueClass, String expression, Object value) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
        this.dataAttributeName = dataAttributeName;
        this.multiValueClass = mutliValueClass;
        this.expression = expression;
        this.value = value;
    }

    /**
     * Create component field using an array.
     *
     * @param fieldDefinitionArray Array positions
     *                             1 fieldName:           required - property name,
     *                             2 defaultValue:        required - default value,
     *                             3 dataAttributeName:   optional - name of component attribute to add value into, specifying "" will return values process as per canonical name
     *                             4 multiValueClass:     optional - canonical name of class for handling multivalues, String or Tag
     */
    public ComponentField(Object[] fieldDefinitionArray) {
        this.fieldName = (String) fieldDefinitionArray[0];
        this.defaultValue = fieldDefinitionArray[1];
        if (fieldDefinitionArray.length > 2) {
            this.dataAttributeName = (String) fieldDefinitionArray[2];
        }
        if (fieldDefinitionArray.length > 3) {
            this.multiValueClass = (String) fieldDefinitionArray[3];
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDataAttributeName() {
        return dataAttributeName;
    }

    public void setDataAttributeName(String dataAttributeName) {
        this.dataAttributeName = dataAttributeName;
    }

    public String getMultiValueClass() {
        return multiValueClass;
    }

    public void setMultiValueClass(String multiValueClass) {
        this.multiValueClass = multiValueClass;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ComponentField{" +
            "fieldName='" + fieldName + '\'' +
            ", defaultValue=" + defaultValue +
            ", dataAttributeName='" + dataAttributeName + '\'' +
            ", multiValueClass='" + multiValueClass + '\'' +
            ", expression='" + expression + '\'' +
            ", value=" + value +
            '}';
    }
}
