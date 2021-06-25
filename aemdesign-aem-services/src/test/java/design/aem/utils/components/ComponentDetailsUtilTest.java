package design.aem.utils.components;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static design.aem.utils.components.CommonUtil.PN_REDIRECT_TARGET;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

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


    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }


    @Test
    public void testCheckSelected() {
        when(currentPage.getPath()).thenReturn("/content/aem-design/en/home");
        when(page.getPath()).thenReturn("/content/aem-design/en");
        boolean isSelected = ComponentDetailsUtil.checkSelected(page, currentPage, resolver);
        assertTrue(isSelected);
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
        assertTrue(isCurrentPageIsRedirectTarget);
    }

}

