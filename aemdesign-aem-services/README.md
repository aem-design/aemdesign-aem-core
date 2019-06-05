#AEM Design - AEM Services

Service for AEM Common

##Guidelines

1. All code must pass checkstyle
2. All code must have 100% test coverage, if you don't like it avoid writing it.

##Branches

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

    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDetails.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }
    
    @Override
    public void activate() throws Exception {
        
        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());

        //COMPONENT STYLES
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