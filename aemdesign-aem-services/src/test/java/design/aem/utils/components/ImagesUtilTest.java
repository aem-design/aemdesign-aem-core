package design.aem.utils.components;

import com.adobe.xmp.XMPException;
import com.day.cq.commons.DiffInfo;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.foundation.Image;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.xss.XSSAPI;
import org.joda.time.DateTime;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getLastModified;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_IMAGE_GENERATED_FORMAT;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_IMAGE_RESOURCETYPE;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ImagesUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesUtilTest.class);

    @Mock
    Node node;

    @Mock
    com.adobe.granite.asset.api.Asset graniteAsset;

    @Mock
    com.day.cq.dam.api.Asset damAsset;

    @Mock
    ResourceResolver resolver;

    @Mock
    Resource resource;

    @Mock
    Rendition rendition;

    @Mock
    com.adobe.granite.asset.api.Rendition graniteRendition;

    @Mock
    ValueMap valueMap;

    @Mock
    Property property;

    @Mock
    Page page;

    @Mock
    Image image;

    @Mock
    MessageFormat messageFormat;

    @Mock
    ImagesUtil imagesUtil;

    @Mock
    DamUtil damUtil;

    private static final String PATH = "/content/aem.design/en/home";


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
    public void testClass() {
        // Run the test
        ImagesUtil test = new ImagesUtil();

        // Verify the results
        Assertions.assertNotNull(test);
    }

    @Test
    public void canRenderOnWeb() {

        Assertions.assertTrue(ImagesUtil.canRenderOnWeb("jpeg"));
        Assertions.assertTrue(ImagesUtil.canRenderOnWeb("jpg"));
        Assertions.assertTrue(ImagesUtil.canRenderOnWeb("gif"));
        Assertions.assertTrue(ImagesUtil.canRenderOnWeb("png"));

        Assertions.assertFalse(ImagesUtil.canRenderOnWeb("bmp"));
    }

    @Test
    public void testGetMetadataStringForKey_Success() throws RepositoryException {
        when(node.hasNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(true);
        when(node.getNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(node);
        when(property.isMultiple()).thenReturn(false);
        when(node.getProperty("key")).thenReturn(property);

        when(damUtil.getValue(node, "key", "defaultValue")).thenReturn("value");

        String actualValue = ImagesUtil.getMetadataStringForKey(node, "key", "defaultValue");
        Assertions.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_NullNode() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(null, "key", "defaultValue");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(node, "", "defaultValue");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromGraniteAsset_Success() throws RepositoryException {
        when(graniteAsset.adaptTo(Node.class)).thenReturn(node);
        when(graniteAsset.getResourceResolver()).thenReturn(resolver);
        when(node.getPath()).thenReturn(PATH);
        when(resolver.getResource(PATH.concat("/").concat(ImagesUtil.ASSET_METADATA_FOLDER))).thenReturn(resource);
        when(resource.adaptTo(ValueMap.class)).thenReturn(valueMap);
        when(valueMap.containsKey("key")).thenReturn(true);
        when(valueMap.get("key")).thenReturn("value");
        String actualValue = ImagesUtil.getMetadataStringForKey(graniteAsset, "key");
        Assertions.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromGraniteAsset_NullAsset() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey((com.adobe.granite.asset.api.Asset) null, "key");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromGraniteAsset_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(graniteAsset, "");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_Success() throws RepositoryException {
        when(damAsset.getMetadata("key")).thenReturn("value");
        String actualValue = imagesUtil.getMetadataStringForKey(damAsset, "key");
        Assertions.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_NullAsset() throws RepositoryException {
        String actualValue = imagesUtil.getMetadataStringForKey((com.day.cq.dam.api.Asset) null, "key");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(damAsset, "");
        Assertions.assertNull(actualValue);
    }

    @Test
    public void testGetThumbnail_Success() {

        int width = 300;

        Rendition customRendition = mock(Rendition.class, "cq5dam.thumbnail." + width);
        when(customRendition.getName()).thenReturn("cq5dam.thumbnail." + width);

        List<Rendition> renditions = new ArrayList<>();
        renditions.add(customRendition);

        com.day.cq.dam.api.Asset asset = mock(com.day.cq.dam.api.Asset.class);
        when(asset.getRenditions()).thenReturn(renditions);

        LOGGER.error("rendition.getName(): {}", customRendition.getName());
        LOGGER.error("renditionList: {}", renditions);

//        when(damAsset.getRenditions()).thenReturn(renditionList);
//        LOGGER.error("damAsset.getRenditions(): {}", damAsset.getRenditions());
//
//        when(damUtil.getBestFitRendition(width, renditionList)).thenReturn(rendition);
        Rendition actualRendition = ImagesUtil.getThumbnail(asset, 300);
        Assertions.assertEquals(customRendition, actualRendition);
    }

    @Test
    public void testGetThumbnailUrl_Success() {
        when(page.getPath()).thenReturn(PATH);
        when(resolver.map(PATH.concat(ImagesUtil.DEFAULT_THUMB_SELECTOR_MD))).thenReturn("/content/aem.design/en/home/jcr:content/image/file.sftmp");
        String actualUrl = ImagesUtil.getThumbnailUrl(page, resolver);
        Assertions.assertEquals("/content/aem.design/en/home/jcr:content/image/file.sftmp", actualUrl);
    }

    @Test
    public void testGetWidth_Success() throws RepositoryException {
        when(node.hasNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(true);
        when(node.getNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(node);
        Property a1 = mock(Property.class);
        when(a1.isMultiple()).thenReturn(false);
        a1.setValue(300);
        Property a2 = mock(Property.class);
        when(a2.isMultiple()).thenReturn(false);
        a2.setValue(320);
        when(node.getProperty("exif:PixelXDimension")).thenReturn(a1);
        when(node.getProperty("tiff:ImageWidth")).thenReturn(a2);
        when(DamUtil.getValue(node, "exif:PixelXDimension", "")).thenReturn("300");
        when(DamUtil.getValue(node, "tiff:ImageWidth", "300")).thenReturn("320");
        int actualWidth = ImagesUtil.getWidth(node);
        Assertions.assertEquals(320, actualWidth);
    }

    @Test
    public void testGetProcessedImage_Success() throws Exception {
        Map<String, Object> resourceProps = new HashMap();
        resourceProps.put("","");
        resourceProps.put("jcr:lastModified",DateTime.now().toDate().getTime());
        resourceProps.put("jcr:created",DateTime.now().toDate().getTime());
        ValueMap resourceVM = new ValueMapDecorator(resourceProps);
        Resource customResource = mock(Resource.class);
        Design designMock = mock(Design.class);
        Node resourceNode = mock(Node.class);
//        when(resourceNode.hasProperty(anyString())).thenReturn(true);

        Property timestamp = mock(Property.class);
        when(resourceNode.getProperty("jcr:lastModified")).thenReturn(timestamp);

        when(customResource.getValueMap()).thenReturn(resourceVM);
        when(customResource.adaptTo(DiffInfo.class)).thenReturn(null);

        XSSAPI xxsmock = mock(XSSAPI.class);

        when(resolver.adaptTo(Design.class)).thenReturn(designMock);
        when(resolver.adaptTo(XSSAPI.class)).thenReturn(xxsmock);

        when(customResource.getResourceResolver()).thenReturn(resolver);

        when(customResource.adaptTo(Node.class)).thenReturn(resourceNode);
        when(customResource.getPath()).thenReturn("/data/image");


        Image actualImage = ImagesUtil.getProcessedImage(customResource, "relativepath");

        Assertions.assertEquals("/data/image/relativepath", actualImage.getPath());

    }

    @Test
    public void testGetProcessedImage_Success_NullPath() throws Exception {
        Map<String, Object> resourceProps = new HashMap();
        resourceProps.put("","");
        resourceProps.put("jcr:lastModified",DateTime.now().toDate().getTime());
        resourceProps.put("jcr:created",DateTime.now().toDate().getTime());
        ValueMap resourceVM = new ValueMapDecorator(resourceProps);
        Resource customResource = mock(Resource.class);
        Design designMock = mock(Design.class);
        Node resourceNode = mock(Node.class);
//        when(resourceNode.hasProperty(anyString())).thenReturn(true);

        Property timestamp = mock(Property.class);
        when(resourceNode.getProperty("jcr:lastModified")).thenReturn(timestamp);

        when(customResource.getValueMap()).thenReturn(resourceVM);
        when(customResource.adaptTo(DiffInfo.class)).thenReturn(null);

        XSSAPI xxsmock = mock(XSSAPI.class);

        when(resolver.adaptTo(Design.class)).thenReturn(designMock);
        when(resolver.adaptTo(XSSAPI.class)).thenReturn(xxsmock);

        when(customResource.getResourceResolver()).thenReturn(resolver);

        when(customResource.adaptTo(Node.class)).thenReturn(resourceNode);
        when(customResource.getPath()).thenReturn("/test");

        Image testImage = new Image(customResource);

        Image actualImage = ImagesUtil.getProcessedImage(customResource, null);

        Assertions.assertEquals(testImage.getPath(), actualImage.getPath());
    }

    @Test
    public void testGetResourceImageCustomHref_Success() {
        Resource imageResource = mock(Resource.class);
//        ResourceResolver resolver = mock(ResourceResolver.class);

        when(resource.getChild("file")).thenReturn(imageResource);
        when(resource.getResourceResolver()).thenReturn(resolver);
        when(resource.getPath()).thenReturn(PATH);
        when(resource.getResourceResolver()).thenReturn(resolver);
        when(resolver.map(anyString())).thenReturn("href");

        when(imageResource.getResourceType()).thenReturn(DEFAULT_IMAGE_RESOURCETYPE);
        Map<String, Object> resourceProps = new HashMap();
        resourceProps.put("jcr:lastModified",1346524199000L);
        resourceProps.put("jcr:created",1346524199000L);
        ValueMap resourceVM = new ValueMapDecorator(resourceProps);

        when(imageResource.adaptTo(ValueMap.class)).thenReturn(resourceVM);
        when(imageResource.getPath()).thenReturn(PATH);

        Resource fileReference = mock(Resource.class);
        when(imageResource.getChild(IMAGE_FILEREFERENCE)).thenReturn(fileReference);

        String actualHref = ImagesUtil.getResourceImageCustomHref(resource, "file");

        Assertions.assertEquals("href", actualHref);
    }

    @Test
    public void testGetResourceImageCustomHref_NullResource() {
        String actualHref = ImagesUtil.getResourceImageCustomHref(null, "file");
        Assertions.assertEquals(0, actualHref.length());
    }

    @Test
    public void testGetResourceImageCustomHref_BlankResourceName() {
        String actualHref = ImagesUtil.getResourceImageCustomHref(resource, "");
        Assertions.assertEquals(0, actualHref.length());
    }

    @Test
    public void testGetPageImgReferencePath_Success() throws RepositoryException {
        when(page.getContentResource()).thenReturn(resource);
        when(resource.getChild("image")).thenReturn(resource);
        when(resource.adaptTo(Node.class)).thenReturn(node);
        when(node.hasProperty(IMAGE_FILEREFERENCE)).thenReturn(true);
        when(node.getProperty(IMAGE_FILEREFERENCE)).thenReturn(property);
        when(property.getString()).thenReturn("/content/aem.design/home/image");
        String actualPath = ImagesUtil.getPageImgReferencePath(page);
        Assertions.assertEquals("/content/aem.design/home/image", actualPath);
    }

    @Test
    public void testGetResourceImagePath_NullResource() throws RepositoryException {
        String actualPath = ImagesUtil.getResourceImagePath(null, ImagesUtil.DEFAULT_IMAGE_NODE_NAME);
        Assertions.assertEquals(0, actualPath.length());
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_Success() {
        when(damAsset.getMetadataValue("name")).thenReturn("value");
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(damAsset, "name", "defaultValue");
        Assertions.assertEquals("value", actualValue);
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_NullAsset() {
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(null, "name", "defaultValue");
        Assertions.assertEquals("defaultValue", actualValue);
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_NullValue() {
        when(damAsset.getMetadataValue("name")).thenReturn(null);
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(damAsset, "name", "defaultValue");
        Assertions.assertEquals("defaultValue", actualValue);
    }

    @Test
    public void testGetAssetCopyrightInfo_Success() throws XMPException {
        when(damAsset.getMetadataValue(DamConstants.DC_CREATOR)).thenReturn("assetCreator");
        when(damAsset.getMetadataValue(DamConstants.DC_CONTRIBUTOR)).thenReturn("assetContributor");
        when(damAsset.getMetadataValue(DamConstants.DC_RIGHTS)).thenReturn("assetLicense");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_COPYRIGHT_OWNER)).thenReturn("assetCopyrightOwner");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_USAGETERMS)).thenReturn("terms");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_EXPIRY)).thenReturn(null);
        String actualInfo = ImagesUtil.getAssetCopyrightInfo(damAsset, "{4} {0} {1} {2} {3}");
        Assertions.assertEquals(" assetCreator assetContributor assetLicense assetCopyrightOwner", actualInfo);
    }

    @Test
    public void testGetDimension_Success_Original() throws RepositoryException {
        when(graniteRendition.getName()).thenReturn("original");
        when(graniteRendition.adaptTo(com.adobe.granite.asset.api.Asset.class)).thenReturn(graniteAsset);
        when(graniteAsset.adaptTo(Node.class)).thenReturn(node);
        when(node.hasNode("jcr:content/metadata")).thenReturn(false);
        when(node.getNode("jcr:content/metadata")).thenReturn(node);
        when(node.hasProperty("tiff:ImageLength")).thenReturn(true);
        when(node.getProperty("tiff:ImageLength")).thenReturn(property);
        when(property.getString()).thenReturn("1000");
        int actualDimension = ImagesUtil.getDimension(graniteRendition, "tiff:ImageLength");
        Assertions.assertEquals(1000, actualDimension);
    }

    @Test
    public void testGetWidth_Success_Rendition() throws RepositoryException {
        when(graniteRendition.getName()).thenReturn("cq5dam.thumbnail.48.48.png");
        int actualDimension = ImagesUtil.getWidth(graniteRendition);
        Assertions.assertEquals(48, actualDimension);
    }

    @Test
    public void testGetAssetDuration_Success_NumericDMScale() {
        when(valueMap.get("xmpDM:scale", "")).thenReturn("31");
        when(valueMap.get("xmpDM:value", "")).thenReturn("123");
        Duration duration = ImagesUtil.getAssetDuration(valueMap);
        Assertions.assertEquals(123, duration.getSeconds());
    }

    @Test
    public void testGetAssetDuration_Success_NonNumericDMScale() {
        when(valueMap.get("xmpDM:scale", "")).thenReturn("1/44100");
        when(valueMap.get("xmpDM:value", "")).thenReturn("123");
        Duration duration = ImagesUtil.getAssetDuration(valueMap);
        Assertions.assertEquals(0, duration.getSeconds());
    }
}
