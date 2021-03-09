package design.aem.utils.components;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import design.aem.context.CoreComponentTestContext;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class SlingPostUtilTest {

    @InjectMocks
    JcrUtil jcrUtil;

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
        Assertions.assertTrue(expectedTags.equals(tags));
    }

    @Test
    public void testGetProcessedTags_Success() throws Exception {
        when(request.getParameterValues("search")).thenReturn(new String[]{"tag1", "tag2:tag3", "tag3"});
        List<String> expectedTags = Arrays.asList(new String[]{"tag1", "tagId", "tag3"});
        when(tagManager.createTagByTitle("tag2:tag3", Locale.ENGLISH)).thenReturn(tag);
        when(tag.getTagID()).thenReturn("tagId");
        List<String> tags = SlingPostUtil.getProcessedTags(tagManager, "search", request);
        Assertions.assertTrue(expectedTags.equals(tags));
    }

    @Test
    public void testGetPropertyName_Success() {
        String actualValue = SlingPostUtil.getPropertyName("./name");
        Assertions.assertEquals("name", actualValue);
    }

    @Test
    public void testGetPropertyName_Success_ContainsSlash() {
        String actualValue = SlingPostUtil.getPropertyName("./image/name");
        Assertions.assertEquals("name", actualValue);
    }

    @Test
    public void testGetParentNode_Success_AccessNode() throws RepositoryException {
        when(node.hasNode("image")).thenReturn(true);
        when(node.getNode("image")).thenReturn(node);
        Node actualNode = SlingPostUtil.getParentNode(node, "./image/content");
        verify(node).getNode("image");
        Assertions.assertEquals(node, actualNode);
    }

    @Disabled
    @Test
    public void testGetParentNode_Success_CreateNode() throws RepositoryException {
        when(node.hasNode("image")).thenReturn(false);
        when(node.getSession()).thenReturn(session);
        when(node.getPath()).thenReturn("/content/aem.design/en");
        when(JcrUtil.createPath("/content/aem.design/en/image", "nt:unstructured", session)).thenReturn(node);
        Node actualNode = SlingPostUtil.getParentNode(node, "./image/content");
        Assertions.assertEquals(node, actualNode);
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
        Assertions.assertTrue(Arrays.equals(authors, multiplesValues.getValue()));
        ArgumentCaptor<String> singleValue = ArgumentCaptor.forClass(String.class);
        verify(node).setProperty(eq("writer"), singleValue.capture(), eq(1));
        Assertions.assertTrue(writer[0].equals(singleValue.getValue()));
    }

}
