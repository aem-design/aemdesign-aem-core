package design.aem.workflow.process;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.adobe.acs.commons.fam.ThrottledTaskRunner;
import com.adobe.acs.commons.util.WorkflowHelper;
import com.adobe.acs.commons.workflow.WorkflowPackageManager;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.mac.sync.api.DAMSyncService;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.metadata.SimpleMetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.impl.services.ContentAccessImpl;
import design.aem.utils.components.CommonUtil;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.*;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static design.aem.workflow.process.ContentFragmentPageGenerator.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DamUtil.class)
@Ignore
public class ContentFragmentPageGeneratorTest {


    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Rule
    public final OsgiContext osgiContext = new OsgiContext();

//    @Rule
//    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Mock
    WorkflowHelper workflowHelper;

    @Mock
    WorkflowPackageManager workflowPackageManager;

    @Mock
    ThrottledTaskRunner throttledTaskRunner;

    @Mock
    Asset asset;

    @Mock
    WorkItem workItem;

    @Mock
    WorkflowData workflowData;

    @Mock
    WorkflowSession workflowSession;



    @InjectMocks
    ContentFragmentPageGenerator workflowProcess = new ContentFragmentPageGenerator();

//    @Mock
//    ResourceResolver resourceResolver;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    private Session session;

    List<String> paths;

    String assetPath = "/content/contentfragment";

    Logger testLogger = (Logger) LoggerFactory.getLogger(ContentFragmentPageGenerator.class);
    List<ILoggingEvent> logsList;

    @Before
    public void setUp() throws Exception {

        context.load().json(getClass().getResourceAsStream("ContentFragmentPageGeneratorTest.json"), "/content");


        PowerMockito.mockStatic(DamUtil.class);
        PowerMockito.mockStatic(ResourceUtil.class);



        paths = new ArrayList<>();
        paths.add(assetPath);

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(assetPath);


        when(workflowPackageManager.getPaths(eq(context.resourceResolver()), anyString())).thenReturn(paths);
        when(asset.getPath()).thenReturn(assetPath);
        when(DamUtil.resolveToAsset(any(Resource.class))).thenReturn(asset);

        // get Logback Logger
        testLogger.setLevel(Level.INFO);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        testLogger.addAppender(listAppender);

        logsList = listAppender.list;


    }

    @Test
    public void testExecuteActivateWithContentFragment() throws Exception {

        String[] update_existing_component_resourcetype = new String[] {"aemdesign/components/details/generic-details"};

        MetaDataMap metaDataMap = new SimpleMetaDataMap();

        metaDataMap.put(ARG_TEMPLATE_PAGE,"/content/template");
        metaDataMap.put(ARG_OUTPUT_LOCATION,"/content/page");
        metaDataMap.put(ARG_THROTTLE,"true");
//        metaDataMap.put(ARG_CONTENT_FRAGMENT_RESOURCETYPE,"aemdesign/components/content/contentfragment");
//        metaDataMap.put(ARG_CONTENT_FRAGMENT_CREATE,false);
//        metaDataMap.put(ARG_CONTENT_FRAGMENT_PAGE_ROOT_PATH,"article/par");
//        metaDataMap.put(ARG_CONTENT_FRAGMENT_NODE_NAME,"contentfragment");
//        metaDataMap.put(ARG_UPDATE_EXISTING_COMPONENT_UPDATE,true);
//        metaDataMap.put(ARG_UPDATE_EXISTING_COMPONENT_RESOURCETYPE,update_existing_component_resourcetype);
//        metaDataMap.put(ARG_UPDATE_EXISTING_ROOT_PATHS,"article/par");
//        metaDataMap.put(ARG_CONTENT_FRAGMENT_ATTRIBUTE_NAME,"fragmentPath");


        Config config = mock(Config.class);

        when(config.content_fragment_component_resourcetype()).thenReturn("aemdesign/components/content/contentfragment");
        when(config.content_fragment_component_create()).thenReturn(false);
        when(config.content_fragment_component_page_root_path()).thenReturn("article/par");
        when(config.content_fragment_component_node_name()).thenReturn("contentfragment");
        when(config.update_existing_component()).thenReturn(false);
        when(config.update_existing_component_resourcetype()).thenReturn(update_existing_component_resourcetype);
        when(config.update_page_root_path()).thenReturn(new String[] { "article/par" });
        when(config.content_fragment_attribute_name()).thenReturn("fragmentPath");

        Session session = mock(Session.class);
        PageManager pageManager = mock(PageManager.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(workflowPackageManager.getPaths(resourceResolver, assetPath))
                .thenReturn(Arrays.asList(assetPath));
        when(workflowHelper.getResourceResolver(workflowSession)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        Resource resource = mock(Resource.class);
        ValueMap resourceVM = mock(ValueMap.class);
        ContentFragment contentFragment = mock(ContentFragment.class);
        when(resourceResolver.getResource(assetPath)).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(resourceVM);
        when(resourceVM.get("jcr:primaryType", String.class)).thenReturn("dam:Asset");
        when(resourceResolver.getResource(assetPath)).thenReturn(resource);

        when(resource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getName()).thenReturn("test");
        when(contentFragment.getTitle()).thenReturn("test");


        Resource newpageR = context.resourceResolver().getResource("/content/newpage");
        Page newpage = mock(Page.class);

        when(pageManager.create(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyBoolean()
        )).thenReturn(newpage);

        when(newpage.getContentResource()).thenReturn(newpageR.getChild("jcr:content"));

        PowerMockito.mockStatic(CommonUtil.class);
        PowerMockito.when(CommonUtil.class, "findComponentInPage", Mockito.any(Page.class), Mockito.any(String [].class), Mockito.any(String [].class)).thenReturn("/content/newpage/article/par/generic_details");


        workflowProcess.activate(config);
        workflowProcess.execute(workItem, workflowSession, metaDataMap);


        assertNotNull(workflowHelper.getResourceResolver(workflowSession).getResource(assetPath));
        assert ResourceUtil.isNonExistingResource( workflowHelper.getResourceResolver(workflowSession).getResource(assetPath));
    }
}