package design.aem.utils.components;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ComponentDetailsUtilTest {

    @Mock
    Page page, currentPage;

    @Mock
    ResourceResolver resolver;

    @Mock
    Resource contentResource;

    @Mock
    ValueMap valueMap;

    @Mock
    PageManager pageManager;

    @Before
    public void before() {
        initMocks(this);
    }

    @Test
    public void testClass() {
        ComponentDetailsUtil test = new ComponentDetailsUtil();
        assertNotNull(test);
    }

    @Test
    public void testCheckSelected() {
        when(currentPage.getPath()).thenReturn("/content/aem-design/en/home");
        when(page.getPath()).thenReturn("/content/aem-design/en");
        boolean isSelected = ComponentDetailsUtil.checkSelected(page, currentPage, resolver);
        Assert.assertTrue(isSelected);
    }

    @Test
    public void testCurrentPageIsRedirectTarget() {
        final String REDIRECT_TARGET = "/content/aem.design/en/home/product";
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(PN_REDIRECT_TARGET, String.class)).thenReturn(REDIRECT_TARGET);
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(REDIRECT_TARGET)).thenReturn(currentPage);
        boolean isCurrentPageIsRedirectTarget = ComponentDetailsUtil.currentPageIsRedirectTarget(page, currentPage, resolver);
        Assert.assertTrue(isCurrentPageIsRedirectTarget);
    }

}

