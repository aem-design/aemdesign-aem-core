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
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Arrays;
import java.util.List;

import static design.aem.utils.components.ComponentsUtil.*;

public abstract class AbstractComponentImpl implements Component {
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
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

    protected ValueMap properties;

    @PostConstruct
    protected void init() {
        attributes = new AttrBuilder(xss);
        properties = resource.getValueMap();
        requestSelectors = Arrays.asList(request.getRequestPathInfo().getSelectors());

        // Set a few standard component attributes
        attributes.add(COMPONENT_ATTRIBUTE_ID, getComponentId());
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

    @Nullable
    @Override
    public String getComponentId() {
        if (StringUtils.isEmpty(componentId)) {
            componentId = ComponentsUtil.getComponentId(currentNode);
        }

        return componentId;
    }
}
