#AEM Design - AEM Services

Service for AEM Common

## Guidelines

1. All code must pass checkstyle
2. All code must have 100% test coverage, if you don't like it avoid writing it.

## Branches

Master      - has all components that
Dev         - has all dev changes ready to be merged into master
Research    - has all the R&D code from other projects prepared to merge into Dev branch



## Example Class

Use following template when creating new WCMUsePojo models

```java
package design.aem.models.v2.details;

import com.adobe.cq.sightly.WCMUsePojo;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.TagUtil;
import com.day.cq.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageDetails extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }
    
    @Override
    public void activate() throws Exception {
        
        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());

        //COMPONENT FIELDS
        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
        };
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


| Name                	| Description                                                                   |
|---------------------	|-------------------------------------------------------------------------------|
| Field Name          	| - field name that will be read from the component node ```./<field name>```   |
| Default Value       	| - default value to be used when field does not exist  |
|                       | - default value expression language statement that will be evaluated when reading the content ex. ```${value ? value : pageUrl}``` | 
|                       | - expressions statement will be re-evaluated after all fields have been collected |
|                       | - individual array items will also be evaluated after all fields have been collected 	|
| Data Attribute Name 	| - attribute to be used when outputting value of field into component HTML attributes typically ```data-<data attribute name>```  |
| Handling Type       	| - special handling for field when interpreting field values current example is ```Tag.class.getCanonicalName()``` which forces resolution of tags to their value attribute  |