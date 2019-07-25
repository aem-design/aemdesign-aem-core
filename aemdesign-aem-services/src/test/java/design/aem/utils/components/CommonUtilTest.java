package design.aem.utils.components;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import design.aem.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommonUtilTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil commonUtilUnderTest;

    private static final String TEST_BASE = "/content";
    protected static final String ROOT = "/content";
    protected static final String PAGE = ROOT + "/page";


    private final String PRIMARY_TYPE = JcrConstants.JCR_PRIMARYTYPE;
    private final String CQ_PAGE = NameConstants.NT_PAGE;

    private Resource testResource = null;
    private Page testResourcePage = null;
    private Node testResourceNode = null;


    public static String getTestBase() {
        return TEST_BASE;
    }

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(getTestBase(), ROOT);



    @Before
    public void setUp() {

        commonUtilUnderTest = new CommonUtil();

        //get test resource content
        testResource = CONTEXT.resourceResolver().getResource(PAGE);
        if (testResource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + PAGE + "?");
        }

        testResourceNode = testResource.adaptTo(Node.class);
        testResourcePage = testResource.adaptTo(Page.class);


    }


    @Test
    public void testValidDetailComponent() {
        // Setup
        final String resourceType = "aemdesign/components/details/page-details";

        // Run the test
        final boolean result = CommonUtil.validDetailComponent(resourceType);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testNodeExists() {
        // Setup
        final String nodeName = "article";

        // Run the test
        final boolean result = CommonUtil.nodeExists(testResourcePage, nodeName);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testPageIsOn() {
        // Run the test
        final boolean result = CommonUtil.pageIsOn(testResourcePage);

        // Verify the results
        assertTrue(result);
    }

    @Test
    public void testHashMd5() {
        // Setup
        final String content = "content";
        final String expectedResult = "9a0364b9e99bb480dd25e1f0284c8555";

        // Run the test
        final String result = CommonUtil.hashMd5(content);

        // Verify the results
        assertEquals(expectedResult, result);
    }


    @Test
    public void testGetSingularProperty() throws Exception {

        assertEquals(CommonUtil.getSingularProperty(testResourceNode, PRIMARY_TYPE).getString(),CQ_PAGE);

    }
//
//    @Test
//    public void testGetSingularPropertyString() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getSingularPropertyString(node, key);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetSingularPropertyString_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//
//        // Run the test
//        CommonUtil.getSingularPropertyString(node, key);
//    }
//
//    @Test
//    public void testGetMultipleProperty() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//        final Value[] expectedResult = new Value[]{};
//
//        // Run the test
//        final Value[] result = CommonUtil.getMultipleProperty(node, key);
//
//        // Verify the results
//        assertArrayEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetMultipleProperty_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//
//        // Run the test
//        CommonUtil.getMultipleProperty(node, key);
//    }
//
//    @Test
//    public void testGetMultiplePropertyString() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//        final String[] expectedResult = new String[]{};
//
//        // Run the test
//        final String[] result = CommonUtil.getMultiplePropertyString(node, key);
//
//        // Verify the results
//        assertArrayEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetMultiplePropertyString_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Node node = null;
//        final String key = "key";
//
//        // Run the test
//        CommonUtil.getMultiplePropertyString(node, key);
//    }
//
//    @Test
//    public void testGetPageBadgeBase1() throws Exception {
//        // Setup
//        final Page inputPage = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getPageBadgeBase(inputPage);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetPageBadgeBase_ThrowsRepositoryException1() throws Exception {
//        // Setup
//        final Page inputPage = null;
//
//        // Run the test
//        CommonUtil.getPageBadgeBase(inputPage);
//    }
//
//    @Test
//    public void testGetPageBadgeBase2() throws Exception {
//        // Setup
//        final Page inputPage = null;
//        final String resourceName = "resourceName";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getPageBadgeBase(inputPage, resourceName);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetPageBadgeBase_ThrowsRepositoryException2() throws Exception {
//        // Setup
//        final Page inputPage = null;
//        final String resourceName = "resourceName";
//
//        // Run the test
//        CommonUtil.getPageBadgeBase(inputPage, resourceName);
//    }
//
//    @Test
//    public void testFindComponentInPage() {
//        // Setup
//        final Page inputPage = null;
//        final String[] resourceTypeTail = new String[]{};
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.findComponentInPage(inputPage, resourceTypeTail);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testFindComponentInPage1() {
//        // Setup
//        final Page inputPage = null;
//        final String[] resourceTypeTail = new String[]{};
//        final String[] pageRoots = new String[]{};
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.findComponentInPage(inputPage, resourceTypeTail, pageRoots);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetPropertyValue() {
//        // Setup
//        final ValueMap _properties = null;
//        final ValueMap _currentStyle = null;
//        final String propertyName = "propertyName";
//        final String defaultValue = "defaultValue";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getPropertyValue(_properties, _currentStyle, propertyName, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testTryParseInt() {
//        // Setup
//        final String value = "value";
//        final int defaultValue = 0;
//        final int expectedResult = 0;
//
//        // Run the test
//        final int result = CommonUtil.tryParseInt(value, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testTryParseLong() {
//        // Setup
//        final String value = "value";
//        final long defaultValue = 0L;
//        final long expectedResult = 0L;
//
//        // Run the test
//        final long result = CommonUtil.tryParseLong(value, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetComponentNodePath() {
//        // Setup
//        final Page thisPage = null;
//        final String[] nodePaths = new String[]{};
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getComponentNodePath(thisPage, nodePaths);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetComponentNode() {
//        // Setup
//        final Page thisPage = null;
//        final String[] nodePaths = new String[]{};
//        final Node expectedResult = null;
//
//        // Run the test
//        final Node result = CommonUtil.getComponentNode(thisPage, nodePaths);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetComponentNodePath1() {
//        // Setup
//        final Page thisPage = null;
//        final String nodePath = "nodePath";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getComponentNodePath(thisPage, nodePath);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetComponentNode1() {
//        // Setup
//        final Page thisPage = null;
//        final String componentPath = "componentPath";
//        final Node expectedResult = null;
//
//        // Run the test
//        final Node result = CommonUtil.getComponentNode(thisPage, componentPath);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetFirstMediaNode() throws Exception {
//        // Setup
//        final Page thisPage = null;
//        final Node expectedResult = null;
//
//        // Run the test
//        final Node result = CommonUtil.getFirstMediaNode(thisPage);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetFirstMediaNode_ThrowsRepositoryException() throws Exception {
//        // Setup
//        final Page thisPage = null;
//
//        // Run the test
//        CommonUtil.getFirstMediaNode(thisPage);
//    }
//
//    @Test
//    public void testGetProperty1() throws Exception {
//        // Setup
//        final PageManager pageManager = null;
//        final String pagePath = "pagePath";
//        final String nodePath = "nodePath";
//        final String propertyName = "propertyName";
//        final String defaultValue = "defaultValue";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getProperty(pageManager, pagePath, nodePath, propertyName, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test(expected = RepositoryException.class)
//    public void testGetProperty_ThrowsRepositoryException1() throws Exception {
//        // Setup
//        final PageManager pageManager = null;
//        final String pagePath = "pagePath";
//        final String nodePath = "nodePath";
//        final String propertyName = "propertyName";
//        final String defaultValue = "defaultValue";
//
//        // Run the test
//        CommonUtil.getProperty(pageManager, pagePath, nodePath, propertyName, defaultValue);
//    }
//
//    @Test
//    public void testLinkToPage() {
//        // Setup
//        final PageManager _pageManager = null;
//        final Resource _resource = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.linkToPage(_pageManager, _resource);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testHtmlToXmlEntities() {
//        // Setup
//        final String html = "html";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.htmlToXmlEntities(html);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testConvertAsciiToXml() {
//        // Setup
//        final String string = "string";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.convertAsciiToXml(string);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testDoDebug() throws Exception {
//        // Setup
//        final String text = "text";
//        final JspWriter out = null;
//
//        // Run the test
//        CommonUtil.doDebug(text, out);
//
//        // Verify the results
//    }
//
//    @Test(expected = IOException.class)
//    public void testDoDebug_ThrowsIOException() throws Exception {
//        // Setup
//        final String text = "text";
//        final JspWriter out = null;
//
//        // Run the test
//        CommonUtil.doDebug(text, out);
//    }
//
//    @Test
//    public void testDoDebug1() throws Exception {
//        // Setup
//        final String text = "text";
//        final String code = "code";
//        final JspWriter out = null;
//
//        // Run the test
//        CommonUtil.doDebug(text, code, out);
//
//        // Verify the results
//    }
//
//    @Test(expected = IOException.class)
//    public void testDoDebug_ThrowsIOException1() throws Exception {
//        // Setup
//        final String text = "text";
//        final String code = "code";
//        final JspWriter out = null;
//
//        // Run the test
//        CommonUtil.doDebug(text, code, out);
//    }
//
//    @Test
//    public void testCompileMapMessage() {
//        // Setup
//        final String template = "template";
//        final Map<String, Object> map = new HashMap<>();
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.compileMapMessage(template, map);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceIncludeAsHtml() {
//        // Setup
//        final ComponentContext componentContext = null;
//        final String path = "path";
//        final SlingHttpServletResponse response = null;
//        final SlingHttpServletRequest request = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceIncludeAsHtml(componentContext, path, response, request);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceIncludeAsHtml1() {
//        // Setup
//        final ComponentContext componentContext = null;
//        final String path = "path";
//        final SlingHttpServletResponse response = null;
//        final SlingHttpServletRequest request = null;
//        final WCMMode mode = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceIncludeAsHtml(componentContext, path, response, request, mode);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceRenderAsHtml() {
//        // Setup
//        final String path = "path";
//        final ResourceResolver resourceResolver = null;
//        final SlingScriptHelper sling = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceRenderAsHtml(path, resourceResolver, sling);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceRenderAsHtml1() {
//        // Setup
//        final Resource resource = null;
//        final ResourceResolver resourceResolver = null;
//        final SlingScriptHelper sling = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceRenderAsHtml(resource, resourceResolver, sling);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceRenderAsHtml2() {
//        // Setup
//        final String path = "path";
//        final ResourceResolver resourceResolver = null;
//        final SlingScriptHelper sling = null;
//        final String requestAttributeName = "requestAttributeName";
//        final ComponentProperties requestAttributes = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceRenderAsHtml(path, resourceResolver, sling, requestAttributeName, requestAttributes);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceRenderAsHtml3() {
//        // Setup
//        final String path = "path";
//        final ResourceResolver resourceResolver = null;
//        final SlingScriptHelper sling = null;
//        final WCMMode mode = null;
//        final String requestAttributeName = "requestAttributeName";
//        final ComponentProperties requestAttributes = null;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceRenderAsHtml(path, resourceResolver, sling, mode, requestAttributeName, requestAttributes);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testResourceRenderAsHtml4() {
//        // Setup
//        final String path = "path";
//        final ResourceResolver resourceResolver = null;
//        final SlingScriptHelper sling = null;
//        final WCMMode mode = null;
//        final String requestAttributeName = "requestAttributeName";
//        final ComponentProperties requestAttributes = null;
//        final boolean appendHTMLExtention = false;
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.resourceRenderAsHtml(path, resourceResolver, sling, mode, requestAttributeName, requestAttributes, appendHTMLExtention);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testGetBadgeFromSelectors() {
//        // Setup
//        final String selectorString = "selectorString";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getBadgeFromSelectors(selectorString);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testForceNoDecoration() {
//        // Setup
//        final ComponentContext componentContext = null;
//        final IncludeOptions includeOptions = null;
//
//        // Run the test
//        CommonUtil.forceNoDecoration(componentContext, includeOptions);
//
//        // Verify the results
//    }
//
//    @Test
//    public void testSetDecoration() {
//        // Setup
//        final ComponentContext componentContext = null;
//        final IncludeOptions includeOptions = null;
//        final String defDecoration = "defDecoration";
//
//        // Run the test
//        CommonUtil.setDecoration(componentContext, includeOptions, defDecoration);
//
//        // Verify the results
//    }
//
//    @Test
//    public void testDisableEditMode() {
//        // Setup
//        final ComponentContext componentContext = null;
//        final IncludeOptions includeOptions = null;
//        final SlingHttpServletRequest request = null;
//
//        // Run the test
//        CommonUtil.disableEditMode(componentContext, includeOptions, request);
//
//        // Verify the results
//    }
//
//    @Test
//    public void testEnableEditMode() {
//        // Setup
//        final WCMMode toWCMMode = null;
//        final ComponentContext componentContext = null;
//        final String defDecoration = "defDecoration";
//        final IncludeOptions includeOptions = null;
//        final SlingHttpServletRequest request = null;
//
//        // Run the test
//        CommonUtil.enableEditMode(toWCMMode, componentContext, defDecoration, includeOptions, request);
//
//        // Verify the results
//    }
//
//    @Test
//    public void testGetPageLastPublished() {
//        // Setup
//        final Page page = null;
//        final Date defaultValue = new GregorianCalendar(2017, 1, 1, 0, 0, 0).getTime();
//        final Date expectedResult = new GregorianCalendar(2017, 1, 1, 0, 0, 0).getTime();
//
//        // Run the test
//        final Date result = CommonUtil.getPageLastPublished(page, defaultValue);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testIsOn() {
//        // Setup
//        final String source = "source";
//
//        // Run the test
//        final boolean result = CommonUtil.isOn(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testIsYes() {
//        // Setup
//        final String source = "source";
//
//        // Run the test
//        final boolean result = CommonUtil.isYes(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testIsNotOn() {
//        // Setup
//        final String source = "source";
//
//        // Run the test
//        final boolean result = CommonUtil.isNotOn(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testIsNotYes() {
//        // Setup
//        final String source = "source";
//
//        // Run the test
//        final boolean result = CommonUtil.isNotYes(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testGetValue() {
//        // Setup
//        final ValueMap source = null;
//        final String Name = "Name";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getValue(source, Name);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testIsNull() {
//        // Setup
//        final Object source = null;
//
//        // Run the test
//        final Boolean result = CommonUtil.isNull(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testIsNotNull() {
//        // Setup
//        final Object source = "test";
//
//        // Run the test
//        final Boolean result = CommonUtil.isNotNull(source);
//
//        // Verify the results
//        assertTrue(result);
//    }
//
//    @Test
//    public void testGetUrlContent() {
//        // Setup
//        final String Url = "Url";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.getUrlContent(Url);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
//
//    @Test
//    public void testReplaceLast() {
//        // Setup
//        final String text = "text";
//        final String regex = "regex";
//        final String replacement = "replacement";
//        final String expectedResult = "result";
//
//        // Run the test
//        final String result = CommonUtil.replaceLast(text, regex, replacement);
//
//        // Verify the results
//        assertEquals(expectedResult, result);
//    }
}
