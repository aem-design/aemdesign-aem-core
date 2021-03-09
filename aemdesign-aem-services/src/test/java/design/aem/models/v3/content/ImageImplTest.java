/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.models.v3.content;

import ch.qos.logback.classic.Logger;
import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

import static org.mockito.MockitoAnnotations.openMocks;

//@ExtendWith(AemContextExtension.class)
public class ImageImplTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ImageImplTest.class);

    protected static String TEST_BASE = "/asset";

    protected static final String TEST_CONTENT_ROOT = "/content";
    protected static final String IMAGE_01_PATH = TEST_CONTENT_ROOT + "/image/jcr:content/article/par/contentblock1/par/image";


    MockSlingHttpServletRequest request;
    MockSlingHttpServletResponse response;
    private Resource testResource;

    protected final AemContext CONTEXT = CoreComponentTestContext.newAemContext();

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

        CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);

        request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        response = new MockSlingHttpServletResponse();

    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    public void testResourceConfig() {

        testResource = CONTEXT.resourceResolver().getResource(IMAGE_01_PATH);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + IMAGE_01_PATH + "?");
        }

    }

    @Test
    public void testClass() {
        // Run the test
        ImageImpl test = new ImageImpl();

        // Verify the results
        Assertions.assertNotNull(test);
    }


}
