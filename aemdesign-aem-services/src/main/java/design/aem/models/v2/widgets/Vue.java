package design.aem.models.v2.widgets;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.NameConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import design.aem.components.AttrBuilder;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.TagUtil;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_VARIANT;
import static design.aem.utils.components.ComponentsUtil.DETAILS_ANALYTICS_LABEL;
import static design.aem.utils.components.ComponentsUtil.DETAILS_ANALYTICS_LOCATION;
import static design.aem.utils.components.ComponentsUtil.FIELD_VARIANT;
import static design.aem.utils.components.ComponentsUtil.getCloudConfigProperty;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAPS;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAPS_API_KEY;

public class Vue extends BaseComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vue.class);

    private static final String FIELD_ANALYTICS_NAME = "analyticsName";
    private static final String FIELD_ANALYTICS_LOCATION = "analyticsLocation";
    private static final String FIELD_VUE_COMPONENT = "vueComponentName";

    private AttrBuilder attrs = null;
    private String componentName = StringUtils.EMPTY;

    private final StringBuilder componentHTML = new StringBuilder();
    private final Map<String, String> configOutput = new HashMap<>();
    private final Map<String, String> slots = new HashMap<>();
    private final Map<String, String> fieldToConfigMap = new HashMap<>();

    private JsonArray config;
    private Externalizer externalizer;
    private ResourceResolver resourceResolver;

    public void ready() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_ANALYTICS_NAME, StringUtils.EMPTY},
            {FIELD_ANALYTICS_LOCATION, StringUtils.EMPTY},
            {FIELD_VUE_COMPONENT, StringUtils.EMPTY},
        });

        componentProperties = ComponentsUtil.getComponentProperties(this, componentFields);

        try {
            attrs = new AttrBuilder(xss);
            componentName = componentProperties.get(FIELD_VUE_COMPONENT, StringUtils.EMPTY);
            resourceResolver = getResourceResolver();
            externalizer = resourceResolver.adaptTo(Externalizer.class);
        } catch (Exception ex) {
            LOGGER.error("Vue component activation failed!");
            LOGGER.error(ex.getLocalizedMessage());
        }

        // Don't go on any further if the component hasn't been configured yet
        if (!StringUtils.isNotEmpty(componentName)) {
            return;
        }

        // Retrieve and process the component config
        retrieveComponentConfig();

        // Retrieve the dynamic configuration for the component
        retrieveComponentConfigurationAndSlots();

        // Add any analytics attributes to the Vue component
        setAnalyticsAttributes();

        // Configuration via attributes
        setConfigurationAttributes();

        // Construct the component HTML
        constructComponentHTML();

        // Debugging output for authors & developers
        componentProperties.put("configOutput", configOutput);
    }

    /**
     * Define the configurations map that enables Vue components to link with the authored values.
     *
     * @return {@code Map<String, String>}
     */
    public Map<String, String> getConfigurationsMap() {
        Map<String, String> storedConfig = new HashMap<>();

        storedConfig.put(DEFAULT_CLOUDCONFIG_GOOGLEMAPS, getCloudConfiguration(
            DEFAULT_CLOUDCONFIG_GOOGLEMAPS,
            DEFAULT_CLOUDCONFIG_GOOGLEMAPS_API_KEY
        ));

        return storedConfig;
    }

    /**
     * Gets the cloud configuration value configured for the given {@code configName} and {@code configProperty}.
     *
     * @param configName     Cloud configuration name
     * @param configProperty JCR property name for the config
     * @return {@link String} representation of the config value
     */
    protected String getCloudConfiguration(String configName, String configProperty) {
        return getCloudConfigProperty(
            (InheritanceValueMap) getPageProperties(),
            configName,
            configProperty,
            getSlingScriptHelper()
        );
    }

    /**
     * Attempts to retrieve the component configuration from pre-stored JSON structures that have
     * been defined within content tags.
     */
    private void retrieveComponentConfig() {
        try {
            config = getComponentDataByKey("config").getAsJsonArray(); // NOSONAR
        } catch (Exception ex) {
            LOGGER.warn("Unable to retrieve the component configuration, this could mean it doesn't exist or is invalid.");
        }
    }

    /**
     * Retrieves the authored configuration and hands off the required values to {@link #handleComponentField}.
     */
    private void retrieveComponentConfigurationAndSlots() {
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

                        // Handle the nodes for the component configuration
                        while (nodes.hasNext()) {
                            Node node = (Node) nodes.next();

                            if (node == null || node.getName().startsWith("jcr:")) {
                                continue;
                            }

                            handleComponentField(node.getName(), StringUtils.EMPTY, node.getProperties());
                        }

                        // Handle the properties for the component configuration
                        while (properties.hasNext()) {
                            Property property = properties.nextProperty();
                            String name = property.getName();

                            if (name.startsWith("jcr:")) {
                                continue;
                            }

                            handleComponentField(name, property.getValue().getString(), null);
                        }
                    }
                }
            } catch (ValueFormatException ex) {
                LOGGER.error("ValueFormatException occurred for: {}", resource.getPath());
                LOGGER.error(ex.getLocalizedMessage());
            } catch (PathNotFoundException ex) {
                LOGGER.error("PathNotFoundException occurred for: {}", resource.getPath());
                LOGGER.error(ex.getLocalizedMessage());
            } catch (RepositoryException ex) {
                LOGGER.error("RepositoryException occurred for: {}", resource.getPath());
                LOGGER.error(ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Binds the given {@code field} to either a slot or HTML attribute based on the fields configuration
     * set for the current component.
     *
     * @param field      Name of the component field
     * @param value      Value of the component field
     * @param properties Any properties that require additional parsing
     * @throws Error When the value property is missing from the field configuration
     */
    private void handleComponentField(String field, String value, PropertyIterator properties) throws Error {
        try {
            JsonObject fieldElement = getComponentDataByKey(String.format("fields/%s", field)).getAsJsonObject();
            JsonObject fieldConfig;
            SightlyWCMMode wcmMode = getWcmMode();

            boolean skipSlotAndAttribute = false;
            String debugValue = null;

            if (fieldElement.has("value") && fieldElement.get("value").isJsonObject()) {
                fieldConfig = fieldElement.get("value").getAsJsonObject();
            } else {
                throw new Error("Unable to handle field as the JSON object is either invalid or is missing the 'value' property");
            }

            boolean isSlot = false;
            String slotName = StringUtils.EMPTY;

            // Does the field have a custom configuration map for the attribute map?
            if (fieldElement.has("mapToConfig")) {
                fieldToConfigMap.put(fieldElement.get("mapToConfig").getAsString(), value);
            }

            if (fieldConfig != null && fieldConfig.has("field")) {
                String fieldType = fieldConfig.get("field").getAsString();

                // Autocompletion
                if (fieldType.equals("autocomplete")) {
                    value = TagUtil.getTagValueAsAdmin(value, getSlingScriptHelper());
                }

                // Checkboxes
                if (fieldType.equals("checkbox")) {
                    boolean isChecked = value.equals("true");

                    debugValue = isChecked ? "Yes" : "No";
                    skipSlotAndAttribute = true;

                    attrs.addBoolean(field, isChecked);
                }

                // Image/File upload
                if (fieldType.equals("fileUpload") && properties != null) {
                    while (properties.hasNext()) {
                        Property property = properties.nextProperty();

                        try {
                            if (property.getName().equals("fileReference")) {
                                value = property.getString();
                                break;
                            }
                        } catch (RepositoryException ex) {
                            LOGGER.error("Unable to handle property iterator step!, {}", property);
                            LOGGER.error(ex.getLocalizedMessage());
                        }
                    }
                }

                // Does the field need to run through Externalizer?
                if (fieldConfig.has("externalizer") && fieldConfig.get("externalizer").getAsBoolean()) {
                    if (slingSettingsService.getRunModes().contains(Externalizer.AUTHOR)) {
                        value = externalizer.authorLink(resourceResolver, value) + ".html?wcmmode=disabled";
                    } else {
                        value = externalizer.externalLink(resourceResolver, Externalizer.LOCAL, value);
                    }
                }

                // Is this field a slot?
                isSlot = fieldConfig.has("slot");
                slotName = isSlot ? fieldConfig.get("slot").getAsString() : slotName;
            }

            if (!skipSlotAndAttribute) {
                if (isSlot) {
                    slots.put(slotName, value);
                } else {
                    attrs.add(field, value);
                }
            }

            // Add the config to some additional output when in the correct WCM Mode
            if (wcmMode.isEdit() || wcmMode.isPreview()) {
                configOutput.put(
                    WordUtils.capitalize(StringUtils.join(field.split("-"), " ")),
                    debugValue != null ? debugValue : value
                );
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to parse field: {}", field);
            LOGGER.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Sets any analytics attributes that are required by the component.
     */
    private void setAnalyticsAttributes() {
        Map<String, String> analyticsAttrs = new HashMap<>();

        analyticsAttrs.put("analytics-label", DETAILS_ANALYTICS_LABEL);
        analyticsAttrs.put("analytics-location", DETAILS_ANALYTICS_LOCATION);

        for (Map.Entry<String, String> attr : analyticsAttrs.entrySet()) {
            String value = componentProperties.get(attr.getValue(), StringUtils.EMPTY);

            if (StringUtils.isNotEmpty(value)) {
                attrs.add(attr.getKey(), value);
            }
        }
    }

    /**
     * Binds the cloud configuration values to their respective attributes for use in the component.
     */
    private void setConfigurationAttributes() {
        if (config != null) {
            Map<String, String> configuration = getConfigurationsMap();

            for (JsonElement jsonElement : config) {
                String[] configMap = jsonElement.getAsString().split(":");

                String configKey = configMap[0];
                String customKey = configMap.length >= 2 ? configMap[1] : null;

                boolean hasCustomKeyMap = StringUtils.isNotEmpty(customKey);

                // Loop over any field-to-config mapped keys and process the correct configuration value using it
                // instead of what was supplied.
                for (Map.Entry<String, String> item : fieldToConfigMap.entrySet()) {
                    if (item.getKey().equals(configKey)) {
                        configKey = item.getValue();
                    }
                }

                // When the configuration key has a custom attribute map, use that over the default
                attrs.set(hasCustomKeyMap ? customKey : configKey, configuration.getOrDefault(configKey, StringUtils.EMPTY));
            }
        }
    }

    /**
     * Retrieves the Base64 encoded JSON string from JCR storage and parses it.
     *
     * @param pathPart Path part of our encoded JSON string
     * @return Parsed {@link JsonElement} instance
     */
    private JsonElement getComponentDataByKey(String pathPart) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = null;

        try {
            String componentPath = "/content/%s/%s/component-dialog/vue-widgets/%s/%s";  // NOSONAR
            String tenantName = TenantUtil.resolveTenantIdFromPath(getResource().getPath());
            String resourcePath = String.format(componentPath, NameConstants.PN_TAGS, tenantName, componentName, pathPart);

            Resource fieldResource = getResourceResolver().getResource(resourcePath);

            if (fieldResource == null) {
                throw new ResourceNotFoundException("Unable to get resource for: " + resourcePath);
            }

            String fieldValue = fieldResource.getValueMap().get("value", StringUtils.EMPTY);
            String json = new String(Base64.getDecoder().decode(fieldValue));

            jsonElement = parser.parse(json);
        } catch (Exception ex) {
            LOGGER.error("Unable to parse JSON value for '{}' on component: '{}'", pathPart, componentName);
            LOGGER.error(ex.getMessage());
        }

        return jsonElement;
    }

    /**
     * Builds the HTML structure needed for our front-end JavaScript code.
     */
    private void constructComponentHTML() {
        componentHTML.append(String.format("<%s %s>", componentName, attrs.build()));

        if (slots.size() > 0) {
            for (Map.Entry<String, String> slot : slots.entrySet()) {
                componentHTML.append(String.format("<template v-slot:%s>%s</template>", slot.getKey(), slot.getValue()));
            }
        }

        componentHTML.append(String.format("</%s>", componentName));
    }

    /**
     * Retrieves the {@link StringBuilder} structure and converts it into a usable string.
     *
     * @return {@link String} version of the component template.
     */
    public String getComponentHTML() {
        return componentHTML.toString();
    }
}
