package design.aem.utils.components;

import com.adobe.xmp.XMPException;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.Image;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_IMAGE_GENERATED_FORMAT;
import static design.aem.utils.components.ComponentsUtil.DEFAULT_IMAGE_RESOURCETYPE;
import static design.aem.utils.components.ConstantsUtil.IMAGE_FILEREFERENCE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DamUtil.class, ImagesUtil.class, MessageFormat.class})
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
    ValueMap valueMap;

    @Mock
    Property property;

    private static final String PATH = "/content/aem.design/en/home";
    @Mock
    Page page;
    @Mock
    Image image;

    @Before
    public void before() {
        initMocks(this);
        PowerMockito.mockStatic(DamUtil.class);
        PowerMockito.mockStatic(MessageFormat.class);
    }

    @Test
    public void testClass() {
        // Run the test
        ImagesUtil test = new ImagesUtil();

        // Verify the results
        assertNotNull(test);
    }

    @Test
    public void canRenderOnWeb() {

        assertTrue(ImagesUtil.canRenderOnWeb("jpeg"));
        assertTrue(ImagesUtil.canRenderOnWeb("jpg"));
        assertTrue(ImagesUtil.canRenderOnWeb("gif"));
        assertTrue(ImagesUtil.canRenderOnWeb("png"));

        assertFalse(ImagesUtil.canRenderOnWeb("bmp"));
    }

    @Test
    public void testGetMetadataStringForKey_Success() throws RepositoryException {
        when(node.hasNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(true);
        when(node.getNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(node);
        when(DamUtil.getValue(node, "key", "defaultValue")).thenReturn("value");
        String actualValue = ImagesUtil.getMetadataStringForKey(node, "key", "defaultValue");
        Assert.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_NullNode() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(null, "key", "defaultValue");
        Assert.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(node, "", "defaultValue");
        Assert.assertNull(actualValue);
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
        Assert.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromGraniteAsset_NullAsset() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey((com.adobe.granite.asset.api.Asset) null, "key");
        Assert.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromGraniteAsset_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(graniteAsset, "");
        Assert.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_Success() throws RepositoryException {
        when(damAsset.getMetadata("key")).thenReturn("value");
        String actualValue = ImagesUtil.getMetadataStringForKey(damAsset, "key");
        Assert.assertEquals("value", actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_NullAsset() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey((com.day.cq.dam.api.Asset) null, "key");
        Assert.assertNull(actualValue);
    }

    @Test
    public void testGetMetadataStringForKey_FromDamAsset_BlankKey() throws RepositoryException {
        String actualValue = ImagesUtil.getMetadataStringForKey(damAsset, "");
        Assert.assertNull(actualValue);
    }

    @Test
    public void testGetThumbnail_Success() {
        List<Rendition> renditionList = new ArrayList<>();
        when(damAsset.getRenditions()).thenReturn(renditionList);
        when(DamUtil.getBestFitRendition(300, renditionList)).thenReturn(rendition);
        Rendition actualRendition = ImagesUtil.getThumbnail(damAsset, 300);
        Assert.assertEquals(rendition, actualRendition);
    }

    @Test
    public void testGetThumbnailUrl_Success() {
        when(page.getPath()).thenReturn(PATH);
        when(resolver.map(PATH.concat(ImagesUtil.DEFAULT_THUMB_SELECTOR_MD))).thenReturn("/content/aem.design/en/home/jcr:content/image/file.sftmp");
        String actualUrl = ImagesUtil.getThumbnailUrl(page, resolver);
        Assert.assertEquals("/content/aem.design/en/home/jcr:content/image/file.sftmp", actualUrl);
    }

    @Test
    public void testGetWidth_Success() throws RepositoryException {
        when(node.hasNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(true);
        when(node.getNode(ImagesUtil.ASSET_METADATA_FOLDER)).thenReturn(node);
        when(DamUtil.getValue(node, "exif:PixelXDimension", "")).thenReturn("300");
        when(DamUtil.getValue(node, "tiff:ImageWidth", "300")).thenReturn("320");
        int actualWidth = ImagesUtil.getWidth(node);
        Assert.assertEquals(320, actualWidth);
    }

    @Test
    public void testGetProcessedImage_Success() throws Exception {
        whenNew(Image.class).withArguments(resource, "/data/image").thenReturn(image);
        Image actualImage = ImagesUtil.getProcessedImage(resource, "/data/image");
        Assert.assertEquals(image, actualImage);
    }

    @Test
    public void testGetProcessedImage_Success_NullPath() throws Exception {
        whenNew(Image.class).withArguments(resource).thenReturn(image);
        Image actualImage = ImagesUtil.getProcessedImage(resource, null);
        Assert.assertEquals(image, actualImage);
    }

    @Test
    public void testGetResourceImageCustomHref_Success() {
        when(resource.getChild("file")).thenReturn(resource);
        when(resource.getChild(IMAGE_FILEREFERENCE)).thenReturn(resource);
        when(resource.getResourceType()).thenReturn(DEFAULT_IMAGE_RESOURCETYPE);
        when(resource.adaptTo(ValueMap.class)).thenReturn(valueMap);
        when(valueMap.get(JcrConstants.JCR_LASTMODIFIED, Long.class)).thenReturn(1346524199000L);
        when(resource.getPath()).thenReturn(PATH);
        when(MessageFormat.format(DEFAULT_IMAGE_GENERATED_FORMAT, PATH, "1346524199000")).thenReturn("/content/url");
        when(resource.getResourceResolver()).thenReturn(resolver);
        when(resolver.map("/content/url")).thenReturn("href");
        String actualHref = ImagesUtil.getResourceImageCustomHref(resource, "file");
        Assert.assertEquals("href", actualHref);
    }

    @Test
    public void testGetResourceImageCustomHref_NullResource() {
        String actualHref = ImagesUtil.getResourceImageCustomHref(null, "file");
        Assert.assertEquals(0, actualHref.length());
    }

    @Test
    public void testGetResourceImageCustomHref_BlankResourceName() {
        String actualHref = ImagesUtil.getResourceImageCustomHref(resource, "");
        Assert.assertEquals(0, actualHref.length());
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
        Assert.assertEquals("/content/aem.design/home/image", actualPath);
    }

    @Test
    public void testGetResourceImagePath_NullResource() throws RepositoryException {
        String actualPath = ImagesUtil.getResourceImagePath(null, ImagesUtil.DEFAULT_IMAGE_NODE_NAME);
        Assert.assertEquals(0, actualPath.length());
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_Success() {
        when(damAsset.getMetadataValue("name")).thenReturn("value");
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(damAsset, "name", "defaultValue");
        Assert.assertEquals("value", actualValue);
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_NullAsset() {
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(null, "name", "defaultValue");
        Assert.assertEquals("defaultValue", actualValue);
    }

    @Test
    public void testGetAssetPropertyValueWithDefault_NullValue() {
        when(damAsset.getMetadataValue("name")).thenReturn(null);
        String actualValue = ImagesUtil.getAssetPropertyValueWithDefault(damAsset, "name", "defaultValue");
        Assert.assertEquals("defaultValue", actualValue);
    }

    @Test
    public void testGetAssetCopyrightInfo_Success() throws XMPException {
        when(damAsset.getMetadataValue(DamConstants.DC_CREATOR)).thenReturn("assetCreator");
        when(damAsset.getMetadataValue(DamConstants.DC_CONTRIBUTOR)).thenReturn("assetContributor");
        when(damAsset.getMetadataValue(DamConstants.DC_RIGHTS)).thenReturn("assetLicense");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_COPYRIGHT_OWNER)).thenReturn("assetCopyrightOwner");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_USAGETERMS)).thenReturn("terms");
        when(damAsset.getMetadataValue(CommonUtil.DAM_FIELD_LICENSE_EXPIRY)).thenReturn(null);
        when(MessageFormat.format("format", "assetCreator", "assetContributor", "assetLicense", "assetCopyrightOwner", "")).thenReturn("info");
        String actualInfo = ImagesUtil.getAssetCopyrightInfo(damAsset, "format");
        Assert.assertEquals("info", actualInfo);
    }

}
