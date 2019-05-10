package design.aem.utils;

import com.adobe.granite.asset.api.Rendition;
import design.aem.context.CoreComponentTestContext;
import design.aem.utils.components.SecurityUtil;
import design.aem.utils.components.SecurityUtilTest;
import com.adobe.granite.asset.api.Asset;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;

import static design.aem.utils.components.CommonUtilTest.getTestBase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WidthBasedRenditionComparatorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtilTest.class);

    private WidthBasedRenditionComparator widthBasedRenditionComparatorUnderTest;
    protected static final String ROOT = "/content";


    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(getTestBase(), ROOT);


    @Mock
    MockResourceResolver resourceResolver;

    @Mock
    ResourceUtil resourceUtil;

    @Before
    public void setUp() {
        widthBasedRenditionComparatorUnderTest = new WidthBasedRenditionComparator();
    }

    @Test
    public void testClass() {
        // Run the test
        WidthBasedRenditionComparator test = new WidthBasedRenditionComparator();

        // Verify the results
        assertNotNull(test);
    }
//
//    @Test
//    public void testCompare() throws Exception {
//        // Setup
//        final int expectedResult = 0;
//
//        final String resourcePath = "/content/dam/aemdesign/en/common/aem-design.png";
//        final String resourceType = "dam:Asset";
//        final String resourceComponentVersion = "v2";
//        final String resourceComponentName = "article";
//
//        //create component structure
//        CONTEXT.build()
//                .hierarchyMode()
//                .resource(resourcePath, "sling:resourceSuperType", resourceType)
//                .resource("jcr:content", "jcr:primaryType", "dam:AssetContent")
//                .resource("renditions", "jcr:primaryType", "nt:folder")
//                .siblingsMode()
//                .resource("cq5dam.thumbnail.140.100.png", "jcr:primaryType", "nt:file")
//                .resource("cq5dam.thumbnail.319.319.png", "jcr:primaryType", "nt:file")
//                .resource("cq5dam.thumbnail.48.48.png", "jcr:primaryType", "nt:file")
//                .resource("cq5dam.thumbnail.1280.1280.png", "jcr:primaryType", "nt:file")
//                .resource("cqdam.pyramid.tiff", "jcr:primaryType", "nt:file")
//                .resource("original", "jcr:primaryType", "nt:file");
//
//        Rendition r1 = mock(Rendition.class);
//        Rendition r2 = mock(Rendition.class);
//        Asset asset1 = mock(Asset.class);
//        Asset asset2 = mock(Asset.class);
//        Node assetNode1 = mock(Node.class);
//        Node assetNode2 = mock(Node.class);
//        Node assetMetaData1 = mock(Node.class);
//        Node assetMetaData2 = mock(Node.class);
//        Property assetMetaDataProperty1 = mock(Property.class);
//        Property assetMetaDataProperty2 = mock(Property.class);
//
//        when(r1.getPath()).thenReturn("cq5dam.thumbnail.140.100.png");
//        when(r2.getPath()).thenReturn("cq5dam.thumbnail.319.319.png");
//        when(r1.adaptTo(Asset.class)).thenReturn(asset1);
//        when(r2.adaptTo(Asset.class)).thenReturn(asset2);
//        when(assetNode1.hasNode("jcr:content/metadata")).thenReturn(true);
//        when(assetNode2.hasNode("jcr:content/metadata")).thenReturn(true);
//
//        when(assetNode1.getNode("jcr:content/metadata")).thenReturn(assetMetaData1);
//        when(assetNode2.getNode("jcr:content/metadata")).thenReturn(assetMetaData2);
//
//        when(assetMetaData1.hasProperty("tiff:ImageWidth")).thenReturn(true);
//        when(assetMetaData2.hasProperty("tiff:ImageWidth")).thenReturn(true);
//
//        when(assetMetaData1.getProperty("tiff:ImageWidth")).thenReturn(assetMetaDataProperty1);
//        when(assetMetaData2.getProperty("tiff:ImageWidth")).thenReturn(assetMetaDataProperty2);
//
//        when(assetMetaDataProperty1.toString()).thenReturn("140");
//        when(assetMetaDataProperty2.toString()).thenReturn("319");
//
//        LOGGER.error("CONTEXT.resourceResolver().getResource(resourcePath)={}", CONTEXT.resourceResolver().getResource(resourcePath));
//        LOGGER.error("CONTEXT.resourceResolver().getResource(r1)={}", r1.getPath());
//        LOGGER.error("CONTEXT.resourceResolver().getResource(assetMetaDataProperty1)={}", assetMetaDataProperty1.toString());
//        LOGGER.error("CONTEXT.resourceResolver().getResource(r2)={}", r2.getPath());
//        LOGGER.error("CONTEXT.resourceResolver().getResource(assetMetaDataProperty2)={}", assetMetaDataProperty2.toString());
//
//
//        // Run the test
//        assertEquals(0, widthBasedRenditionComparatorUnderTest.compare(r1, r2));
//        assertEquals(-1, widthBasedRenditionComparatorUnderTest.compare(r2, r1));
//        assertEquals(1, widthBasedRenditionComparatorUnderTest.compare(r1, r1));
//    }
}
