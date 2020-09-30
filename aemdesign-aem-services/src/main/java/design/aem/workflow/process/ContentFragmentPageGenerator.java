package design.aem.workflow.process;

import com.adobe.acs.commons.fam.ThrottledTaskRunner;
import com.adobe.acs.commons.util.WorkflowHelper;
import com.adobe.acs.commons.util.visitors.ContentVisitor;
import com.adobe.acs.commons.util.visitors.ResourceRunnable;
import com.adobe.acs.commons.workflow.WorkflowPackageManager;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.PageManager;
import design.aem.utils.components.CommonUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import javax.jcr.Node;
import javax.jcr.Session;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component(
        immediate = true,
        service = WorkflowProcess.class
)
@Designate(ocd = ContentFragmentPageGenerator.Config.class)
@ServiceDescription("Workflow step for generating pages for Content Fragments")
@ServiceRanking(1001)
@ServiceVendor("AEM.Design")
public class ContentFragmentPageGenerator implements WorkflowProcess {

    @ObjectClassDefinition(
            name = "AEM Design - Workflow - Content Fragment Page Generator",
            description = "Workflow step for generating pages for Content Fragments"
    )
    public @interface Config {

        @AttributeDefinition(
                name = "Content Fragment Attribute Name",
                description = "Attribute Name for path to content fragment, default: fragmentPath"
        )
        String content_fragment_attribute_name() default "fragmentPath";

        @AttributeDefinition(
                name = "Content Fragment Component Node Name",
                description = "Name of new content fragment component node, default: contentfragment"
        )
        String content_fragment_component_node_name() default "contentfragment";


        @AttributeDefinition(
                name = "Update Existing Component",
                description = "Add reference to existing component, default: true",
                type = AttributeType.BOOLEAN
        )
        boolean update_existing_component() default true;

        @AttributeDefinition(
                name = "Update Page Root Path",
                description = "Path of root nodes in page to use, default: article/par"
        )
        String[] update_page_root_path() default { "article/par" };

        @AttributeDefinition(
                name = "Update Existing Component Resource Types",
                description = "Resource type for Component to Update, default: aemdesign/components/details/generic-details"
        )
        String[] update_existing_component_resourcetype() default {"aemdesign/components/details/generic-details"};

        @AttributeDefinition(
                name = "Create Content Fragment Component",
                description = "Create content component in page root, default: false",
                type = AttributeType.BOOLEAN
        )
        boolean content_fragment_component_create() default false;

        @AttributeDefinition(
                name = "Create Content Fragment Resource Type",
                description = "Resource type for Content Fragment component default: aemdesign/components/content/contentfragment"
        )
        String content_fragment_component_resourcetype() default "aemdesign/components/content/contentfragment";

        @AttributeDefinition(
                name = "Create Content Fragment Page Root Path",
                description = "Path of root node where to create Content Fragment, default: article/par"
        )
        String content_fragment_component_page_root_path() default  "article/par";


    }

