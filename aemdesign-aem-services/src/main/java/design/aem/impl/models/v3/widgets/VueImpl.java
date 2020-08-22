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
package design.aem.impl.models.v3.widgets;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.NameConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import design.aem.components.AttrBuilder;
import design.aem.impl.models.ComponentImpl;
import design.aem.models.v3.widgets.Vue;
import design.aem.utils.components.TagUtil;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.getCloudConfigProperty;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAPS;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_CLOUDCONFIG_GOOGLEMAPS_API_KEY;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {Vue.class, ComponentExporter.class},
    resourceType = VueImpl.RESOURCE_TYPE
)
@Exporter(
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    options = {
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "false")
    }
)
public class VueImpl extends ComponentImpl implements Vue {
    private static final Logger LOGGER = LoggerFactory.getLogger(VueImpl.class);

    protected static final String RESOURCE_TYPE = "aemdesign/components/widgets/vue/v3/vue";

    @ValueMapValue(name = "vueComponentName", injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String componentName;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String analyticsLabel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String analyticsLocation;

    @ChildResource(name = "dynamic", injectionStrategy = InjectionStrategy.OPTIONAL)
    private Resource dynamicResource;

    private JsonArray componentConfiguration;
    private AttrBuilder vueAttributes = null;

    private final StringBuilder componentHTML = new StringBuilder();
    private final Map<String, String> configOutput = new HashMap<>();
    private final Map<String, String> slots = new HashMap<>();
    private final Map<String, String> fieldToConfigMap = new HashMap<>();

    @Override
    @PostConstruct
    protected void init() {
        super.init();

        attributes.add("vue-component", componentName);

        vueAttributes = new AttrBuilder(xss);

        if (!isConfigured()) {
            return;
        }

        // Retrieve and process the component config
        getComponentConfiguration();

        // Handle the dynamic configuration for the component
        handleComponentConfigurationAndSlots();

        // Add any analytics attributes to the Vue component
        setAnalyticsAttributes();

        // Configuration via attributes
        setConfigurationAttributes();

        // Construct the component HTML
        constructComponentHTML();
    }

    @JsonIgnore
    @Override
    public AttrBuilder getVueAttributes() {
        return vueAttributes;
    }

    @JsonIgnore
    @Override
    public String getComponentName() {
        return componentName;
    }

    @JsonProperty("html")
    @Override
    public String getComponentHTML() {
        return componentHTML.toString();
    }

    @JsonIgnore
    @Override
    public Map<String, String> getConfigOutput() {
        return configOutput;
    }

    /**
     * Determine if the the component is configured.
     *
     * @return {@code true} when configured and {@code false} when not
     */
    @Override
    public boolean isConfigured() {
        return StringUtils.isNotEmpty(componentName);
    }

    /**
     * Get an overview of how the component is configured.
     *
     * @return instance of a {@link Map}
     */
    @JsonProperty("component")
    public Map<String, Object> getComponentData() {
        Map<String, Object> componentData = new HashMap<>();

        componentData.put("configured", isConfigured());
        componentData.put("name", componentName);

        return componentData;
    }

    /**
     * Define the configurations map that enables Vue components to link with the authored values.
     *
     * @return {@code Map<String, String>}
     */
    @JsonIgnore
    protected Map<String, String> getConfigurationsMap() {
        Map<String, String> storedConfig = new HashMap<>();

        storedConfig.put(DEFAULT_CLOUDCONFIG_GOOGLEMAPS, getCloudConfiguration(
            DEFAULT_CLOUDCONFIG_GOOGLEMAPS,
            DEFAULT_CLOUDCONFIG_GOOGLEMAPS_API_KEY
        ));

        return storedConfig;
    }

    /**
     * Get the cloud configuration value for the given {@code configName} and {@code configProperty}.
     *
     * @param name     cloud configuration name
     * @param property JCR property name for the config
     * @return {@link String} representation of the config value
     */
    protected final String getCloudConfiguration(String name, String property) {
        return getCloudConfigProperty(
            (InheritanceValueMap) pageProperties,
            name,
            property,
            slingScriptHelper
        );
    }

    /**
     * Attempt to retrieve the Vue component configuration from pre-stored JSON structures that
     * have been defined within content tags.
     */
    private void getComponentConfiguration() {
        try {
            if (componentConfiguration == null) {
                componentConfiguration = getComponentDataByPath("config").getAsJsonArray(); // NOSONAR
            }
        } catch (Exception ex) {
            LOGGER.error("Unable to retrieve the component configuration. Error: {}", ex.getMessage());
        }
    }

    /**
     * Retrieve and parse over the authored configuration and hand off to {@link #handleComponentField}.
     */
    @SuppressWarnings("Duplicates")
    private void handleComponentConfigurationAndSlots() { // NOSONAR
        try {
            Node dynamicNode = dynamicResource.adaptTo(Node.class);

            if (dynamicNode != null) {
                Node componentNode = dynamicNode.getNode(componentName);

                if (componentNode != null) {
                    PropertyIterator properties = componentNode.getProperties();
                    NodeIterator nodes = componentNode.getNodes();

                    while (nodes.hasNext()) {
                        Node node = (Node) nodes.next();

                        if (node == null || node.getName().startsWith("jcr:")) {
                            continue;
                        }

                        handleComponentField(node.getName(), StringUtils.EMPTY, node.getProperties());
                    }

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

    /**
     * Binds the given {@code field} to either a slot or HTML attribute based on the fields configuration
     * set for the current component.
     *
     * @param field      name of the component field
     * @param value      value of the component field
     * @param properties any properties that require additional parsing
     * @throws Error when the value property is missing from the field configuration
     */
    @SuppressWarnings("Duplicates")
    private void handleComponentField(String field, String value, PropertyIterator properties) // NOSONAR
        throws Error {
        try {
            JsonObject fieldElement = getComponentDataByPath("fields/" + field).getAsJsonObject(); // NOSONAR
            JsonObject fieldConfig;

            boolean skipSlotAndAttribute = false;
            String debugValue = null;

            if (fieldElement.has("value") && fieldElement.get("value").isJsonObject()) { // NOSONAR
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
                    value = TagUtil.getTagValueAsAdmin(value, slingScriptHelper);
                }

                // Checkboxes
                if (fieldType.equals("checkbox")) {
                    boolean isChecked = value.equals("true");

                    debugValue = isChecked ? "Yes" : "No";
                    skipSlotAndAttribute = true;

                    vueAttributes.addBoolean(field, isChecked);
                }

                // Image/File upload
                if (fieldType.equals("fileUpload") && properties != null) {
                    while (properties.hasNext()) {
                        Property property = properties.nextProperty();

                        try { // NOSONAR
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

                // Does the field need to run through externalizer?
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
                    vueAttributes.add(field, value);
                }
            }

            // Add the config to some additional output when in the correct WCM Mode
            if (wcmmode.isEdit() || wcmmode.isPreview()) {
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
     * Retrieve the Base64 encoded string from the JCR and parse the JSON string within it.
     *
     * @param path path of encoded JSON string
     * @return parsed {@link JsonElement} instance
     */
    private JsonElement getComponentDataByPath(String path) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = null;

        try {
            String componentPath = "/content/%s/%s/component-dialog/vue-widgets/%s/%s"; // NOSONAR
            String tenantName = TenantUtil.resolveTenantIdFromPath(resource.getPath());
            String resourcePath = String.format(componentPath, NameConstants.PN_TAGS, tenantName, componentName, path);

            Resource fieldResource = resourceResolver.getResource(resourcePath);

            if (fieldResource == null) {
                throw new ResourceNotFoundException("Unable to get resource for: " + resourcePath);
            }

            String fieldValue = fieldResource.getValueMap().get("value", StringUtils.EMPTY);
            String json = new String(Base64.getDecoder().decode(fieldValue));

            jsonElement = parser.parse(json);
        } catch (Exception ex) {
            LOGGER.error("Unable to parse JSON value for '{}' on component: '{}'. Error: {}",
                path,
                componentName,
                ex.getMessage());
        }

        return jsonElement;
    }

    /**
     * Sets any analytics attributes that are required by the component.
     */
    @SuppressWarnings("Duplicates")
    private void setAnalyticsAttributes() {
        Map<String, String> analyticsAttrs = new HashMap<>();

        analyticsAttrs.put("analytics-label", analyticsLabel);
        analyticsAttrs.put("analytics-location", analyticsLocation);

        for (Map.Entry<String, String> attr : analyticsAttrs.entrySet()) {
            String value = StringUtils.defaultIfEmpty(attr.getValue(), StringUtils.EMPTY);

            if (StringUtils.isNotEmpty(value)) {
                vueAttributes.add(attr.getKey(), value);
            }
        }
    }

    /**
     * Binds the cloud configuration values to their respective attributes for use in the component.
     */
    @SuppressWarnings("Duplicates")
    private void setConfigurationAttributes() { // NOSONAR
        if (componentConfiguration != null) {
            Map<String, String> configuration = getConfigurationsMap();

            for (JsonElement jsonElement : componentConfiguration) {
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
                vueAttributes.set(hasCustomKeyMap ? customKey : configKey,
                    configuration.getOrDefault(configKey, StringUtils.EMPTY));
            }
        }
    }

    /**
     * Builds the HTML structure needed for our front-end JavaScript code.
     */
    @SuppressWarnings("Duplicates")
    private void constructComponentHTML() {
        componentHTML.append(String.format("<%s %s>", componentName, vueAttributes.build()));

        if (slots.size() > 0) {
            for (Map.Entry<String, String> slot : slots.entrySet()) {
                componentHTML.append(String.format("<template v-slot:%s>%s</template>", slot.getKey(), slot.getValue()));
            }
        }

        componentHTML.append(String.format("</%s>", componentName));
    }
}
