package design.aem.utils.components;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

public class ConstantsUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUtilTest.class);


    @Test
    public void testClass() {
        // Run the test
        ConstantsUtil test = new ConstantsUtil();

        // Verify the results
        assertNotNull(test);
    }

}
