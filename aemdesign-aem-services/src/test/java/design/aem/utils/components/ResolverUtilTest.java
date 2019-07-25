package design.aem.utils.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.CommonUtilTest.getTestBase;
import static design.aem.utils.components.ResolverUtil.DEFAULT_MAP_CONFIG_SCHEMA;
import static design.aem.utils.components.ResolverUtil.SECURE_MAP_CONFIG_SCHEMA;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResolverUtilTest {


    protected static final String ROOT = "/content";
    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(getTestBase(), ROOT);
    protected static final String PAGE = ROOT + "/page";
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolverUtil.class);
    MockSlingHttpServletRequest request;
    MockSlingHttpServletResponse response;
    @Mock
    MockResourceResolver resourceResolver;

    @Mock
    ResourceUtil resourceUtil;

    @Mock
    Externalizer externalizer;
    private Resource testResource;

    @Before
    public void setUp() throws Exception {
        request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        response = new MockSlingHttpServletResponse();

        CONTEXT.registerService(Externalizer.class, externalizer,
                JcrConstants.JCR_PRIMARYTYPE, "sling:OsgiConfig",
                "externalizer.domains", "[local http://192.168.27.2:4502,author http://192.168.27.2:4502,publish http://192.168.27.2:4503]",
                "externalizer.contextpath", "",
                "externalizer.host", "192.168.27.2",
                "externalizer.encodedpath", false
            );

        testResource = CONTEXT.resourceResolver().getResource(PAGE);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PAGE + "?");
        }
    }

    @Test
    public void testClass() {
        // Run the test
        ResolverUtil test = new ResolverUtil();

        // Verify the results
        assertNotNull(test);
    }



    @Test
    public void testMappedUrl() {
        // Setup
        final String path = "/content";
        final String expectedResult = "/content";

        // Run the test
        final String result = ResolverUtil.mappedUrl(CONTEXT.resourceResolver(), path);


        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testMappedUrlNull() {
        // Setup
        final String path = null;
        final String expectedResult = null;

        // Run the test
        final String result = ResolverUtil.mappedUrl(CONTEXT.resourceResolver(), path);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerHTTP() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "http://192.168.27.2:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, DEFAULT_MAP_CONFIG_SCHEMA,
                path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result =  ResolverUtil.mappedUrl(resourceResolver, request, domain, path);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerNotSecure() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "http://192.168.27.2:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, DEFAULT_MAP_CONFIG_SCHEMA,
                path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result =  ResolverUtil.mappedUrl(resourceResolver, request, path);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerSecure() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "https://192.168.27.2:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, SECURE_MAP_CONFIG_SCHEMA,
                path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result =  ResolverUtil.mappedUrl(resourceResolver, request, path, true);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testMappedUrlExternalizerHTTPS() {
        // Setup
        final String path = "/content/externalize-me.html";
        final String domain = "local";
        final String expectedResult = "https://192.168.27.2:4502/content/externalize-me.html";

        request.setResource(testResource);


        when(externalizer.externalLink(resourceResolver, domain, SECURE_MAP_CONFIG_SCHEMA,
                path)).thenReturn(expectedResult);

        when(resourceResolver.adaptTo(Externalizer.class)).thenReturn(externalizer);
        when(resourceResolver.map(path)).thenReturn(path);
        when(resourceResolver.map(null, path)).thenReturn(path);


        // Run the test
        final String result =  ResolverUtil.mappedUrl(resourceResolver, request, domain, path, true);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testMappedUrlAllNulls() {

        // Run the test
        final String result = ResolverUtil.mappedUrl(null, null,null,null,null);

        // Verify the results
        assertEquals(null, result);
    }

    @Test
    public void testIfPassingNullParamsToFunctionReturnNull() {

        // Run the test
        assertEquals(null, ResolverUtil.mappedUrl(null, null,null,"path",false));
        assertEquals(null, ResolverUtil.mappedUrl(null, null,"local","path",false));
        assertEquals(null, ResolverUtil.mappedUrl(null, request,"local","path",false));


        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request,"local","path",null));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request,"local",null,null));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request,null,null,null));

        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, null, null, false));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null,"local","path",false));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null,null,"path",false));


        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null, null, null, null));
        assertEquals(null, ResolverUtil.mappedUrl(null, request,null, null, null));
        assertEquals(null, ResolverUtil.mappedUrl(null, null,"local",null, null));
        assertEquals(null, ResolverUtil.mappedUrl(null, null,null,"path",null));
        assertEquals(null, ResolverUtil.mappedUrl(null, null, null, null,false));

        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, null,"local","path",false));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request,null,"path",false));
        assertEquals(null, ResolverUtil.mappedUrl(resourceResolver, request,"local",null,false));


    }

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
        assertTrue(result);
    }
}
