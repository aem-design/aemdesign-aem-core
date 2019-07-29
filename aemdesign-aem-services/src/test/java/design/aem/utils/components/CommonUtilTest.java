package design.aem.utils.components;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil commonUtilUnderTest;

    private static final String TEST_BASE = "/content";
    protected static final String ROOT = "/content";
    protected static final String PAGE = ROOT + "/page";


    private final String PRIMARY_TYPE = JcrConstants.JCR_PRIMARYTYPE;
    private final String CQ_PAGE = NameConstants.NT_PAGE;

    private Resource testResource = null;
    private Page testResourcePage = null;
    private Node testResourceNode = null;


    public static String getTestBase() {
        return TEST_BASE;
    }

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(getTestBase(), ROOT);



    @Before
    public void setUp() {

        commonUtilUnderTest = new CommonUtil();

        //get test resource content
        testResource = CONTEXT.resourceResolver().getResource(PAGE);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PAGE + "?");
        }

        testResourceNode = testResource.adaptTo(Node.class);
        testResourcePage = testResource.adaptTo(Page.class);


    }


    @Test
    public void testValidDetailComponent() {
        // Setup
        final String resourceType = "aemdesign/components/details/page-details";

        // Run the test
        final boolean result = CommonUtil.validDetailComponent(resourceType);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testNodeExists() {
        // Setup
        final String nodeName = "article";

        // Run the test
        final boolean result = CommonUtil.nodeExists(testResourcePage, nodeName);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testPageIsOn() {
        // Run the test
        final boolean result = CommonUtil.pageIsOn(testResourcePage);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testHashMd5() {
        // Setup
        final String content = "content";
        final String expectedResult = "9a0364b9e99bb480dd25e1f0284c8555";

        // Run the test
        final String result = CommonUtil.hashMd5(content);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testGetSingularProperty() throws Exception {

        assertEquals(CommonUtil.getSingularProperty(testResourceNode, PRIMARY_TYPE).getString(),CQ_PAGE);

    }

}
