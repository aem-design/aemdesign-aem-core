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
package design.aem.impl;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;
import com.drew.lang.annotations.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import design.aem.components.AttrBuilder;
import design.aem.models.v3.Component;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Arrays;
import java.util.List;

import static design.aem.utils.components.ComponentsUtil.FIELD_STYLE_COMPONENT_ID;

public abstract class AbstractComponentImpl implements Component {
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected ComponentContext componentContext;

    @ScriptVariable
    @JsonIgnore
    protected Design currentDesign;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Node currentNode;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
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

    private String componentId;

    @PostConstruct
    protected void init() {
        attributes = new AttrBuilder(xss);
        requestSelectors = Arrays.asList(request.getRequestPathInfo().getSelectors());

        // Set the component 'id' attribute
        attributes.add("id", getComponentId());
    }

    @Override
    public AttrBuilder getAttributes() {
        return attributes;
    }

    @JsonIgnore
    @Override
    public boolean isSlingModelRequest() {
        return !requestSelectors.isEmpty() && requestSelectors.contains("model");
    }

    @Nullable
    @Override
    public String getComponentId() {
        if (componentId == null) {
            ValueMap properties = resource.getValueMap();

            componentId = properties.get(FIELD_STYLE_COMPONENT_ID, String.class);

            if (StringUtils.isEmpty(componentId)) {
                componentId = ComponentsUtil.getComponentId(currentNode);
            }
        }

        return componentId;
    }
}
