package design.aem.utils.components;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import org.apache.sling.testing.resourceresolver.MockResource;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void before() {
        initMocks(this);
    }

    @Test
    public void testClass() {
        ContentFragmentUtil test = new ContentFragmentUtil();
        assertNotNull(test);
    }

    @Test
    public void testGetComponentFragmentMap_EmptyFragmentPath() {
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap("", VARIATION, resourceResolver);
        Assert.assertTrue(fragmentMap.isEmpty());
    }

    @Test
    public void testGetComponentFragmentMap_NullFragmentPath() {
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(null, VARIATION, resourceResolver);
        Assert.assertTrue(fragmentMap.isEmpty());
    }

    @Test
    public void testGetComponentFragmentMap_NullContentFragment() {
        when(resourceResolver.getResource(CONTENT_FRAGMENT_PATH)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn("/apps/valid");
        when(resource.adaptTo(ContentFragment.class)).thenReturn(null);
        Map<String, Object> fragmentMap = ContentFragmentUtil.getComponentFragmentMap(CONTENT_FRAGMENT_PATH, VARIATION, resourceResolver);
        Assert.assertTrue(fragmentMap.isEmpty());
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
        Assert.assertNull(fragmentMap.get("name"));
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
        Assert.assertEquals("content", fragmentMap.get("name"));
    }

}
