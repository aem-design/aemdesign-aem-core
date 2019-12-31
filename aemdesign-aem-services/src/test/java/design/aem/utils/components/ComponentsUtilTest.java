package design.aem.utils.components;

import org.apache.commons.imaging.common.BinaryInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class ComponentsUtilTest {

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Resource resource;

    @Mock
    Node node;

    @Mock
    NodeType nodeType;

    @Mock
    Property property;

    @Mock
    Binary binary;

    @Mock
    InputStream stream;

    @Mock
    BinaryInputStream bin;

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(IOUtils.class);
    }

    @Test
    public void testClass() {
        ComponentsUtil test = new ComponentsUtil();
        assertNotNull(test);
    }

    @Test
    public void testGetResourceContent_Success_Asset() throws RepositoryException, IOException {
        byte[] result = new byte[]{'a', 'b'};
        String[] paths = {"/content/dam/aem-design/campaigns/asset-test.jpg"};
        when(resourceResolver.getResource("/content/dam/aem-design/campaigns/asset-test.jpg")).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getPrimaryNodeType()).thenReturn(nodeType);
        when(nodeType.getName()).thenReturn("dam:Asset");
        when(node.hasNode("jcr:content/renditions/original/jcr:content")).thenReturn(true);
        when(node.getNode("jcr:content/renditions/original/jcr:content")).thenReturn(node);
        when(node.hasProperty(JcrConstants.JCR_DATA)).thenReturn(true);
        when(node.getProperty("jcr:data")).thenReturn(property);
        when(property.getBinary()).thenReturn(binary);
        when(binary.getStream()).thenReturn(stream);
        when(IOUtils.toByteArray(any(BufferedInputStream.class))).thenReturn(result);
        String resourceContent = ComponentsUtil.getResourceContent(resourceResolver, paths, ".");
        Assert.assertEquals("ab.", resourceContent);
    }

    @Test
    public void testGetResourceContent_Success_Page() throws RepositoryException, IOException {
        String[] paths = {"/content/aem-design/home/products"};
        when(resourceResolver.getResource("/content/aem-design/home/products")).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.getPrimaryNodeType()).thenReturn(nodeType);
        when(nodeType.getName()).thenReturn("cq:page");
        when(node.hasNode("jcr:content")).thenReturn(true);
        when(node.getNode("jcr:content")).thenReturn(node);
        when(node.hasProperty(JcrConstants.JCR_DATA)).thenReturn(false);
        String resourceContent = ComponentsUtil.getResourceContent(resource);
        Assert.assertEquals(0, resourceContent.length());
    }

}
