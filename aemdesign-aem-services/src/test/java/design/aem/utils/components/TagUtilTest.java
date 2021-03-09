package design.aem.utils.components;


import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import design.aem.services.ContentAccess;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class TagUtilTest {

    private static final String tagPath = "/content/cq:tags/men";

    @Mock
    SlingScriptHelper sling;

    @Mock
    ContentAccess contentAccess;

    @Mock
    ResourceResolver adminResourceResolver;

    @Mock
    TagManager adminTagManager;

    @Mock
    Resource resource1, resource2;

    @Mock
    ValueMap tagVM1, tagVM2;

    @Mock
    Tag tag1, tag2;

    @Mock
    Page page;

    @Mock
    Iterable<Resource> resourceIterator;

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
    public void testGetTagValueAsAdmin_Success() {
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve(tagPath)).thenReturn(tag1);
        when(tag1.getName()).thenReturn("tag");
        when(tag1.adaptTo(Resource.class)).thenReturn(resource1);
        when(resource1.getValueMap()).thenReturn(tagVM1);
        when(tagVM1.containsKey("value")).thenReturn(true);
        when(tagVM1.get("value", "tag")).thenReturn("tagValue");
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertEquals("tagValue", actualTag);
    }

    @Test
    public void testGetTagValueAsAdmin_EmptyTagPath() {
        String actualTag = TagUtil.getTagValueAsAdmin("", sling);
        Assertions.assertTrue(actualTag.isEmpty());
    }

    @Test
    public void testGetTagValueAsAdmin_NullTagPath() {
        String actualTag = TagUtil.getTagValueAsAdmin(null, sling);
        Assertions.assertTrue(actualTag.isEmpty());
    }

    @Test
    public void testGetTagValueAsAdmin_NullContentAccess() {
        when(sling.getService(ContentAccess.class)).thenReturn(null);
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertTrue(actualTag.isEmpty());
    }

    @Test
    public void testGetTagValueAsAdmin_NullResourceResolver() {
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(null);
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertTrue(actualTag.isEmpty());
    }

    @Test
    public void testGetTagValueAsAdmin_NullTag() {
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve(tagPath)).thenReturn(null);
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertTrue(actualTag.isEmpty());
    }

    @Test
    public void testGetTagValueAsAdmin_NullResource() {
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve(tagPath)).thenReturn(tag1);
        when(tag1.getName()).thenReturn("tag");
        when(tag1.adaptTo(Resource.class)).thenReturn(null);
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertEquals("tag", actualTag);
    }

    @Test
    public void testGetTagValueAsAdmin_NullValueMap() {
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve(tagPath)).thenReturn(tag1);
        when(tag1.getName()).thenReturn("tag");
        when(tag1.adaptTo(Resource.class)).thenReturn(resource1);
        when(resource1.getValueMap()).thenReturn(null);
        String actualTag = TagUtil.getTagValueAsAdmin(tagPath, sling);
        Assertions.assertEquals("tag", actualTag);
    }

    @Test
    public void testGetTagsAsAdmin_Success_GetTagChildren() {
        String[] tagPaths = {"/content/cq:tags/gender"};
        String[] attributesToRead = {"title", "description"};
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve("/content/cq:tags/gender")).thenReturn(tag1);
        when(tag1.adaptTo(Resource.class)).thenReturn(resource1);
        when(resource1.hasChildren()).thenReturn(true);
        when(resource1.getChildren()).thenReturn(getResources());
        when(resource2.getPath()).thenReturn("/content/cq:tags/gender/male");
        when(adminTagManager.resolve("/content/cq:tags/gender/male")).thenReturn(tag2);
        when(tag2.adaptTo(Resource.class)).thenReturn(resource1);
        when(tag2.getPath()).thenReturn("/content/cq:tags/gender/male");
        when(tag2.getTitle()).thenReturn("Male");
        when(tag2.getDescription()).thenReturn("Male Clothing");
        when(tag2.getName()).thenReturn("Male");
        when(tag2.getLocalTagID()).thenReturn("localId");
        when(tag2.getTagID()).thenReturn("tagId");
        when(resource1.getValueMap()).thenReturn(tagVM1);
        when(tagVM1.containsKey("value")).thenReturn(true);
        when(tagVM1.get("value", "Male")).thenReturn("tagValue");
        when(tagVM1.get("title", null)).thenReturn("Male");
        when(tagVM1.get("description", null)).thenReturn("description");
        when(tagVM1.containsKey("title")).thenReturn(true);
        when(tagVM1.containsKey("description")).thenReturn(true);
        LinkedHashMap<String, Map> actualTagsMap = TagUtil.getTagsAsAdmin(sling, tagPaths, Locale.getDefault(), attributesToRead, true);
        Assertions.assertEquals("/content/cq:tags/gender/male", actualTagsMap.get("tagId").get("path"));
        Assertions.assertEquals("localId", actualTagsMap.get("tagId").get("tagid"));
        Assertions.assertEquals("description", actualTagsMap.get("tagId").get("description"));
        Assertions.assertEquals("Male", actualTagsMap.get("tagId").get("title"));
    }

    @Test
    public void testGetTagsAsAdmin_Success_NoTagChildren() {
        String[] tagPaths = {"/content/cq:tags/male"};
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve("/content/cq:tags/male")).thenReturn(tag1);
        when(tag1.getTitle()).thenReturn("Male");
        when(tag1.getDescription()).thenReturn("Male Clothing");
        when(tag1.getPath()).thenReturn("/content/cq:tags/gender/male");
        when(tag1.getName()).thenReturn("Male");
        when(tag1.getLocalTagID()).thenReturn("localId");
        when(tag1.getTagID()).thenReturn("tagId");
        when(tag1.adaptTo(Resource.class)).thenReturn(resource1);
        when(resource1.getValueMap()).thenReturn(tagVM1);
        when(tagVM1.containsKey("value")).thenReturn(true);
        when(tagVM1.get("value", "Male")).thenReturn("tagValue");
        LinkedHashMap<String, Map> actualTagsMap = TagUtil.getTagsAsAdmin(sling, tagPaths, Locale.getDefault());
        Assertions.assertEquals("/content/cq:tags/gender/male", actualTagsMap.get("tagId").get("path"));
        Assertions.assertEquals("localId", actualTagsMap.get("tagId").get("tagid"));
        Assertions.assertEquals("Male Clothing", actualTagsMap.get("tagId").get("description"));
        Assertions.assertEquals("Male", actualTagsMap.get("tagId").get("title"));
        Assertions.assertEquals("tagValue", actualTagsMap.get("tagId").get("value"));
    }

    @Test
    public void testGetTagsAsValuesAsAdmin_Success() {
        String[] tagPaths = {"/content/cq:tags/male", "/content/cq:tags/female"};
        when(sling.getService(ContentAccess.class)).thenReturn(contentAccess);
        when(contentAccess.getAdminResourceResolver()).thenReturn(adminResourceResolver);
        when(adminResourceResolver.adaptTo(TagManager.class)).thenReturn(adminTagManager);
        when(adminTagManager.resolve("/content/cq:tags/male")).thenReturn(tag1);
        when(adminTagManager.resolve("/content/cq:tags/female")).thenReturn(tag2);
        when(tag1.getName()).thenReturn("tag1");
        when(tag2.getName()).thenReturn("tag2");
        mockTag(tag1, "tag1", "tag1", resource1, tagVM1);
        mockTag(tag2, "tag2", "tag2", resource2, tagVM2);
        String actualValue = TagUtil.getTagsAsValuesAsAdmin(sling, ":", tagPaths);
        Assertions.assertEquals("tag1:tag2", actualValue);
    }

    @Test
    public void testGetTagsAsValuesAsAdmin_NullTagPath() {
        String actualValue = TagUtil.getTagsAsValuesAsAdmin(sling, ":", null);
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetTagsAsValues_Success() {
        String[] tagPaths = {"/content/cq:tags/male", "/content/cq:tags/female"};
        when(adminTagManager.resolve("/content/cq:tags/male")).thenReturn(tag1);
        when(adminTagManager.resolve("/content/cq:tags/female")).thenReturn(tag2);
        when(tag1.getName()).thenReturn("tag1");
        when(tag2.getName()).thenReturn("tag2");
        mockTag(tag1, "tag1", "tag1", resource1, tagVM1);
        mockTag(tag2, "tag2", "tag2", resource2, tagVM2);
        String actualValue = TagUtil.getTagsAsValues(adminTagManager, adminResourceResolver, ":", tagPaths);
        Assertions.assertEquals("tag1:tag2", actualValue);
    }

    @Test
    public void testGetTagsAsValues_NullTagPath() {
        String actualValue = TagUtil.getTagsAsValues(adminTagManager, adminResourceResolver, ":", null);
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetTagsValues_Success() {
        String[] tagPaths = {"/content/cq:tags/male", "/content/cq:tags/female"};
        when(adminTagManager.resolve("/content/cq:tags/male")).thenReturn(tag1);
        when(adminTagManager.resolve("/content/cq:tags/female")).thenReturn(tag2);
        when(tag1.getName()).thenReturn("tag1");
        when(tag2.getName()).thenReturn("tag2");
        mockTag(tag1, "tag1", "tag1", resource1, tagVM1);
        mockTag(tag2, "tag2", "tag2", resource2, tagVM2);
        String[] actualValue = TagUtil.getTagsValues(adminTagManager, adminResourceResolver, ":", tagPaths);
        Assertions.assertEquals("tag1", actualValue[0]);
        Assertions.assertEquals("tag2", actualValue[1]);
    }

    @Test
    public void testGetTagsValues_NullTagPath() {
        String[] actualValue = TagUtil.getTagsValues(adminTagManager, adminResourceResolver, ":", null);
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetTagsValues_NullResource() {
        String[] tagPaths = {"/content/cq:tags/male", "/content/cq:tags/female"};
        when(adminTagManager.resolve("/content/cq:tags/male")).thenReturn(tag1);
        when(adminTagManager.resolve("/content/cq:tags/female")).thenReturn(tag2);
        when(tag1.getName()).thenReturn("tag1");
        when(tag2.getName()).thenReturn("tag2");
        when(tag1.adaptTo(Resource.class)).thenReturn(null);
        when(tag2.adaptTo(Resource.class)).thenReturn(null);
        String[] actualValue = TagUtil.getTagsValues(adminTagManager, adminResourceResolver, ":", tagPaths);
        Assertions.assertEquals(0, actualValue.length);
    }

    @Test
    public void testGetPathFromTagId() {
        String path = TagUtil.getPathFromTagId("gender:male/size", "/content/cq:tags");
        Assertions.assertEquals("/content/cq:tags/gender/male/size", path);
    }

    @Test
    public void testGetPageTags_Success() {
        when(page.getTags()).thenReturn(new Tag[]{tag1});
        Tag[] tags = TagUtil.getPageTags(page);
        Assertions.assertEquals(tag1, tags[0]);
    }

    @Test
    public void testGetPageTags_NullPage() {
        Tag[] tags = TagUtil.getPageTags(null);
        Assertions.assertEquals(0, tags.length);
    }

    private Iterable<Resource> getResources() {
        Iterable<Resource> resources = Arrays.asList(resource2);
        return resources;
    }

    private void mockTag(Tag tag, final String value, final String name, final Resource resource, final ValueMap tagVM) {
        when(tag.adaptTo(Resource.class)).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(tagVM);
        when(tagVM.containsKey("value")).thenReturn(true);
        when(tagVM.get("value", name)).thenReturn(value);
    }

}
