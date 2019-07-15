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

}
