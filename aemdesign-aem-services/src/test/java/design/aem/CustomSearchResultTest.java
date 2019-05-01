package design.aem;

import design.aem.utils.components.I18nUtilTest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomSearchResultTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSearchResultTest.class);

    private CustomSearchResult customSearchResultUnderTest;

    final String path = "path";
    final String excerpt = "excerpt";
    final String title = "title";
    final String subtitle = "subtitle";
    final String detailspath = "/content";
    final String thumbnalurl = "/content/thumbnailurl";
    final String pathurl = "/content/pathurl";

    @Before
    public void setUp() {
        customSearchResultUnderTest = new CustomSearchResult(path);
        customSearchResultUnderTest.setIsPage(true);
        customSearchResultUnderTest.setIsAsset(true);
        customSearchResultUnderTest.setExcerpt(excerpt);
        customSearchResultUnderTest.setTitle(title);
        customSearchResultUnderTest.setSubTitle(subtitle);
        customSearchResultUnderTest.setDetailsPath(detailspath);
        customSearchResultUnderTest.setThumbnailUrl(thumbnalurl);
        customSearchResultUnderTest.setPathUrl(pathurl);
        customSearchResultUnderTest.setPath(path);
    }

    @Test
    public void testClass() {
        // Run the test
        assertTrue(customSearchResultUnderTest.getIsPage());
        assertTrue(customSearchResultUnderTest.getIsAsset());
        assertEquals(customSearchResultUnderTest.getExcerpt(),excerpt);
        assertEquals(customSearchResultUnderTest.getTitle(),title);
        assertEquals(customSearchResultUnderTest.getSubTitle(),subtitle);
        assertEquals(customSearchResultUnderTest.getDetailsPath(),detailspath);
        assertEquals(customSearchResultUnderTest.getThumbnailUrl(),thumbnalurl);
        assertEquals(customSearchResultUnderTest.getPathUrl(),pathurl);
        assertEquals(customSearchResultUnderTest.getPath(),path);
    }

}
