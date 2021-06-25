#AEM Design - AEM Services

Service for AEM Common

## Guidelines

1. All code must pass checkstyle
2. All code must have 100% test coverage, if you don't like it avoid writing it.

## Example Class

Use following template when creating new WCMUsePojo models

```java
package design.aem.models.v2.details;

import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import com.day.cq.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDetails extends BaseComponent {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }
    
    protected void ready() {
        
        com.day.cq.i18n.I18n i18n = new I18n(getRequest());

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
        });
        
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY,
            DEFAULT_FIELDS_ANALYTICS
        );
        
    }
}
```

## Component Field Helper

All components have a need to collect information about its content. 
To provide a more efficient and consistent mechanism to collect information across components method ```ComponentsUtil.getComponentProperties``` is used.
This method relies on a field definition structure that allows uniforms mechnismfor describing fields that component needs.
Following is an outline of a helper structure used by ```ComponentsUtil.getComponentProperties```.

```
    Object[][] componentFields = {
        {<field name>,<default value>,<data attribute name>,<handling type>},
    };
```

This structure provides a simple structured approach to specify which feelds to read.
Additionally number of common field maps exist to provide collection of content from nodes when shared dialogs are used.

### Field description and usage

All fields that are specified in Component Fields Helper block will be collected into a Key Value Map with Field Name being the Key in the map.
This then used by your templates for presentation.
This approach saves on needless creation of attributes and provides a simple mechanism to avoid duplicate logic repeated across all components.  

| Name                 | Description                                                                   |
|--------------------- |-------------------------------------------------------------------------------|
| Field Name           | - field name that will be read from the component node ```./<field name>```   |
| Default Value        | - default value to be used when field does not exist  |
|                       | - default value expression language statement that will be evaluated when reading the content ex. ```${value ? value : pageUrl}``` | 
|                       | - expressions statement will be re-evaluated after all fields have been collected |
|                       | - individual array items will also be evaluated after all fields have been collected  |
| Data Attribute Name  | - attribute to be used when outputting value of field into component HTML attributes typically ```data-<data attribute name>```  |
|                       | - multiple fields can be added to same attribute, example using multiple field ```class``` will result with all values combined into that data attribute |
| Handling Type        | - special handling for field when interpreting field values current example is ```Tag.class.getCanonicalName()``` which forces resolution of tags to their value attribute  |


### Field Usage Examples

Following are examples of Component Field Helper map. 

| Example                                                                                                                | Description                                                                                                                                                     |
|----------------------------------------------------------------------------------------------------------------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ```{FIELD_VARIANT, DEFAULT_VARIANT},```                                                                                | this will read your field and use default value if field does not exist                                                                                         |
| ```{FIELD_STYLE_COMPONENT_MODIFIERS, new String[]{}, "class", Tag.class.getCanonicalName()},```                        | this will read your field, resolve all tags to values and store it into specified data attribute                                                                |
| ```{FIELD_STYLE_COMPONENT_BOOLEANATTR, new String[]{}, FIELD_VALUES_ARE_ATTRIBUTES,,Tag.class.getCanonicalName()},```  | this will collect your fields as an array and will add it as boolean attribute to component, if values are Key-Value it will be added as key="value" attribute  |
| ```{FIELD_STYLE_COMPONENT_POSITIONX, "", "x"},```                                                                      | this will read value from node and add store it into attribute x, if value is not empty                                                                         |
| ```{FIELD_STYLE_COMPONENT_WIDTH, "${value ? 'width:' + value + 'px;' : ''}", "style"},```                             | this will field and evaluate an expression and store it into an attribute if value is not empty                                                                 |
