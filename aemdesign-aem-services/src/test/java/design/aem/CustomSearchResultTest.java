package design.aem;

import design.aem.context.CoreComponentTestContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.openMocks;


public class CustomSearchResultTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSearchResultTest.class);

    private CustomSearchResult customSearchResultUnderTest;

    final static String path = "path";
    final static String excerpt = "excerpt";
    final static String title = "title";
    final static String subtitle = "subtitle";
    final static String detailspath = "/content";
    final static String thumbnalurl = "/content/thumbnailurl";
    final static String pathurl = "/content/pathurl";


    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

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

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    void testClass() {
        // Run the test
        assertTrue(customSearchResultUnderTest.getIsPage());
        assertTrue(customSearchResultUnderTest.getIsAsset());
        assertEquals(customSearchResultUnderTest.getExcerpt(), excerpt);
        assertEquals(customSearchResultUnderTest.getTitle(), title);
        assertEquals(customSearchResultUnderTest.getSubTitle(), subtitle);
        assertEquals(customSearchResultUnderTest.getDetailsPath(), detailspath);
        assertEquals(customSearchResultUnderTest.getThumbnailUrl(), thumbnalurl);
        assertEquals(customSearchResultUnderTest.getPathUrl(), pathurl);
        assertEquals(customSearchResultUnderTest.getPath(), path);
    }

}
