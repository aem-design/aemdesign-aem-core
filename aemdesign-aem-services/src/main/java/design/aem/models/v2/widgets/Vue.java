package design.aem.models.v2.widgets;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.AttrBuilder;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.NameConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.TagUtil;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAP;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAP_API_KEY;

public class Vue extends WCMUsePojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vue.class);

    public ComponentProperties componentProperties = null;

    private AttrBuilder attrs = null;
    private StringBuilder componentHTML = new StringBuilder();
    private Map<String, String> configOutput = new HashMap<>();
    private Map<String, String> slots = new HashMap<>();

    @Override
    public void activate() {

        Object[][] componentFields = {
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"vueComponentName", StringUtils.EMPTY},
            {"analyticsName", StringUtils.EMPTY},
            {"analyticsLocation", StringUtils.EMPTY},
        };

        attrs = new AttrBuilder(getRequest(), getXSSAPI());
        componentProperties = ComponentsUtil.getComponentProperties(this, componentFields);

        String componentName = componentProperties.get("vueComponentName", StringUtils.EMPTY);

        // Retrieve the dynamic configuration for the component
        retrieveComponentConfigurationAndSlots(componentName);

        String googleMapApiKey = getCloudConfigProperty((InheritanceValueMap)getPageProperties(),DEFAULT_CLOUDCONFIG_GOOGLEMAP,DEFAULT_CLOUDCONFIG_GOOGLEMAP_API_KEY,getSlingScriptHelper());
        attrs.add("google-maps-key", googleMapApiKey);

        // Add any analytics attributes to the Vue component
        setAnalyticsAttributes();

        // Construct the component HTML
        constructComponentHTML(componentName);

        // Debugging output for authors & devs
        componentProperties.put("configOutput", configOutput);
    }

    /***
     * set component analytics attributes
     */
    private void setAnalyticsAttributes() {
        Map<String, String> analyticsAttrs = new HashMap<>();
        analyticsAttrs.put("analytics-name", "analyticsName");
        analyticsAttrs.put("analytics-location", "analyticsLocation");

        for (Map.Entry<String, String> attr : analyticsAttrs.entrySet()) {
            String value = componentProperties.get(attr.getValue(), StringUtils.EMPTY);

            if (StringUtils.isNotEmpty(value)) {
                attrs.add(attr.getKey(), value);
            }
        }
    }

    /***
     * returns a attributes from given component node.
     * @param componentName component node to read attributes from
     */
    @SuppressWarnings("squid:S3776")
    private void retrieveComponentConfigurationAndSlots(String componentName) {
        Resource resource = getResource();

        if (resource != null) {
            Node resourceNode = resource.adaptTo(Node.class);

            try {
                if (resourceNode != null && resourceNode.hasNode("dynamic")) {
                    Node dynamicNode = resourceNode.getNode("dynamic");
                    Node componentNode = dynamicNode.getNode(componentName);

                    if (componentNode != null) {
                        PropertyIterator properties = componentNode.getProperties();
                        NodeIterator nodes = componentNode.getNodes();

                        while (nodes.hasNext()) {
                            Node node = (Node) nodes.next();

                            if (node == null || node.getName().startsWith("jcr:")) {
                                continue;
                            }

                            handleComponentValue(componentName, node.getName(), StringUtils.EMPTY, node.getProperties());
                        }

                        while (properties.hasNext()) {
                            Property property = properties.nextProperty();
                            String name = property.getName();

                            if (name.startsWith("jcr:")) {
                                continue;
                            }

                            handleComponentValue(componentName, name, property.getValue().getString(), null);
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("[Vue Component] Unable to load all or part of the dynamic configuration for: {}", resource.getPath());
                LOGGER.error(ex.getMessage());
            }
        }
    }

    /***
     * evaluate component config and its specified fields with types
     * @param componentName component node to read config from
     * @param fieldName name of field to read from tag config
     * @param fieldValue field value to use or lookup
     * @param fieldProperties all field properties
     */
    @SuppressWarnings("squid:S3776")
    private void handleComponentValue(String componentName, String fieldName, String fieldValue, PropertyIterator fieldProperties) {
        JsonObject fieldConfig = getFieldTagConfig(componentName, fieldName);
        SightlyWCMMode wcmMode = getWcmMode();

        boolean isSlot = false;
        String slotName = StringUtils.EMPTY;

        if (fieldConfig != null && fieldConfig.has("field")) {
            String fieldType = fieldConfig.get("field").getAsString();

            // Autocompletion
            if (fieldType.equals("autocomplete")) {
                fieldValue = TagUtil.getTagValueAsAdmin(fieldValue, getSlingScriptHelper());
            }

            // Image/File upload
            if (fieldType.equals("fileUpload") && fieldProperties != null) {
                while (fieldProperties.hasNext()) {
                    Property property = fieldProperties.nextProperty();

                    try {
                        if (property.getName().equals("fileReference")) {
                            fieldValue = property.getString();
                            break;
                        }
                    } catch (RepositoryException ex) {
                        LOGGER.error("Unable to handle property iterator step!, {}", property);
                        LOGGER.error(ex.getLocalizedMessage());
                    }
                }
            }

            // Is this field a slot?
            isSlot = fieldConfig.has("slot");
            slotName = isSlot ? fieldConfig.get("slot").getAsString() : slotName;
        }

        if (isSlot) {
            slots.put(slotName, fieldValue);
        } else {
            attrs.add(fieldName, fieldValue);
        }

        // Add the config to some additional output when in the correct WCM Mode
        if (wcmMode.isEdit() || wcmMode.isPreview()) {
            configOutput.put(StringUtils.capitalize(fieldName), fieldValue);
        }
    }

    /***
     * get field config from tags
     * @param componentName component name
     * @param fieldName field name
     * @return returns json config object
     */
    private JsonObject getFieldTagConfig(String componentName, String fieldName) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = null;

        try {
            String componentPath = "/content/%s/%s/component-dialog/vue-widgets/%s/%s";
            String tenantName = TenantUtil.resolveTenantIdFromPath(getResource().getPath());
            String resourcePath = String.format(componentPath, NameConstants.PN_TAGS, tenantName, componentName, fieldName);

            Resource fieldResource = getResourceResolver().getResource(resourcePath);

            if (fieldResource == null) {
                throw new ResourceNotFoundException("Unable to get resource for: " + resourcePath);
            }

            String fieldValue = fieldResource.getValueMap().get("value", StringUtils.EMPTY);
            String json = new String(Base64.getDecoder().decode(fieldValue));
            JsonElement parsedJson = parser.parse(json);

            if (parsedJson.isJsonObject()) {
                jsonObject = parsedJson.getAsJsonObject();
            }
        } catch (Exception ex) {
            LOGGER.error("[Vue Component] Unable to parse JSON value for '{}' on component: '{}'", fieldName, componentName);
            LOGGER.error(ex.getMessage());
        }

        return jsonObject;
    }

    /***
     * create component HTML template
     * @param componentName component name to use
     */
    private void constructComponentHTML(String componentName) {
        componentHTML.append(String.format("<%s %s>", componentName, attrs.build()));

        if (slots.size() > 0) {
            for (Map.Entry<String, String> slot : slots.entrySet()) {
                componentHTML.append(String.format("<template v-slot:%s>%s</template>", slot.getKey(), slot.getValue()));
            }
        }

        componentHTML.append(String.format("</%s>", componentName));
    }

    /***
     * return component html
     * @return component html string
     */
    public String getComponentHTML() {
        return componentHTML.toString();
    }
}
