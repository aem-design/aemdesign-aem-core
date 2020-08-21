package design.aem.impl;

import com.drew.lang.annotations.NotNull;
import design.aem.models.v3.Component;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_VARIANT;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = Component.class
)
public class ComponentImpl extends AbstractComponentImpl {
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = DEFAULT_VARIANT)
    protected String variant;

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
