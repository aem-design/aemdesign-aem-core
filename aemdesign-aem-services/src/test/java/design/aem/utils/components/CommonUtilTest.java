package design.aem.utils.components;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.context.CoreComponentTestContext;
import design.aem.utils.WidthBasedRenditionComparator;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;

import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;

public class CommonUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private static final String TEST_BASE = "/content";

    protected static final String TEST_CONTENT_ROOT = "/content";
    protected static final String PAGE = TEST_CONTENT_ROOT + "/page";


    private final String PRIMARY_TYPE = JcrConstants.JCR_PRIMARYTYPE;
    private final String CQ_PAGE = NameConstants.NT_PAGE;

    private Resource testResource = null;
    private Page testResourcePage = null;
    private Node testResourceNode = null;
    private Resource testResourcePageContent = null;


    public final AemContext CONTEXT = CoreComponentTestContext.newAemContext();


    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

        CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);

        //get test resource content
        testResource = CONTEXT.resourceResolver().getResource(PAGE);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PAGE + "?");
        }

        Session mockSession = mock(Session.class);
        Page mockPage = mock(Page.class);
        Resource mockResource = mock(Resource.class);
        CONTEXT.registerAdapter(ResourceResolver.class, Session.class, mockSession);
        CONTEXT.registerAdapter(Resource.class, Page.class, mockPage);
        CONTEXT.registerAdapter(Page.class, Resource.class, mockResource);

        testResourceNode = testResource.adaptTo(Node.class);

        LOGGER.error("testResource: {}", testResource);
        LOGGER.error("testResourceNode: {}", testResourceNode);

        PageManager pm = mock(PageManager.class);
        testResourcePage = pm.getPage(PAGE);

        LOGGER.error("testResourcePage1: {}", testResourcePage);

        testResourcePage = testResource.adaptTo(Page.class);

        LOGGER.error("testResourcePage2: {}", testResourcePage);

    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    public void testValidDetailComponent() {
        // Setup
        final String resourceType = "aemdesign/components/details/page-details";

        // Run the test
        final boolean result = CommonUtil.validDetailComponent(resourceType);

        // Verify the results
        Assertions.assertTrue(result);
    }

    @Disabled
    @Test
    public void testNodeExists() {
        // Setup
        final String nodeName = "article";

        // Run the test
        final boolean result = CommonUtil.nodeExists(testResourcePage, nodeName);

        // Verify the results
        Assertions.assertTrue(result);
    }

    @Disabled
    @Test
    public void testPageIsOn() {
        // Run the test
        final boolean result = CommonUtil.pageIsOn(testResourcePage);

        // Verify the results
        Assertions.assertTrue(result);
    }

    @Test
    public void testHashMd5() {
        // Setup
        final String content = "content";
        final String expectedResult = "9a0364b9e99bb480dd25e1f0284c8555";

        // Run the test
        final String result = CommonUtil.hashMd5(content);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testGetSingularProperty() throws Exception {

        Assertions.assertEquals(CommonUtil.getSingularProperty(testResourceNode, PRIMARY_TYPE).getString(), CQ_PAGE);

    }

}
