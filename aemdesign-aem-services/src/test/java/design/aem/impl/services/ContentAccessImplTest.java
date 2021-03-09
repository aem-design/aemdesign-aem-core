package design.aem.impl.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class ContentAccessImplTest {


    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ContentAccessImplTest.class);

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @InjectMocks
    private ContentAccessImpl contentAccess;

    @Mock
    private ResourceResolver adminResourceResolver;

    static String SERVICE_NAME = "content-services";
    static Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

    private AutoCloseable closeable;

    @BeforeEach
    void setup() throws IllegalAccessException {
        closeable = openMocks(this);

    }

    @AfterEach
    void close() throws Exception {
        closeable.close();
    }

    @Test
    public void testClass() {
        // Run the test
        ContentAccessImpl test = new ContentAccessImpl();

        // Verify the results
        Assertions.assertNotNull(test);
    }

    @Test
    public void testActivate() {

        //TEST IF FUNCTION OUTPUTS TO LOG

        // get Logback Logger
        Logger testLogger = (Logger) LoggerFactory.getLogger(ContentAccessImpl.class);
        testLogger.setLevel(Level.INFO);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        ContentAccessImpl test = new ContentAccessImpl();

        test.activate();

        List<ILoggingEvent> logsList = listAppender.list;

        Assertions.assertTrue(logsList.get(0).getMessage().startsWith("activate: resourceResolverFactory="));
        Assertions.assertEquals(Level.INFO, logsList.get(0).getLevel());

        test.deactivate();

        Assertions.assertTrue(logsList.get(1).getMessage().startsWith("deactivate: resourceResolverFactory="));
        Assertions.assertEquals(Level.INFO, logsList.get(1).getLevel());

    }

    @Test
    public void testGetAdminResourceResolver() throws Exception {
        String SERVICE_NAME = "content-services";
        Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        lenient().when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(adminResourceResolver);

        ResourceResolver test = contentAccess.getAdminResourceResolver();

        Assertions.assertNotNull(test);

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenThrow(new LoginException());

        //TEST IF FUNCTION OUTPUTS ERROR TO LOG

        // get Logback Logger
        Logger testLogger = (Logger) LoggerFactory.getLogger(ContentAccessImpl.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        test = contentAccess.getAdminResourceResolver();

        List<ILoggingEvent> logsList = listAppender.list;

        Assertions.assertTrue(logsList.get(0).getMessage().startsWith("openAdminResourceResolver: Login Exception when getting admin resource resolver, ex="));
        Assertions.assertEquals(Level.ERROR, logsList.get(0).getLevel());


        Assertions.assertNull(test);


    }

    @Test
    public void testGetAdminResourceResolverThrowingUnknownError() throws Exception {
        String SERVICE_NAME = "content-services";
        Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        lenient().when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenThrow(new ResourceNotFoundException(""));

        //TEST IF FUNCTION OUTPUTS ERROR TO LOG

        // get Logback Logger
        Logger testLogger = (Logger) LoggerFactory.getLogger(ContentAccessImpl.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        ResourceResolver test = contentAccess.getAdminResourceResolver();

        List<ILoggingEvent> logsList = listAppender.list;

        Assertions.assertTrue(logsList.get(0).getMessage().startsWith("openAdminResourceResolver: could not get elevated resource resolver, returning non elevated resource resolver. ex="));
        Assertions.assertEquals(Level.ERROR, logsList.get(0).getLevel());


        Assertions.assertNull(test);

    }

    @Test
    public void testGetSubServiceUser() throws Exception {

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(adminResourceResolver);
        when(adminResourceResolver.getUserID()).thenReturn("admin");

        String test = contentAccess.getSubServiceUser();

        Assertions.assertEquals("admin", test);

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenThrow(new LoginException());

        //TEST IF FUNCTION OUTPUTS ERROR TO LOG

        // get Logback Logger
        Logger testLogger = (Logger) LoggerFactory.getLogger(ContentAccessImpl.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        test = contentAccess.getSubServiceUser();

        List<ILoggingEvent> logsList = listAppender.list;

        Assertions.assertTrue(logsList.get(0).getMessage().startsWith("getSubServiceUser: Login Exception when obtaining a User for the Bundle Service"));
        Assertions.assertEquals(Level.ERROR, logsList.get(0).getLevel());


        Assertions.assertEquals("", test);


    }

    @Test
    public void testGetBundleServiceUser() throws Exception {


        when(resourceResolverFactory.getServiceResourceResolver(null)).thenReturn(adminResourceResolver);
        when(adminResourceResolver.getUserID()).thenReturn("admin");

        String test = contentAccess.getBundleServiceUser();

        Assertions.assertEquals("admin", test);


        when(resourceResolverFactory.getServiceResourceResolver(null)).thenThrow(new LoginException());

        // get Logback Logger
        Logger testLogger = (Logger) LoggerFactory.getLogger(ContentAccessImpl.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        test = contentAccess.getBundleServiceUser();

        List<ILoggingEvent> logsList = listAppender.list;

        Assertions.assertTrue(logsList.get(0).getMessage().startsWith("getBundleServiceUser: Login Exception when obtaining a User for the Bundle Service"));
        Assertions.assertEquals(Level.ERROR, logsList.get(0).getLevel());


        Assertions.assertEquals("", test);


    }
}
