package design.aem.models.v2.analytics;

import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.CommonUtil;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.script.Bindings;
import java.util.Locale;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentsUtil.class, CommonUtil.class})
public class DataLayerTest {

    @InjectMocks
    DataLayer dataLayer = new DataLayer();

    @Mock
    Page page;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    ValueMap properties;

    @Mock
    Bindings bindings;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Component component;

    @Mock
    SlingScriptHelper helper;

    @Mock
    Resource resource;

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(ComponentsUtil.class);
        PowerMockito.mockStatic(CommonUtil.class);
        when(bindings.get("resourcePage")).thenReturn(page);
        when(bindings.get("properties")).thenReturn(properties);
        when(bindings.get("request")).thenReturn(request);
        when(bindings.get("components")).thenReturn(component);
        when(bindings.get("sling")).thenReturn(helper);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    public void testActivate_Success() throws Exception {
        Locale locale = new Locale("en", "australia");
        ComponentProperties componentProperties = new ComponentProperties();
        when(page.getPath()).thenReturn("path");
        when(properties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, "")).thenReturn("");
        when(ComponentsUtil.getNewComponentProperties(dataLayer)).thenReturn(componentProperties);
        when(page.getLanguage(false)).thenReturn(locale);
        when(CommonUtil.findComponentInPage(page, DEFAULT_LIST_DETAILS_SUFFIX)).thenReturn("path");
        when(resourceResolver.getResource("path")).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("aemdesign/components/lists/list/v2");
        when(resource.adaptTo(ValueMap.class)).thenReturn(properties);
        when(properties.get(DETAILS_ANALYTICS_PAGENAME, "")).thenReturn("name");
        when(properties.get(DETAILS_ANALYTICS_PAGETYPE, "")).thenReturn("full-width");
        when(properties.get(DETAILS_ANALYTICS_PLATFORM, "aem")).thenReturn("mobile");
        when(properties.get(DETAILS_ANALYTICS_ABORT, "false")).thenReturn("true");
        when(properties.get(DETAILS_ANALYTICS_VARIANT, DEFAULT_VARIANT)).thenReturn("variant");
        when(ComponentsUtil.getComponentVariantTemplate(component, format(COMPONENT_VARIANT_TEMPLATE_FORMAT, "variant"), helper)).thenReturn("template");
        dataLayer.activate();
        Assert.assertEquals("template", dataLayer.getComponentProperties().get("variantTemplate"));
        Assert.assertEquals("full-width", dataLayer.getComponentProperties().get("pageType"));
        Assert.assertEquals("true", dataLayer.getComponentProperties().get("abort"));
        Assert.assertEquals("AUSTRALIA", dataLayer.getComponentProperties().get("contentCountry"));
        Assert.assertEquals("english", dataLayer.getComponentProperties().get("contentLanguage"));
        Assert.assertEquals("path", dataLayer.getComponentProperties().get("pagePath"));
        Assert.assertEquals("name", dataLayer.getComponentProperties().get("pageName"));
        Assert.assertEquals("", dataLayer.getComponentProperties().get("effectiveDate"));
        Assert.assertEquals("mobile", dataLayer.getComponentProperties().get("platform"));
    }
}
