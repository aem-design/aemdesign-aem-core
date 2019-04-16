package design.aem.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import design.aem.components.ComponentProperties;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = {GenericComponent.class, ComponentExporter.class})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class GenericModel implements GenericComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericModel.class);
    @ScriptVariable
    protected Resource resource;
    @ScriptVariable
    protected PageManager pageManager;
    @ScriptVariable
    protected Page currentPage;
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;
    @ScriptVariable
    protected Page resourcePage;
    @ScriptVariable
    protected ValueMap pageProperties;
    @ScriptVariable
    protected ValueMap properties;
    @ScriptVariable
    protected Designer designer;
    @ScriptVariable
    protected Design currentDesign;
    @ScriptVariable
    protected Component component;
    @ScriptVariable
    protected ValueMap inheritedPageProperties;
    @SlingObject
    protected ResourceResolver resourceResolver;
    @Self
    protected SlingHttpServletRequest slingHttpServletRequest;
    @SlingObject
    protected SlingHttpServletResponse slingHttpServletResponse;
    @SlingObject
    protected SlingScriptHelper slingScriptHelper;
    @ScriptVariable
    protected ComponentContext componentContext;
    @ScriptVariable
    protected Design resourceDesign;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected ComponentProperties componentProperties;

    @Override
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    public final PageManager getPageManager() { return this.pageManager; }
    public final Page getCurrentPage() { return this.currentPage; }
    public final Page getResourcePage() { return this.resourcePage; }
    public final ValueMap getPageProperties() { return this.pageProperties; }
    public final ValueMap getProperties() { return this.properties; }
    public final Designer getDesigner() { return this.designer; }
    public final Design getCurrentDesign() { return this.currentDesign; }
    public final Style getCurrentStyle() { return this.currentStyle; }
    public final Component getComponent() { return this.component; }
    public final ValueMap getInheritedPageProperties() { return this.inheritedPageProperties; }
    public final Resource getResource() { return this.resource; }
    public final ResourceResolver getResourceResolver() { return this.resourceResolver; }
    public final SlingHttpServletRequest getRequest() { return this.slingHttpServletRequest; }
    public final SlingHttpServletResponse getResponse() { return this.slingHttpServletResponse; }
    public final SlingScriptHelper getSlingScriptHelper() { return this.slingScriptHelper; }
    public final ComponentContext getComponentContext() { return this.componentContext; }
    public final EditContext getEditContext() { return this.componentContext.getEditContext(); }
    public final Design getResourceDesign() { return this.resourceDesign; }
    public final Node getCurrentNode() { return this.resource.adaptTo(Node.class); }
    public final I18n getI18n() { return new I18n(getRequest()); }

    public Map<String, Object> getPageContextMap() {

        Map<String, Object> pageContextMap = new HashMap<>();
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLINGREQUEST, getRequest());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCERESOLVER, getResourceResolver());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_SLING, getSlingScriptHelper());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_COMPONENTCONTEXT, getComponentContext());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCE, getResource());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTNODE, getCurrentNode());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_PROPERTIES, getProperties());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTSTYLE, getCurrentStyle());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_CURRENTPAGE, getCurrentPage());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEPAGE, getResourcePage());
        pageContextMap.put(PAGECONTEXTMAP_OBJECT_RESOURCEDESIGN, getResourceDesign());
        pageContextMap.put(PAGECONTEXTMAP_SOURCE, this);
        pageContextMap.put(PAGECONTEXTMAP_SOURCE_TYPE, PAGECONTEXTMAP_SOURCE_TYPE_SLINGMODEL);

        return pageContextMap;
    }

    @PostConstruct
    protected void initModel() {

    }
}
