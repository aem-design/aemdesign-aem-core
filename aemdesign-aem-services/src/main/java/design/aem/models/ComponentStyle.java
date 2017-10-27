package design.aem.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class)
public class ComponentStyle {

    public static final String KEY_TAG_VALUE = "value";

    public static final String DEFAULT_ATTRIBUTE_NAME = "componentStyle";

    private String componentModifiers;

    private List<String> modifiers;

    private final Resource resource;

    public ComponentStyle(Resource resource) {
        this.resource = resource;
    }

    @PostConstruct
    protected void postConstruct() {
        if (this.resource != null) {
            TagManager tagManager = this.resource.getResourceResolver().adaptTo(TagManager.class);
            ValueMap properties = this.resource.getValueMap();

            modifiers = new ArrayList<String>();

            for (String modifier : properties.get("componentModifiers", new String[] {})) {
                Tag tag = tagManager.resolve(modifier);

                if (tag != null) {
                    Resource valueMapRes = this.resource.getResourceResolver().resolve(tag.getPath());
                    if (!ResourceUtil.isNonExistingResource(valueMapRes)) {

                        ValueMap valueMap = valueMapRes.getValueMap();

                        String value = valueMap.get(KEY_TAG_VALUE, String.class);

                        if (StringUtils.isNotBlank(value)) {
                            modifiers.add(value);
                        }
                    }
                }
            }

            this.componentModifiers = StringUtils.join(this.modifiers, " ");
        }
    }

    public String getComponentModifiers() {
        return componentModifiers;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }
}
