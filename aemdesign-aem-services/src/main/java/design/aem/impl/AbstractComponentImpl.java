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
import java.util.Arrays;
import java.util.List;

public abstract class AbstractComponentImpl implements Component {
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected ComponentContext componentContext;

    @ScriptVariable
    @JsonIgnore
    protected Design currentDesign;

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

    @PostConstruct
    protected void init() {
        attributes = new AttrBuilder(xss);
        requestSelectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
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
}
