package design.aem.components;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.commons.WCMUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.xml.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Abstract Class for CQ5 Components.
 *
 * @author yawly
 * @date 19.05.2016
 */
abstract class AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponent.class);

    private Boolean editMode;
    private Boolean previewMode;
    private Boolean publishMode;
    private Boolean designMode;

    private SlingHttpServletRequest slingRequest;
    private ComponentContext componentContext;

    private ResourceResolver resourceResolver;
    private Resource resource;
    private ValueMap properties;
    private I18n i18n;

    private Page currentPage;
    private Page resourcePage;
    private ValueMap pageProperties;
    private PageManager pageManager;

    public void setSlingRequest(final SlingHttpServletRequest slingRequest) {
        this.slingRequest = slingRequest;
        init();
    }

    public void setPage(final Page page) {
        this.currentPage = page;
    }

    protected abstract void init();

    public void init(Resource resource) {
        this.resource = resource;
        this.properties = resource.adaptTo(ValueMap.class);
        this.init();
    }

    protected final SlingHttpServletRequest getSlingRequest() {
        return this.slingRequest;
    }

    @SuppressWarnings("unchecked")
    protected final ComponentContext getComponentContext() {
        if (this.componentContext == null) {
            this.componentContext = WCMUtils.getComponentContext(this.slingRequest);
        }
        return this.componentContext;
    }

    protected final PageManager getPageManager() {
        if (this.pageManager == null) {
            this.pageManager = getResourceResolver().adaptTo(PageManager.class);
        }
        return this.pageManager;
    }

    protected final I18n getI18n() {
        if (this.i18n == null) {
            Locale pageLocale = getCurrentPage().getLanguage(false);
            ResourceBundle resourceBundle = getSlingRequest().getResourceBundle(pageLocale);
            this.i18n = new I18n(resourceBundle);
        }
        return this.i18n;
    }

    public final Page getCurrentPage() {
        if (this.currentPage == null) {
            this.currentPage = getComponentContext() == null ? null : getComponentContext().getPage();
            if (this.currentPage == null) {
                this.currentPage = getResourcePage();
            }
            if (this.currentPage == null) {
                throw new IllegalArgumentException("Current Page is Null");
            }
        }
        return this.currentPage;
    }

    protected final Page getResourcePage() {
        if (this.resourcePage == null) {
            this.resourcePage = getPageManager().getContainingPage(getResource());
        }
        return this.resourcePage;
    }

    public final ResourceResolver getResourceResolver() {
        if (this.resourceResolver == null) {
            this.resourceResolver = getResource().getResourceResolver();
        }
        return this.resourceResolver;
    }

    protected Resource getResource() {
        if (this.resource == null) {
            this.resource = getSlingRequest().getResource();
        }
        return this.resource;
    }

    public final ValueMap getPageProperties() {
        if (this.pageProperties == null) {
            this.pageProperties = getCurrentPage() == null ? ValueMap.EMPTY : getCurrentPage().getProperties();
        }
        return this.pageProperties;
    }

    public final ValueMap getProperties() {
        if (this.properties == null) {
            this.properties = getResource().adaptTo(ValueMap.class);
            if (this.properties == null) {
                this.properties = ValueMap.EMPTY;
            }
        }
        return this.properties;
    }

    protected String getStringProperty(String name) {
        return getProperties().get(name, "");
    }

    protected Date getDateProperty(String name) {
        return getProperties().get(name, Date.class);
    }

    protected String getStringPropertyXMLEscaped(String name) {
        return XML.escape(getProperties().get(name, ""));
    }

    public final String getStringProperty(final String propertyName, final String defaultValue) {
        return getProperties().get(propertyName, defaultValue);
    }

    protected List<String> getStringArrayProperty(String name) {
        String[] properties = getProperties().get(name, String[].class);
        return (properties != null) ? Arrays.asList(properties) : new ArrayList<String>();
    }

    protected final Integer getIntegerProperty(final String propertyName) {
        return getProperties().get(propertyName, Integer.class);
    }

    protected final Boolean getBooleanProperty(final String propertyName) {
        Boolean propertyValue = getProperties().get(propertyName, Boolean.class);
        return (propertyValue != null) ? propertyValue : false;

    }

    @SuppressWarnings("unchecked")
    public final boolean isEditMode() {
        if (this.editMode == null) {
            this.editMode = WCMMode.fromRequest(getSlingRequest()) == WCMMode.EDIT;
        }
        return this.editMode;
    }

    @SuppressWarnings("unchecked")
    public final boolean isPreviewMode() {
        if (this.previewMode == null) {
            this.previewMode = WCMMode.fromRequest(getSlingRequest()) == WCMMode.PREVIEW;
        }
        return this.previewMode;
    }

    @SuppressWarnings("unchecked")
    public final boolean isPublishMode() {
        if (this.publishMode == null) {
            this.publishMode = WCMMode.fromRequest(getSlingRequest()) == WCMMode.DISABLED;
        }
        return this.publishMode;
    }

    @SuppressWarnings("unchecked")
    public final boolean isDesignMode() {
        if (this.designMode == null) {
            this.designMode = WCMMode.fromRequest(getSlingRequest()) == WCMMode.DESIGN;
        }
        return this.designMode;
    }

    public static String getTemplateName(final Page page) {
        ValueMap properties = page.getProperties();
        return properties.get("cq:template", String.class);
    }

    public String getExt(String linkPropertyName) {
        return getResourceResolver().getResource(getStringProperty(linkPropertyName)) != null ? ".html" : "";
    }
}

