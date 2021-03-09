package design.aem.utils;

import design.aem.CustomSearchResult;
import design.aem.context.CoreComponentTestContext;
import design.aem.utils.components.SecurityUtilTest;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.MockitoAnnotations.openMocks;

public class WidthBasedRenditionComparatorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtilTest.class);

    private static WidthBasedRenditionComparator widthBasedRenditionComparatorUnderTest;
    protected static final String ROOT = "/content";

    @Mock
    MockResourceResolver resourceResolver;

    @Mock
    ResourceUtil resourceUtil;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

        widthBasedRenditionComparatorUnderTest = new WidthBasedRenditionComparator();
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    public void testClass() {
        // Run the test
        WidthBasedRenditionComparator test = new WidthBasedRenditionComparator();

        // Verify the results
        Assertions.assertNotNull(test);
    }

}
