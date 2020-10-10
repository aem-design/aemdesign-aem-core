package design.aem.utils.components;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JcrUtil.class})
public class SlingPostUtilTest {

    @Mock
    HttpServletRequest request;

    @Mock
    Node node;

    @Mock
    TagManager tagManager;

    @Mock
    Tag tag;

    @Mock
    Property property;

    @Mock
    Session session;

    @Before
    public void before() {
        try {
            initMocks(this);
            PowerMockito.mockStatic(JcrUtil.class);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Test
    public void testProcessDeletes_Success_HasProperty() throws Exception {
        when(request.getParameterNames()).thenReturn(Collections.enumeration(
            Arrays.asList(new String[]{"./name@Delete", "title"})));
        when(node.hasProperty("./name@Delete")).thenReturn(true);
        when(node.getProperty("./name@Delete")).thenReturn(property);
        SlingPostUtil.processDeletes(node, request);
        verify(property).remove();
    }

    @Test
    public void testProcessDeletes_Success_HasNode() throws Exception {
        when(request.getParameterNames()).thenReturn(Collections.enumeration(
            Arrays.asList(new String[]{"./name@Delete", "title"})));
        when(node.hasProperty("./name@Delete")).thenReturn(false);
        when(node.hasNode("./name@Delete")).thenReturn(true);
        when(node.getNode("./name@Delete")).thenReturn(node);
        SlingPostUtil.processDeletes(node, request);
        verify(node).remove();
    }

    @Test
    public void testGetTagRequestParameters() {
        when(request.getParameterNames()).thenReturn(Collections.enumeration(
            Arrays.asList(new String[]{"tag1@cq:tags", "test1", "tag2@cq:tags", "test2"})));
        List<String> tags = SlingPostUtil.getTagRequestParameters(request);
        List<String> expectedTags = Arrays.asList(new String[]{"tag1@cq:tags", "tag2@cq:tags"});
        Assert.assertTrue(expectedTags.equals(tags));
    }

    @Test
    public void testGetProcessedTags_Success() throws Exception {
        when(request.getParameterValues("search")).thenReturn(new String[]{"tag1", "tag2:tag3", "tag3"});
        List<String> expectedTags = Arrays.asList(new String[]{"tag1", "tagId", "tag3"});
        when(tagManager.createTagByTitle("tag2:tag3", Locale.ENGLISH)).thenReturn(tag);
        when(tag.getTagID()).thenReturn("tagId");
        List<String> tags = SlingPostUtil.getProcessedTags(tagManager, "search", request);
        Assert.assertTrue(expectedTags.equals(tags));
    }

    @Test
    public void testGetPropertyName_Success() {
        String actualValue = SlingPostUtil.getPropertyName("./name");
        Assert.assertEquals("name", actualValue);
    }

    @Test
    public void testGetPropertyName_Success_ContainsSlash() {
        String actualValue = SlingPostUtil.getPropertyName("./image/name");
        Assert.assertEquals("name", actualValue);
    }

    @Test
    public void testGetParentNode_Success_AccessNode() throws RepositoryException {
        when(node.hasNode("image")).thenReturn(true);
        when(node.getNode("image")).thenReturn(node);
        Node actualNode = SlingPostUtil.getParentNode(node, "./image/content");
        verify(node).getNode("image");
        Assert.assertEquals(node, actualNode);
    }

    @Test
    public void testGetParentNode_Success_CreateNode() throws RepositoryException {
        when(node.hasNode("image")).thenReturn(false);
        when(node.getSession()).thenReturn(session);
        when(node.getPath()).thenReturn("/content/aem.design/en");
        when(JcrUtil.createPath("/content/aem.design/en/image", "nt:unstructured", session)).thenReturn(node);
        Node actualNode = SlingPostUtil.getParentNode(node, "./image/content");
        Assert.assertEquals(node, actualNode);
    }

    @Test
    public void testWriteContent() throws RepositoryException {
        final String authors[] = new String[]{"test1", "test2", "test3"};
        final String writer[] = new String[]{"test4"};
        when(request.getParameterNames()).thenReturn(Collections.enumeration(
            Arrays.asList(new String[]{"./authors", "./writer"})));
        when(request.getParameterValues("./authors")).thenReturn(authors);
        when(request.getParameterValues("./writer")).thenReturn(writer);

        SlingPostUtil.writeContent(node, request);

        ArgumentCaptor<String[]> multiplesValues = ArgumentCaptor.forClass(String[].class);
        verify(node).setProperty(eq("authors"), multiplesValues.capture(), eq(1));
        Assert.assertTrue(Arrays.equals(authors, multiplesValues.getValue()));
        ArgumentCaptor<String> singleValue = ArgumentCaptor.forClass(String.class);
        verify(node).setProperty(eq("writer"), singleValue.capture(), eq(1));
        Assert.assertTrue(writer[0].equals(singleValue.getValue()));
    }

}
