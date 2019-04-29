package design.aem.context;

import com.adobe.cq.wcm.core.components.testing.MockAdapterFactory;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

/**
 * Provides a context for unit tests.
 */
public final class CoreComponentTestContext {

    public static final String TEST_CONTENT_JSON = "/test-content.json";


    private CoreComponentTestContext() {
        // only static methods
    }

    public static AemContext createContext() {
        return createContext(null, null);
    }

    /**
     * Creates a new instance of {@link AemContext}, adds the project specific Sling Models and loads test data from the JSON file
     * "/test-content.json" in the current classpath
     *
     * @param testBase    Prefix of the classpath resource to load test data from. Optional, can be null. If null, test data will be
     *                    loaded from /test-content.json
     * @param contentRoot Path to import the JSON content to
     * @return New instance of {@link AemContext}
     */
    public static AemContext createContext(final String testBase, final String contentRoot) {
        return new AemContext(
                (AemContextCallback) context -> {
                    context.registerService(ImplementationPicker.class, new ResourceTypeBasedResourcePicker());
                    if (testBase != null) {
                        if (StringUtils.isNotEmpty(testBase)) {
                            context.load().json(testBase + TEST_CONTENT_JSON, contentRoot);
                        } else {
                            context.load().json(TEST_CONTENT_JSON, contentRoot);
                        }
                    }
                    context.registerInjectActivateService(new MockAdapterFactory());
                },
                ResourceResolverType.JCR_MOCK
        );
    }
}
