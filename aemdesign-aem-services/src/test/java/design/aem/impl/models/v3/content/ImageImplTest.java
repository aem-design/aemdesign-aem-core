/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.impl.models.v3.content;

import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ImageImplTest {
    protected static final String ROOT = "/asset";
    protected static final String IMAGE_01_PATH = ROOT + "/image/jcr:content/article/par/contentblock1/par/image";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(ROOT, ROOT);

    @Before
    public void setUp() {
        Resource testResource = CONTEXT.resourceResolver().getResource(IMAGE_01_PATH);

        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + IMAGE_01_PATH + "?");
        }
    }

    @Test
    public void testClass() {
        // Run the test
        ImageImpl test = new ImageImpl();

        // Verify the results
        assertNotNull(test);
    }
}
