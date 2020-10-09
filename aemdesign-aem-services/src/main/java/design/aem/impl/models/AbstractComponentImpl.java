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
package design.aem.impl.models;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import design.aem.components.AttrBuilder;
import design.aem.models.Component;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static design.aem.utils.components.ComponentsUtil.*;

public abstract class AbstractComponentImpl implements Component {
    @ScriptVariable
    protected ComponentContext componentContext;

    @ScriptVariable
    @JsonIgnore
    protected Design currentDesign;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Node currentNode;

    @ScriptVariable
    protected Page currentPage;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Nullable
    protected Style currentStyle;

    @Inject
    protected Externalizer externalizer;

    @ScriptVariable
    protected ValueMap pageProperties;

    @Self
    protected SlingHttpServletRequest request;

    @SlingObject
    protected Resource resource;

    @SlingObject(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected ResourceResolver resourceResolver;

    @Inject
    protected SlingScriptHelper slingScriptHelper;

    @Inject
    protected SlingSettingsService slingSettingsService;

    @ScriptVariable
    protected SightlyWCMMode wcmmode;

    @Inject
    protected XSSAPI xss;

    @JsonIgnore
    protected AttrBuilder attributes = null;

    @JsonIgnore
    protected List<String> requestSelectors;

    @ValueMapValue(name = FIELD_STYLE_COMPONENT_ID, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String componentId;

    protected ValueMap properties = new ValueMapDecorator(new HashMap<>());

    @PostConstruct
    protected void init() {
        attributes = new AttrBuilder(xss);
        requestSelectors = Arrays.asList(request.getRequestPathInfo().getSelectors());

        // Handle the component properties
        evaluatePropertyDefaults();

        properties.putAll(resource.getValueMap());

        // Set a few standard component attributes
        attributes.add(COMPONENT_ATTRIBUTE_ID, getComponentId());
    }

    /**
     * Iterate through the component property field defaults and assign default values.
     */
    private void evaluatePropertyDefaults() {
        getPropertyDefaultsMap().forEach((fieldName, callback) ->
            properties.put(fieldName, callback.apply(properties.get(fieldName))));
    }

    /**
     * Construct the component property defaults to ensure critical fields have values.
     *
     * @return {@link Map} instance with property defaults
     */
    protected Map<String, Function<Object, Object>> getPropertyDefaultsMap() {
        Map<String, Function<Object, Object>> propertyDefaults = new HashMap<>();

        propertyDefaults.put(FIELD_VARIANT, value -> StringUtils.defaultIfEmpty((String) value, DEFAULT_VARIANT));

        return propertyDefaults;
    }

    @Override
    public AttrBuilder getAttributes() {
        return attributes;
    }

    @JsonIgnore
    @Override
    public boolean isBadgeRequest() {
        return requestSelectors.contains("badge");
    }

    @JsonIgnore
    @Override
    public boolean isSlingModelRequest() {
        return requestSelectors.contains("model");
    }

    @JsonIgnore
    @Nullable
    @Override
    public String getBadgeTemplate() {
        if (isBadgeRequest() && requestSelectors.size() >= 2) {
            return requestSelectors.get(1);
        }

        return null;
    }

    @Override
    public @Nullable String getComponentName() {
        return componentContext.getComponent().getName().trim();
    }

    @Nullable
    @Override
    public String getComponentId() {
        if (StringUtils.isEmpty(componentId)) {
            componentId = ComponentsUtil.getComponentId(currentNode);
        }

        return componentId;
    }

    /**
     * Determine if the given {@code childNode} resource exists and has children.
     *
     * @param childNode name of the child node
     * @return 'true' with children and 'false' without
     */
    protected final boolean resourceChildHasChildren(String childNode) {
        Resource componentResource = resource.getChild(childNode);

        return componentResource != null && componentResource.hasChildren();
    }
}
