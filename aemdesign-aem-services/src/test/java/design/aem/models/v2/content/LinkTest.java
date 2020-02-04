package design.aem.models.v2.content;

import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
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

import static design.aem.utils.components.ComponentsUtil.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentsUtil.class})
public class LinkTest {

    @InjectMocks
    Link link = new Link();

    @Mock
    SlingHttpServletRequest request;

    @Mock
    Resource resource;

    @Mock
    Bindings bindings;

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(ComponentsUtil.class);
        when(bindings.get("request")).thenReturn(request);
        when(bindings.get("resource")).thenReturn(resource);
    }

    @Test
    public void testReady() {
        ComponentProperties componentProperties = new ComponentProperties();
        when(ComponentsUtil.getComponentProperties(eq(link),
            any(Object[][].class),
            eq(DEFAULT_FIELDS_STYLE),
            eq(DEFAULT_FIELDS_ACCESSIBILITY),
            eq(DEFAULT_FIELDS_ANALYTICS),
            eq(DEFAULT_FIELDS_ATTRIBUTES))).thenReturn(componentProperties);
        when(resource.getPath()).thenReturn("PATH");
        link.ready();
        Assert.assertTrue(link.getComponentProperties().isEmpty());
    }
}
