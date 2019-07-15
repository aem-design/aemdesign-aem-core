package design.aem.impl.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.resourceresolver.MockResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentAccessImplTest {


    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ContentAccessImplTest.class);

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @InjectMocks
    ContentAccessImpl contentAccess;

    @Mock
    MockResourceResolver adminResourceResolver;

    @Test
    public void testClass() {
        // Run the test
        ContentAccessImpl test = new ContentAccessImpl();

        // Verify the results
        assertNotNull(test);
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

        assertTrue(logsList.get(0).getMessage().startsWith("activate: resourceResolverFactory="));
        assertEquals(Level.INFO, logsList.get(0).getLevel());

        test.deactivate();

        assertTrue(logsList.get(1).getMessage().startsWith("deactivate: resourceResolverFactory="));
        assertEquals(Level.INFO, logsList.get(1).getLevel());

    }

    @Test
    public void testGetAdminResourceResolver() throws Exception {
        String SERVICE_NAME = "content-services";
        Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(adminResourceResolver);

        ResourceResolver test = contentAccess.getAdminResourceResolver();

        assertNotNull(test);

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

        assertTrue(logsList.get(0).getMessage().startsWith("openAdminResourceResolver: Login Exception when getting admin resource resolver, ex="));
        assertEquals(Level.ERROR, logsList.get(0).getLevel());


        assertNull(test);


    }

    @Test
    public void testGetAdminResourceResolverThrowingUnknownError() throws Exception {
        String SERVICE_NAME = "content-services";
        Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenThrow(new ResourceNotFoundException(""));

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

        assertTrue(logsList.get(0).getMessage().startsWith("openAdminResourceResolver: could not get elevated resource resolver, returning non elevated resource resolver. ex="));
        assertEquals(Level.ERROR, logsList.get(0).getLevel());


        assertNull(test);

    }


    @Test
    public void testGetSubServiceUser() throws Exception {
        String SERVICE_NAME = "content-services";
        Map<String, Object> AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        when(resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)).thenReturn(adminResourceResolver);
        when(adminResourceResolver.getUserID()).thenReturn("admin");

        String test = contentAccess.getSubServiceUser();

        assertEquals("admin",test);


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

        assertTrue(logsList.get(0).getMessage().startsWith("getSubServiceUser: Login Exception when obtaining a User for the Bundle Service"));
        assertEquals(Level.ERROR, logsList.get(0).getLevel());


        assertEquals("", test);



    }

    @Test
    public void testGetBundleServiceUser() throws Exception {


        when(resourceResolverFactory.getServiceResourceResolver(null)).thenReturn(adminResourceResolver);
        when(adminResourceResolver.getUserID()).thenReturn("admin");

        String test = contentAccess.getBundleServiceUser();

        assertEquals("admin",test);


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

        assertTrue(logsList.get(0).getMessage().startsWith("getBundleServiceUser: Login Exception when obtaining a User for the Bundle Service"));
        assertEquals(Level.ERROR, logsList.get(0).getLevel());


        assertEquals("", test);



    }
}