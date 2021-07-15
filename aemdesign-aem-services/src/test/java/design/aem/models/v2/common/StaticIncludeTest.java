package design.aem.models.v2.common;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.wcm.api.components.Component;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
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

import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_ACCESSIBILITY;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_FIELDS_STYLE;
import static design.aem.utils.components.ConstantsUtil.SITE_INCLUDE_PATHS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentsUtil.class})
public class StaticIncludeTest {

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private Bindings bindings;

    @Mock
    Component component;

    @Mock
    ValueMap valueMap;

    @Mock
    SightlyWCMMode wcmMode;

    @Mock
    ResourceResolver resourceResolver;

    @InjectMocks
    StaticInclude staticInclude = new StaticInclude();

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(ComponentsUtil.class);
        when(bindings.get("request")).thenReturn(request);
        when(bindings.get("component")).thenReturn(component);
        when(bindings.get("properties")).thenReturn(valueMap);
        when(bindings.get("wcmmode")).thenReturn(wcmMode);
        when(component.getProperties()).thenReturn(valueMap);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    public void testReady_Success() {
        final String[] paths = new String[]{"/path1"};
        ComponentProperties componentProperties = new ComponentProperties();
        when(ComponentsUtil.getComponentProperties(eq(staticInclude),
            any(Object[][].class),
            eq(DEFAULT_FIELDS_STYLE),
            eq(DEFAULT_FIELDS_ACCESSIBILITY))).thenReturn(componentProperties);
        when(valueMap.get(JcrConstants.JCR_TITLE, "")).thenReturn("title");
        when(valueMap.get(SITE_INCLUDE_PATHS, new String[0])).thenReturn(paths);
        when(valueMap.get("showContentPreview", "false")).thenReturn("true");
        when(valueMap.get("showContent", "false")).thenReturn("true");
        when(ComponentsUtil.getResourceContent(resourceResolver, paths, "")).thenReturn("resource-content");
        when(wcmMode.isEdit()).thenReturn(true);
        staticInclude.ready();
        Assert.assertTrue((Boolean) staticInclude.getComponentProperties().get("hasContent"));
        Assert.assertTrue((Boolean) staticInclude.getComponentProperties().get("showContentPreview"));
        Assert.assertTrue((Boolean) staticInclude.getComponentProperties().get("showContentSet"));
        Assert.assertTrue((Boolean) staticInclude.getComponentProperties().get("showContent"));
        Assert.assertEquals("resource-content", staticInclude.getComponentProperties().get("includeContents"));
        Assert.assertEquals("parentnotfound", staticInclude.getComponentProperties().get("parentnotfound"));
        Assert.assertEquals("title", staticInclude.getComponentProperties().get("componentName"));
        Assert.assertEquals("/path1", staticInclude.getComponentProperties().get("includePaths"));
    }
}
