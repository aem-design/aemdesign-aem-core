package design.aem.utils.components;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class ImagesUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesUtilTest.class);

    @Test
    public void testClass() {
        // Run the test
        ImagesUtil test = new ImagesUtil();

        // Verify the results
        assertNotNull(test);
    }

    @Test
    public void canRenderOnWeb() {

        assertTrue(ImagesUtil.canRenderOnWeb("jpeg") );
        assertTrue(ImagesUtil.canRenderOnWeb("jpg") );
        assertTrue(ImagesUtil.canRenderOnWeb("gif") );
        assertTrue(ImagesUtil.canRenderOnWeb("png") );

        assertFalse(ImagesUtil.canRenderOnWeb("bmp") );
    }

}