    private Config config;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentPageGenerator.class);

    protected static final String ARG_THROTTLE = "throttle";
    protected static final String ARG_TEMPLATE_PAGE = "templatePage";
    protected static final String ARG_OUTPUT_LOCATION = "outputLocation";
    protected static final String ARG_CONTENT_FRAGMENT_RESOURCETYPE = "contentFragmentComponentResourceType";
    protected static final String ARG_CONTENT_FRAGMENT_CREATE = "contentFragmentComponentCreate";
    protected static final String ARG_CONTENT_FRAGMENT_PAGE_ROOT_PATH = "contentFragmentComponentPageRootPath";
    protected static final String ARG_CONTENT_FRAGMENT_NODE_NAME = "contentFragmentComponentNodeName";
    protected static final String ARG_UPDATE_EXISTING_COMPONENT_UPDATE = "updateExistingComponent";
    protected static final String ARG_UPDATE_EXISTING_COMPONENT_RESOURCETYPE = "updateComponentResourceType";
    protected static final String ARG_UPDATE_EXISTING_ROOT_PATHS = "updateExistingRootPath";
    protected static final String ARG_CONTENT_FRAGMENT_ATTRIBUTE_NAME = "contentFragmentAttributeName";

    @Reference
    private WorkflowPackageManager workflowPackageManager;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ThrottledTaskRunner throttledTaskRunner;

    @Activate
    @Modified
    protected void activate(Config config) {
        LOGGER.info("activate: resourceResolverFactory={}", resourceResolverFactory);
        this.config = config;
    }

    @Deactivate
    protected void deactivate(Config config) {
    }

    @Override
    public final void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final long start = System.currentTimeMillis();

        try {
            final Session session = workflowSession.adaptTo(Session.class);
            final ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            final String originalPayload = (String) workItem.getWorkflowData().getPayload();
            final List<String> payloads = workflowPackageManager.getPaths(resourceResolver, originalPayload);
            final ProcessArgs processArgs = new ProcessArgs(metaDataMap, config);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger failCount = new AtomicInteger(0);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            // Anonymous inner class to facilitate counting of processed payloads
            final ResourceRunnable generatorRunnable = new ResourceRunnable() {
                @Override
                public void run(final Resource resource) throws Exception {
                    if (processArgs.isThrottle()) {
                        throttledTaskRunner.waitForLowCpuAndLowMemory();
                    }

                    boolean autoSaveContent = false;

                    try {

                        ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);

                        if (contentFragment == null) {
                            failed("Payload is not a Content Fragment at path: {}", resource);
                            return;
                        }

                        Page newPage = pageManager.create(
                                processArgs.getOutputLocation(),
                                contentFragment.getName(),
                                processArgs.getTemplatePath(),
                                contentFragment.getTitle(),
                                autoSaveContent
                        );

                        if (newPage != null) {

                            Resource contentResource = newPage.getContentResource();

                            if (contentResource != null) {
                                Node contentNode = contentResource.adaptTo(Node.class);

                                if (contentNode != null) {

                                    if (processArgs.isUpdateComponent()) {

                                        String containerPath = CommonUtil.findComponentInPage(newPage, processArgs.getUpdateComponentResourceType(), processArgs.getUpdateExistingRootPaths());

                                        if (isNotEmpty(containerPath)) {

                                            Node componentNode = JcrUtils.getNodeIfExists(containerPath, session);
                                            if (componentNode != null) {
                                                componentNode.setProperty(processArgs.getContentFragmentAttributeName(), resource.getPath());
                                            } else {
                                                failed("Could not get component node to update at path {}", containerPath);
                                            }

                                        } else {
                                            failed("Could not find component int root paths {} with resource type {}",processArgs.getUpdateExistingRootPaths(), processArgs.getUpdateComponentResourceType());
                                        }
                                    }

                                    if (processArgs.isCreateContentFragmentComponent()) {

                                        if (contentNode.hasNode(processArgs.getContentFragmentComponentPageRootPath())) {

                                            Node parNode = contentNode.getNode(processArgs.getContentFragmentComponentPageRootPath());

                                            if (parNode != null) {

                                                Node fragmentNode = JcrUtils.getOrCreateUniqueByPath(parNode, processArgs.getContentFragmentComponentNodeName(), JcrConstants.NT_UNSTRUCTURED);
                                                fragmentNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, processArgs.getContentFragmentComponentResourceType());
                                                fragmentNode.setProperty(processArgs.getContentFragmentAttributeName(), resource.getPath());
                                            }

                                        } else {
                                            failed("Page does not have required root node at path {}", processArgs.getUpdateExistingRootPaths());
                                        }
                                    }
                                }

                            } else {
                                failed("Page does not have content resource at path {}", processArgs.getUpdateExistingRootPaths());
                            }
                        } else {
                            failed("Could not create new page {}", newPage);
                        }
                    } catch (Exception e) {
                        failed("Error occurred creating a new page, error: {}", e);
                    }

                    count.incrementAndGet();

                }

                private void failed(String message, Object... error) {
                    failCount.incrementAndGet();
                    LOGGER.error(message, error);
                }
            };

            final ContentVisitor visitor = new ContentVisitor(generatorRunnable);

            for (final String payload : payloads) {

                final Resource resource = resourceResolver.getResource(payload);
                if (!ResourceUtil.isNonExistingResource(resource)) {

                    visitor.accept(resource);
                } else {
                    LOGGER.info("Content Fragment does not exist: {}", payload);
                }
            }

            if (failCount.get() == 0) {
                session.save();
            } else {
                LOGGER.info("There were failures total of {} not saving", failCount.get());
            }

            LOGGER.error("Content Fragment Pages generated [{}] in {} ms", count.get(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("Error occurred Content Fragment Pages were not saved.");
            throw new WorkflowException(e);
        }

    }

    /**
     * ProcessArgs parsed from the Workflow metadata map.
     */
    protected static class ProcessArgs {
        private boolean throttle;
        private String templatePage;
        private String outputLocation;
        private String contentFragmentComponentResourceType;
        private boolean contentFragmentComponentCreate;
        private String contentFragmentComponentPageRootPath;
        private boolean updateExistingComponent;
        private String[] updateComponentResourceType;
        private String[] updateExistingRootPaths;
        private String contentFragmentAttributeName;
        private String contentFragmentComponentNodeName;

        ProcessArgs(MetaDataMap map, Config config) throws WorkflowException {

            // basic config

            if (map.get(ARG_TEMPLATE_PAGE, String.class) == null) {
                throw new WorkflowException("Please set template Page.");
            }
            templatePage = map.get(ARG_TEMPLATE_PAGE, "");

            if (map.get(ARG_OUTPUT_LOCATION, String.class) == null) {
                throw new WorkflowException("Please set output location for generated pages.");
            }
            outputLocation = map.get(ARG_OUTPUT_LOCATION, "");

            // create config

            if (map.get(ARG_CONTENT_FRAGMENT_RESOURCETYPE, String.class) == null) {
                LOGGER.warn("Content Fragment Component Resource Type not specified using default: {}.", config.content_fragment_component_resourcetype());
            }
            contentFragmentComponentResourceType = map.get(ARG_CONTENT_FRAGMENT_RESOURCETYPE, config.content_fragment_component_resourcetype());

            if (map.get(ARG_CONTENT_FRAGMENT_CREATE, Boolean.class) == null) {
                LOGGER.warn("Content Fragment Component Create not specified using default: {}.", config.content_fragment_component_create());
            }
            contentFragmentComponentCreate = map.get(ARG_CONTENT_FRAGMENT_CREATE, config.content_fragment_component_create());

            if (map.get(ARG_CONTENT_FRAGMENT_PAGE_ROOT_PATH, String.class) == null) {
                LOGGER.warn("Content Fragment Component Page Root Path not specified using default: {}.", config.content_fragment_component_page_root_path());
            }
            contentFragmentComponentPageRootPath = map.get(ARG_CONTENT_FRAGMENT_PAGE_ROOT_PATH, config.content_fragment_component_page_root_path());

            if (map.get(ARG_CONTENT_FRAGMENT_NODE_NAME, String.class) == null) {
                LOGGER.warn("Content Fragment Component Node Name not specified using default: {}.", config.content_fragment_component_node_name());
            }
            contentFragmentComponentNodeName = map.get(ARG_CONTENT_FRAGMENT_NODE_NAME, config.content_fragment_component_node_name());

            // update config

            if (map.get(ARG_UPDATE_EXISTING_COMPONENT_UPDATE, Boolean.class) == null) {
                LOGGER.warn("Update Existing Component not specified using default: {}.", config.update_existing_component());
            }
            updateExistingComponent = map.get(ARG_UPDATE_EXISTING_COMPONENT_UPDATE, config.update_existing_component());

            if (map.get(ARG_UPDATE_EXISTING_COMPONENT_RESOURCETYPE, String[].class) == null) {
                LOGGER.warn("Page Container Component Resource Type not specified using default: {}", config.update_existing_component_resourcetype());
            }
            updateComponentResourceType = map.get(ARG_UPDATE_EXISTING_COMPONENT_RESOURCETYPE, config.update_existing_component_resourcetype());
            LOGGER.warn("updateComponentResourceType {}", updateComponentResourceType);

            if (map.get(ARG_UPDATE_EXISTING_ROOT_PATHS, String[].class) == null) {
                LOGGER.warn("Page Root Path not specified using default: {}", config.update_page_root_path());
            }
            updateExistingRootPaths = map.get(ARG_UPDATE_EXISTING_ROOT_PATHS, config.update_page_root_path());

            // common config

            if (map.get(ARG_CONTENT_FRAGMENT_ATTRIBUTE_NAME, String.class) == null) {
                LOGGER.warn("Page Root Path not specified using default: {}", config.content_fragment_attribute_name());
            }
            contentFragmentAttributeName = map.get(ARG_CONTENT_FRAGMENT_ATTRIBUTE_NAME, config.content_fragment_attribute_name());

            // advanced config

            if (map.get(ARG_THROTTLE, Boolean.class) == null) {
                LOGGER.warn("Throttle not specified defaulting to throttle enabled.");
            }
            throttle = Boolean.parseBoolean(map.get(ARG_THROTTLE, "true"));

        }

        public String getTemplatePath() {
            return templatePage;
        }

        public String getOutputLocation() {
            return outputLocation;
        }

        public boolean isCreateContentFragmentComponent() {
            return contentFragmentComponentCreate;
        }

        public String getContentFragmentComponentResourceType() {
            return contentFragmentComponentResourceType;
        }
        public String getContentFragmentComponentPageRootPath() {
            return contentFragmentComponentPageRootPath;
        }
        public String getContentFragmentComponentNodeName() {
            return contentFragmentComponentNodeName;
        }


        public boolean isUpdateComponent() {
            return updateExistingComponent;
        }

        public String[] getUpdateComponentResourceType() {
            return updateComponentResourceType;
        }

        public String[] getUpdateExistingRootPaths() {
            return updateExistingRootPaths;
        }

        public String getContentFragmentAttributeName() {
            return contentFragmentAttributeName;
        }

        public boolean isThrottle() {
            return throttle;
        }


    }

}
