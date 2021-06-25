package design.aem.utils.components;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConstantsUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUtilTest.class);

    @Test
    public void testClass() {
        // Run the test
        ConstantsUtil test = new ConstantsUtil();

        // Verify the results
        Assertions.assertNotNull(test);
    }

}
