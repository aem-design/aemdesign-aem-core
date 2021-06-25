package design.aem.utils.components;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import org.apache.sling.testing.resourceresolver.MockResource;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ContentFragmentUtilTest {

    private static final String CONTENT_FRAGMENT_PATH = "/content/dam/aem-desigm/traditonals-shared";
    private static final String VARIATION = "VARIATION";

    @Mock
    MockResourceResolver resourceResolver;

    @Mock
    ContentFragment contentFragment;

    @Mock
    MockResource resource;

    @Mock
    ContentElement contentElement;

    @Mock
    ContentVariation variation;

    @Mock
    Iterator<ContentElement> contentElementIterator;

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
    public void testGetComponentFragmentMap_EmptyFragmentPath() {
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap("", VARIATION, resourceResolver);
        Assertions.assertTrue(fragmentMap.isEmpty());
    }

    @Test
    public void testGetComponentFragmentMap_NullFragmentPath() {
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(null, VARIATION, resourceResolver);
        Assertions.assertTrue(fragmentMap.isEmpty());
    }

    @Test
    public void testGetComponentFragmentMap_NullContentFragment() {
        when(resourceResolver.getResource(CONTENT_FRAGMENT_PATH)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("/apps/valid");
        when(resource.adaptTo(ContentFragment.class)).thenReturn(null);
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, VARIATION, resourceResolver);
        Assertions.assertTrue(fragmentMap.isEmpty());
    }

    @Test
    public void testGetComponentFragmentMap_NullVariation() {
        when(resourceResolver.getResource(CONTENT_FRAGMENT_PATH)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("/apps/valid");
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElements()).thenReturn(contentElementIterator);
        when(contentElementIterator.hasNext()).thenReturn(true, false);
        when(contentElementIterator.next()).thenReturn(contentElement);
        when(contentElement.getName()).thenReturn("name");
        when(contentElement.getVariation(VARIATION)).thenReturn(null);
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, VARIATION, resourceResolver);
        Assertions.assertNull(fragmentMap.get("name"));
    }

    @Test
    public void testGetComponentFragmentMap_Success_DefinedVariation() {
        when(resourceResolver.getResource(CONTENT_FRAGMENT_PATH)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("/apps/valid");
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElements()).thenReturn(contentElementIterator);
        when(contentElementIterator.hasNext()).thenReturn(true, false);
        when(contentElementIterator.next()).thenReturn(contentElement);
        when(contentElement.getName()).thenReturn("name");
        when(contentElement.getVariation(VARIATION)).thenReturn(variation);
        when(variation.getContent()).thenReturn("content");
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, VARIATION, resourceResolver);
        Assertions.assertEquals("content", fragmentMap.get("name"));
    }

    @Test
    public void testGetComponentFragmentMap_Success_DefaultVariation() {
        when(resourceResolver.getResource(CONTENT_FRAGMENT_PATH)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("/apps/valid");
        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElements()).thenReturn(contentElementIterator);
        when(contentElementIterator.hasNext()).thenReturn(true, false);
        when(contentElementIterator.next()).thenReturn(contentElement);
        when(contentElement.getName()).thenReturn("name");
        when(contentElement.getContent()).thenReturn("content");
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, null, resourceResolver);
        Assertions.assertEquals("content", fragmentMap.get("name"));
        verify(contentElement, never()).getVariation(anyString());
    }

    @Test
    public void testGetComponentFragmentMap_Exception() {
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, VARIATION, null);
        Assertions.assertTrue(fragmentMap.isEmpty());
    }

}
