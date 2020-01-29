package design.aem.models.v2.common;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
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

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_ACCESSIBILITY;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_STYLE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentsUtil.class})
public class RedirectNotificationTest {

    @Mock
    I18n i18n;

    @Mock
    private Bindings bindings;

    @Mock
    private ValueMap properties;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    PageManager pageManager;

    @Mock
    Page page;

    @InjectMocks
    RedirectNotification redirectNotification = new RedirectNotification();

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(ComponentsUtil.class);
        when(bindings.get("request")).thenReturn(request);
        when(bindings.get("pageProperties")).thenReturn(properties);
        when(bindings.get("pageManager")).thenReturn(pageManager);
    }

    @Test
    public void testReady_Success_ExternalUrl() {
        when(properties.get(PN_REDIRECT_TARGET, "")).thenReturn("https://www.linkedin.com/");
        ComponentProperties componentProperties = new ComponentProperties();
        componentProperties.put("redirectUrl", "https://www.linkedin.com/");
        componentProperties.put("redirectTitle", "redirectTitle");
        when(ComponentsUtil.getComponentProperties(eq(redirectNotification),
            any(Object[][].class),
            eq(DEFAULT_FIELDS_STYLE),
            eq(DEFAULT_FIELDS_ACCESSIBILITY))).thenReturn(componentProperties);
        redirectNotification.ready();
        Assert.assertEquals("redirectTitle", redirectNotification.getComponentProperties().get("redirectTitle"));
        Assert.assertEquals("https://www.linkedin.com/", redirectNotification.getComponentProperties().get("redirectUrl"));
        Assert.assertEquals("redirectIsSet", redirectNotification.getComponentProperties().get("redirectIsSet"));
        Assert.assertEquals("redirectIsNotSet", redirectNotification.getComponentProperties().get("redirectIsNotSet"));
    }

    @Test
    public void testReady_Success_InternalUrl() {
        when(properties.get(PN_REDIRECT_TARGET, "")).thenReturn("/content/aem.design/home/about-us");
        when(pageManager.getPage("/content/aem.design/home/about")).thenReturn(page);
        ComponentProperties componentProperties = new ComponentProperties();
        componentProperties.put("redirectUrl", "/content/aem.design/home/about");
        componentProperties.put("redirectTitle", "redirectTitle");
        when(ComponentsUtil.getComponentProperties(eq(redirectNotification),
            any(Object[][].class),
            eq(DEFAULT_FIELDS_STYLE),
            eq(DEFAULT_FIELDS_ACCESSIBILITY))).thenReturn(componentProperties);
        redirectNotification.ready();
        Assert.assertEquals("redirectTitle", redirectNotification.getComponentProperties().get("redirectTitle"));
        Assert.assertEquals("/content/aem.design/home/about.html", redirectNotification.getComponentProperties().get("redirectUrl"));
        Assert.assertEquals("redirectIsSet", redirectNotification.getComponentProperties().get("redirectIsSet"));
        Assert.assertEquals("redirectIsNotSet", redirectNotification.getComponentProperties().get("redirectIsNotSet"));
    }

}
