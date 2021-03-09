package design.aem.utils.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.ResolverUtil.DEFAULT_MAP_CONFIG_SCHEMA;
import static design.aem.utils.components.ResolverUtil.SECURE_MAP_CONFIG_SCHEMA;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ResolverUtilTest {

    protected String TEST_BASE = "/content";

    protected final String TEST_CONTENT_ROOT = "/content";

    public final AemContext CONTEXT = CoreComponentTestContext.newAemContext();
    protected final String PAGE = TEST_CONTENT_ROOT + "/page";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResolverUtil.class);
    static MockSlingHttpServletRequest request;
    static MockSlingHttpServletResponse response;
    @Mock
    MockResourceResolver resourceResolver;

    @Mock
    ResourceUtil resourceUtil;

    @Mock
    static Externalizer externalizer;
    private Resource testResource;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = openMocks(this);

        CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);

        request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        response = new MockSlingHttpServletResponse();

        CONTEXT.registerService(Externalizer.class, externalizer,
            JcrConstants.JCR_PRIMARYTYPE, "sling:OsgiConfig",
            "externalizer.domains", "[local http://localhost:4502,author http://localhost:4502,publish http://localhost:4503]",
            "externalizer.contextpath", "",
            "externalizer.host", "localhost",
            "externalizer.encodedpath", false
        );

        testResource = CONTEXT.resourceResolver().getResource(PAGE);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PAGE + "?");
        }
    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }


    @Test
    public void testClass() {
        // Run the test
        ResolverUtil test = new ResolverUtil();

        // Verify the results
        Assertions.assertNotNull(test);
    }


    @Test
    public void testMappedUrl() {
        // Setup
        final String path = "/content";
        final String expectedResult = "/content";

        // Run the test
        final String result = ResolverUtil.mappedUrl(CONTEXT.resourceResolver(), path);


        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testMappedUrlNull() {
        // Setup
        final String path = null;
        final String expectedResult = null;

        // Run the test
        final String result = ResolverUtil.mappedUrl(CONTEXT.resourceResolver(), path);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerHTTP() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "http://localhost:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, DEFAULT_MAP_CONFIG_SCHEMA,
            path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result = ResolverUtil.mappedUrl(resourceResolver, request, domain, path);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerNotSecure() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "http://localhost:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, DEFAULT_MAP_CONFIG_SCHEMA,
            path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result = ResolverUtil.mappedUrl(resourceResolver, request, path);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerSecure() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "https://localhost:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, SECURE_MAP_CONFIG_SCHEMA,
            path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result = ResolverUtil.mappedUrl(resourceResolver, request, path, true);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerHTTPS() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "https://localhost:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, SECURE_MAP_CONFIG_SCHEMA,
            path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result = ResolverUtil.mappedUrl(resourceResolver, request, domain, path, true);

        // Verify the results
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testMappedUrlAllNulls() {

        // Run the test
        final String result = ResolverUtil.mappedUrl(null, null, null, null, null);

        // Verify the results
        Assertions.assertEquals(null, result);
    }

    @Test
    public void testIfPassingNullParamsToFunctionReturnNull() {

        // Run the test
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, null, null, "path", false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, null, "local", "path", false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, request, "local", "path", false));


        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request, "local", "path", null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request, "local", null, null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request, null, null, null));

        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, null, null, false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, "local", "path", false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, null, "path", false));


        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, null, null, null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, request, null, null, null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, null, "local", null, null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, null, null, "path", null));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(null, null, null, null, false));

        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, "local", "path", false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request, null, "path", false));
        Assertions.assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request, "local", null, false));


    }

    @Disabled
    @Test
    public void testCheckResourceHasChildResource() {
        // Setup
        final String resourceName = "variant.default.html";
        final String resourceComponent = "/apps/aemdesign/components/layout/article";
        final String resourceComponentVersion = "v2";
        final String resourceComponentName = "article";
        final String resourceSuperType = "aemdesign/components/layout/article/v2/article";

        //create component structure
        CONTEXT.build()
            .hierarchyMode()
            .resource(resourceComponent, JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY, resourceSuperType)
            .resource(resourceComponentVersion, JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER)
            .resource(resourceComponentName, JcrConstants.JCR_PRIMARYTYPE, NameConstants.NT_COMPONENT)
            .resource(resourceName, JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER);


        when(resourceUtil.findResourceSuperType(CONTEXT.resourceResolver().getResource(resourceComponent))).thenReturn(resourceSuperType);
        when(resourceResolver.getResource(resourceSuperType + "/" + resourceName)).thenReturn(CONTEXT.resourceResolver().getResource(resourceSuperType + "/" + resourceName));

        // Run the test
        final boolean result = ResolverUtil.checkResourceHasChildResource(resourceName, CONTEXT.resourceResolver().getResource(resourceComponent), resourceResolver);


        // Verify the results
        Assertions.assertTrue(result);
    }
}
