package design.aem.utils.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TenantUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUtilTest.class);

    @Test
    public void testClass() {
        // Run the test
        TenantUtil test = new TenantUtil();

        // Verify the results
        Assertions.assertNotNull(test);
    }


    @Test
    public void testResolveTenantIdFromPath() {
        // Setup
        final String path = "/content/aemdesign/test";
        final String expectedResult = "aemdesign";

        // Run the test
        final String result = TenantUtil.resolveTenantIdFromPath(path);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testResolveTenantIdFromPathMultiMatch() {
        // Setup
        final String path = "/content/cq:tags/aemdesign1/cq:tags/aemdesign2";
        final String expectedResult = "aemdesign1";

        // Run the test
        final String result = TenantUtil.resolveTenantIdFromPath(path);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testResolveTenantIdFromPathNotMatch() {
        // Setup
        final String path = "/apps/aemdesign/";

        // Run the test
        final String result = TenantUtil.resolveTenantIdFromPath(path);

        // Verify the results
        Assertions.assertEquals(null, result);
    }

    @Test
    public void testResolveTenantIdFromNull() {
        // Run the test
        final String result = TenantUtil.resolveTenantIdFromPath("");

        // Verify the results
        Assertions.assertEquals(null, result);
    }
}
