package design.aem.workflow.process;

import com.adobe.acs.commons.fam.ThrottledTaskRunner;
import com.adobe.acs.commons.util.visitors.ContentVisitor;
import com.adobe.acs.commons.util.visitors.ResourceRunnable;
import com.adobe.acs.commons.workflow.WorkflowPackageManager;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.xfa.Bool;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.Iterables;
import design.aem.utils.components.CommonUtil;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static design.aem.utils.components.CommonUtil.tryParseInt;
import static design.aem.utils.components.ComponentsUtil.evaluateExpressionWithValue;
import static design.aem.utils.components.ComponentsUtil.isStringRegex;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * This workflow steps creates a page and links content fragment to that page.
 * <p>
 * A specified template is used to create a new page.
 * <p>
 * A new content fragment component then can be create in the given page content path.
 * <p>
 * OR
 * An existing components by resource type can be updated with reference to the fragment.
 * <p>
 * At minimum following arguments should be provided:
 * <p>
 *     outputLocation=path where to create new pages
 *     templatePage=path to template to use for new pages
 *     updateExistingComponent=true
 *     updateComponentResourceType=jcr:content relative path to the component to update
 *       (eg: aemdesign/components/details/generic-details)
 * <p>
 * Default parameter values are defined in the dialog help.
 */

@Component(
    immediate = true,
    service = WorkflowProcess.class,
    property = { "process.label=Content Fragment Page Generator" }
    )
@Service
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

        /* config start */

        //dialog field: ./metaData/contentFragmentAttributeName
        @AttributeDefinition(
            name = "Config - Content Fragment Attribute Name",
            description = "Attribute Name for path to content fragment, default: fragmentPath"
        )
        String content_fragment_attribute_name() default "fragmentPath";

        /* config end */

        /* segmentation start */

        //dialog field: ./metaData/segmentationUsingPaths
        @AttributeDefinition(
            name = "Segmentation - Create Content Fragment Component",
            description = "Create content component in page root, default: false",
            type = AttributeType.BOOLEAN
        )
        boolean segmentation_using_paths() default false;

        //dialog field: ./metaData/segmentationContentFragmentRootPath
        @AttributeDefinition(
            name = "Segmentation - Content Fragments Root Path",
            description = "Select root path of content fragments.",
            type = AttributeType.STRING
        )
        String segmentation_content_fragment_root_path() default "";

        //dialog field: ./metaData/segmentationTemplatePage
        @AttributeDefinition(
            name = "Segmentation - Intermediate Page Template",
            description = "Select template to use when creating intermediate pages.",
            type = AttributeType.STRING
        )
        String segmentation_template_page() default "";


        /* segmentation end */

        /* update start */

        //dialog field: ./metaData/updateExistingComponent
        @AttributeDefinition(
            name = "Update - Existing Component",
            description = "Add reference to existing component, default: true",
            type = AttributeType.BOOLEAN
        )
        boolean update_existing_component() default true;

        //dialog field: ./metaData/updateExistingRootPath
        @AttributeDefinition(
            name = "Update - Page Root Path",
            description = "Path of root nodes in page to use, default: root/article/par"
        )
        String[] update_page_root_path() default {"root/article/par"};

        //dialog field: ./metaData/updateComponentResourceType
        @AttributeDefinition(
            name = "Update - Existing Component Resource Types",
            description = "Resource type for Component to Update, default: aemdesign/components/details/generic-details"
        )
        String[] update_existing_component_resourcetype() default {"aemdesign/components/details/generic-details"};

        /* update end */

        /* create start */

        //dialog field: ./metaData/contentFragmentComponentCreate
        @AttributeDefinition(
            name = "Create - Content Fragment Component",
            description = "Create content component in page root, default: false",
            type = AttributeType.BOOLEAN
        )
        boolean content_fragment_component_create() default false;

        //dialog field: ./metaData/contentFragmentComponentResourceType
        @AttributeDefinition(
            name = "Create - Content Fragment Resource Type",
            description = "Resource type for Content Fragment component default: aemdesign/components/content/contentfragment"
        )
        String content_fragment_component_resourcetype() default "aemdesign/components/content/contentfragment";

        //dialog field: ./metaData/contentFragmentComponentPageRootPath
        @AttributeDefinition(
            name = "Create - Content Fragment Page Root Path",
            description = "Path of root node where to create Content Fragment, default: root/article/par"
        )
        String content_fragment_component_page_root_path() default "root/article/par";

        //dialog field: ./metaData/contentFragmentComponentNodeName
        @AttributeDefinition(
            name = "Create - Fragment Component Node Name",
            description = "Name of new content fragment component node, default: contentfragment"
        )
        String content_fragment_component_node_name() default "contentfragment";

        /* create end */

        /* generate start - generate par0's that represent assets in the model  */

        //dialog field: ./metaData/generateContentFragmentParagraphContent
        @AttributeDefinition(
            name = "Generate - Paragraphs for Content Fragment",
            description = "Add content fragment sub paragraphs with components, default: false"
        )
        boolean generate_content_fragment_paragraph_content() default false;

        //dialog field: ./metaData/generateContentFragmentComponentPath
        @AttributeDefinition(
            name = "Generate - Content Fragment Component Path",
            description = "Path of content fragment component where to generate paragraph entries, default: root/article/par/contentfragment"
        )
        String generate_content_fragment_component_path() default "root/article/par/contentfragment";

        //dialog field: ./metaData/generateFromField
        @AttributeDefinition(
            name = "Generate - Field to use",
            description = "Name of field that has the rich text that will be used, default: text"
        )
        String generate_from_field() default "text";

        //dialog field: ./metaData/generateFromFieldAssets
        @AttributeDefinition(
            name = "Generate Paragraph Node Index Prefix",
            description = "Name of field that has the rich text that will be used, default: text__assets"
        )
        String generate_from_field_assets() default "text__assets";

        //dialog field: ./metaData/generateFromFieldAssetsIndex
        @AttributeDefinition(
            name = "Generate - Paragraph Asset Index",
            description = "Name of field that has the rich text asset list index, default: text__assetsindex"
        )
        String generate_from_field_assets_index() default "text__assetsindex";

        //dialog field: ./metaData/generateFromFieldAssetsType
        @AttributeDefinition(
            name = "Generate - Paragraph Asset Type",
            description = "Name of field that has the rich text asset list index, default: text__assetstype"
        )
        String generate_from_field_assets_type() default "text__assetstype";

        //dialog field: ./metaData/generateParagraphNodeIndexPrefix
        @AttributeDefinition(
            name = "Generate - Paragraph Node Index Prefix",
            description = "Prefix to use when creating nodes {prefix}{0..n}, default: par"
        )
        String generate_paragraph_node_index_prefix() default "par";

        //dialog field: ./metaData/generateParagraphNodeResourceType
        @AttributeDefinition(
            name = "Generate - Paragraph Node Resource Type",
            description = "Paragraph Node to use when creating sub nodes, default: dam/cfm/components/grid"
        )
        String generate_paragraph_node_resource_type() default "dam/cfm/components/grid";

        //dialog field: ./metaData/generateParagraphSubComponentResourceType
        @AttributeDefinition(
            name = "Generate - Paragraph Sub Component Resource Type",
            description = "Add a component into paragraph with following resource type, default: aemdesign/components/media/image"
        )
        String generate_paragraph_sub_component_resource_type() default "aemdesign/components/media/image";

        //dialog field: ./metaData/generateParagraphSubComponentAttributeName
        @AttributeDefinition(
            name = "Generate - Paragraph Sub Component Attribute Name",
            description = "Set path to content into following attribute, default: fileReference"
        )
        String generate_paragraph_sub_component_attribute_name() default "fileReference";

        /* generate end */

    }

    private Config config;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentPageGenerator.class);

    protected static final String ARG_THROTTLE = "throttle";
    protected static final String ARG_TEMPLATE_PAGE = "templatePage";
    protected static final String ARG_OUTPUT_LOCATION = "outputLocation";
    protected static final String ARG_CONTENT_FRAGMENT_RESOURCE_TYPE = "contentFragmentComponentResourceType";
    protected static final String ARG_CONTENT_FRAGMENT_CREATE = "contentFragmentComponentCreate";
    protected static final String ARG_CONTENT_FRAGMENT_PAGE_ROOT_PATH = "contentFragmentComponentPageRootPath";
    protected static final String ARG_CONTENT_FRAGMENT_NODE_NAME = "contentFragmentComponentNodeName";
    protected static final String ARG_UPDATE_EXISTING_COMPONENT_UPDATE = "updateExistingComponent";
    protected static final String ARG_UPDATE_EXISTING_COMPONENT_RESOURCE_TYPE = "updateComponentResourceType";
    protected static final String ARG_UPDATE_EXISTING_ROOT_PATHS = "updateExistingRootPath";
    protected static final String ARG_CONTENT_FRAGMENT_ATTRIBUTE_NAME = "contentFragmentAttributeName";
    protected static final String ARG_GENERATE_CONTENT_FRAGMENT_PARAGRAPH_CONTENT = "generateContentFragmentParagraphContent";
    protected static final String ARG_GENERATE_CONTENT_FRAGMENT_COMPONENT_PATH = "generateContentFragmentComponentPath";
    protected static final String ARG_GENERATE_FROM_FIELD = "generateFromField";
    protected static final String ARG_GENERATE_FROM_FIELD_ASSETS = "generateFromFieldAssets";
    protected static final String ARG_GENERATE_FROM_FIELD_ASSETS_INDEX = "generateFromFieldAssetsIndex";
    protected static final String ARG_GENERATE_FROM_FIELD_ASSETS_TYPE = "generateFromFieldAssetsType"; //tag template path
    protected static final String ARG_GENERATE_PARAGRAPH_NODE_INDEX_PREFIX = "generateParagraphNodeIndexPrefix";
    protected static final String ARG_GENERATE_PARAGRAPH_NODE_RESOURCE_TYPE = "generateParagraphNodeIndexPrefix";
    protected static final String ARG_GENERATE_PARAGRAPH_SUB_COMPONENT_RESOURCE_TYPE = "generateParagraphSubComponentResourceType";
    protected static final String ARG_GENERATE_PARAGRAPH_SUB_COMPONENT_ATTRIBUTE_NAME = "generateParagraphSubComponentAttributeName";
    protected static final String ARG_SEGMENTATION_USING_PATHS = "segmentationUsingPaths";
    protected static final String ARG_SEGMENTATION_CONTENT_FRAGMENT_ROOT_PATH = "segmentationContentFragmentRootPath";
    protected static final String ARG_SEGMENTATION_TEMPLATE_PATH = "segmentationTemplatePage";

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
    protected void deactivate() {
    }

    @Override
    public final void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final long start = System.currentTimeMillis();

        try {
            @SuppressWarnings("DuplicatedCode") final Session session = workflowSession.adaptTo(Session.class);

            final ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            final String originalPayload = (String) workItem.getWorkflowData().getPayload();
            final List<String> payloads = workflowPackageManager.getPaths(resourceResolver, originalPayload);
            final ProcessArgs processArgs = new ProcessArgs(metaDataMap, config);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger failCount = new AtomicInteger(0);

            if (resourceResolver == null) {
                throw new Error("Unable to execute workflow due to ResourceResolver been 'null'.");
            }

            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager == null) {
                throw new Error("Unable to execute workflow due to PageManager been 'null'.");
            }

            // Anonymous inner class to facilitate counting of processed payloads
            final ResourceRunnable generatorRunnable = new ResourceRunnable() {
                @Override
                public void run(final Resource resource) throws Exception {
                    if (processArgs.isThrottle()) {
                        throttledTaskRunner.waitForLowCpuAndLowMemory();
                    }

                    try {
                        ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);

                        if (contentFragment == null) {
                            failed("Payload is not a Content Fragment at path: {}", resource);
                            return;
                        }

                        Page parentPage = null;
                        //segementation
                        if (processArgs.isSegmentationUsingPaths()) {
                            if (StringUtils.isNotEmpty(processArgs.getSegmentationContentFragmentRootPath()) && resource.getPath().startsWith(processArgs.getSegmentationContentFragmentRootPath())) {
                                String segmentPath = resource.getPath().replace(processArgs.getSegmentationContentFragmentRootPath(),"");
                                String[] segmentPaths = segmentPath.split("/");

                                //ensure segmentation path existing in output location
                                Resource segmentParent = null;
                                for (String path: segmentPaths) {
                                    if (isNotEmpty(path)) {

                                        //get segment parent
                                        String segmentParentPath = (segmentParent == null ? processArgs.getSegmentationContentFragmentRootPath() : segmentParent.getPath()) + "/" + path;
                                        segmentParent = resource.getResourceResolver().getResource(segmentParentPath);
                                        if (segmentParent != null && !ResourceUtil.isNonExistingResource(segmentParent)) {
                                            //get segment parent title
                                            String segmentPathResourceTitle = path;
                                            Resource segmentPathResourceContent = segmentParent.getChild(JcrConstants.JCR_CONTENT);
                                            if (segmentPathResourceContent != null && !ResourceUtil.isNonExistingResource(segmentPathResourceContent)) {
                                                segmentPathResourceTitle = segmentPathResourceContent.getValueMap().get(JcrConstants.JCR_TITLE, "");
                                            }

                                            //get segment path in output location, create if does not exist
                                            String targetSegmentPath = (parentPage == null ? processArgs.getOutputLocation() : parentPage.getPath()) + "/" + path;
                                            Resource segmentPageResource = resource.getResourceResolver().getResource(targetSegmentPath);
                                            if (segmentPageResource == null || ResourceUtil.isNonExistingResource(segmentPageResource)) {
                                                parentPage = pageManager.create(
                                                    parentPage == null ? processArgs.getOutputLocation() : parentPage.getPath(),
                                                    path,
                                                    processArgs.getSegmentationTemplatePage(),
                                                    isNotEmpty(segmentPathResourceTitle) ? segmentPathResourceTitle : path,
                                                    false
                                                );
                                            } else {
                                                parentPage = segmentPageResource.adaptTo(Page.class);
                                                warn("Segment path already exist {}.", (Object)targetSegmentPath);
                                            }
                                        } else {
                                            warn("Can't continue segmentation as segment path does not exist {}.", (Object)segmentParentPath);
                                            break;
                                        }
                                    }
                                }

                            } else {
                                warn("Can't generate segmentation as the specified root path [{}] for content fragments its does not match current resource {}.", (Object)processArgs.getSegmentationContentFragmentRootPath(), (Object)resource.getPath());
                            }
                        }

                        //create new page
                        Page newPage = pageManager.create(
                            parentPage == null ? processArgs.getOutputLocation() : parentPage.getPath(),
                            contentFragment.getName(),
                            processArgs.getTemplatePath(),
                            contentFragment.getTitle(),
                            false
                        );

                        if (newPage != null) {
                            Resource contentResource = newPage.getContentResource();

                            if (contentResource != null) {
                                Node contentNode = contentResource.adaptTo(Node.class);

                                if (contentNode != null) {

                                    if (!contentNode.hasProperty(NameConstants.NN_TEMPLATE)) {
                                        failed("Page was created but its not complete, cq:template is missing, should be pointing to {}", processArgs.getTemplatePath());
                                    }

                                    if (!contentNode.hasNodes()) {
                                        failed("Page was created but its not complete, page has no content to update");
                                    }

                                    if (processArgs.isUpdateComponent()) {

                                        String containerPath = CommonUtil.findComponentInPage(newPage, processArgs.getUpdateComponentResourceType(), processArgs.getUpdateExistingRootPaths());

                                        if (isNotEmpty(containerPath)) {

                                            Node componentNode = JcrUtils.getNodeIfExists(containerPath, session);
                                            if (componentNode != null) {
                                                componentNode.setProperty(processArgs.getContentFragmentAttributeName(), resource.getPath());
                                            } else {
                                                failed("Update - Could not get component node to update at path {}", containerPath);
                                            }

                                        } else {
                                            failed("Update - Could not find component in root paths {} with resource type {}", processArgs.getUpdateExistingRootPaths(), processArgs.getUpdateComponentResourceType());
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
                                            failed("Create - Page does not have required root node at path {}", (Object) processArgs.getContentFragmentComponentPageRootPath());
                                        }
                                    }

                                    if (processArgs.isGenerateContentFragmentParagraphContent()) {

                                        if (contentNode.hasNode(processArgs.getGenerateContentFragmentComponentPath())) {

                                            //get content fragment node
                                            Node contentFragmentNode = contentNode.getNode(processArgs.getGenerateContentFragmentComponentPath());

                                            if (contentFragmentNode != null) {

                                                //get content from model
                                                Map contentFragmentData = contentFragment.getMetaData();

                                                //get the
                                                String generateFromField = (String)contentFragmentData.get(processArgs.getGenerateFromField());
                                                if (StringUtils.isEmpty(generateFromField)) {
                                                    failed("Generate - Content fragment does not have this field {}", (Object) processArgs.getGenerateFromField());
                                                }

                                                String[] generateFromFieldAssets = (String[])contentFragmentData.get(processArgs.getGenerateFromFieldAssets());
                                                if (ArrayUtils.isEmpty(generateFromFieldAssets)) {
                                                    failed("Generate - Content fragment does not have this field {}", (Object) processArgs.getGenerateFromFieldAssets());
                                                }

                                                String[] generateFromFieldAssetsIndex = (String[])contentFragmentData.get(processArgs.getGenerateFromFieldAssetsIndex());
                                                if (ArrayUtils.isEmpty(generateFromFieldAssetsIndex)) {
                                                    failed("Generate - Content fragment does not have this field {}", (Object) processArgs.getGenerateFromFieldAssetsIndex());
                                                }

                                                String[] generateFromFieldAssetsType = (String[])contentFragmentData.get(processArgs.getGenerateFromFieldAssetsType());
                                                if (ArrayUtils.isEmpty(generateFromFieldAssetsType)) {
                                                    failed("Generate - Content fragment does not have this field {}", (Object) processArgs.getGenerateFromFieldAssetsType());
                                                }
                                                if (generateFromFieldAssets.length == generateFromFieldAssetsIndex.length && generateFromFieldAssetsIndex.length == generateFromFieldAssetsType.length) {
                                                    failed("Generate - Assets[{}], Index[{}] and Types[{}] need to be of the same size.", (Object) generateFromFieldAssets.length, (Object) generateFromFieldAssetsIndex.length, (Object) generateFromFieldAssetsType.length);
                                                }

                                                //use this no idex and type defined
                                                String defaultType = processArgs.getGenerateParagraphSubComponentResourceType();
                                                String defaultAttributeName = processArgs.getGenerateParagraphSubComponentAttributeName();

                                                JexlEngine jexl = new JexlBuilder().create();
                                                JxltEngine jxlt = jexl.createJxltEngine();
                                                Map<String,Object> values = new HashMap<>();
                                                values.put("asset",resource.getPath());
                                                JexlContext jc = new MapContext(values);

                                                int assetIndex = 0;
                                                for (String asset: generateFromFieldAssets) {
                                                    //check if does not exist then create par and sub asset
                                                    int index = 0;

                                                    //get index for asset
                                                    if (ArrayUtils.isNotEmpty(generateFromFieldAssetsIndex) && generateFromFieldAssetsIndex.length == generateFromFieldAssets.length) {
                                                        index = tryParseInt(generateFromFieldAssetsIndex[assetIndex],0);
                                                    }

                                                    Node fragmentNode = JcrUtils.getOrCreateUniqueByPath(contentFragmentNode, processArgs.getGenerateParagraphNodeIndexPrefix() + index, JcrConstants.NT_UNSTRUCTURED);

                                                    if (fragmentNode != null) {
                                                        //is a new node, then add resource type
                                                        if (!fragmentNode.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)) {
                                                            fragmentNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, processArgs.getGenerateParagraphNodeResourceType());
                                                        }

                                                        String type = defaultType;
                                                        String attributeName = defaultAttributeName;
                                                        String attributeValue = "";
                                                        String assetTypeTemplatePath = "";


                                                        String assetNodeName = type.substring(type.lastIndexOf("/"));
                                                        Node assetNode = JcrUtils.getOrCreateUniqueByPath(fragmentNode, assetNodeName, JcrConstants.NT_UNSTRUCTURED);
                                                        if (assetNode != null) {
                                                            //set type if does not exist
                                                            if (!assetNode.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)) {
                                                                assetNode.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, type);
                                                            } else {
                                                                warn("Generate - Node with name {} already exists with type {}", (Object) assetNodeName, (Object) type);
                                                            }
                                                            //set attribute if does not exist
                                                            if (!assetNode.hasProperty(attributeName)) {
                                                                assetNode.setProperty(attributeName, asset);
                                                            } else {
                                                                warn("Generate - Node already has attribute {}", (Object) attributeName);
                                                            }

                                                            //copy fields from template into resource
                                                            if (ArrayUtils.isNotEmpty(generateFromFieldAssetsType) && generateFromFieldAssetsType.length == generateFromFieldAssets.length) {
                                                                assetTypeTemplatePath = generateFromFieldAssetsType[assetIndex];
                                                                Resource assetTypeTemplateResource = resource.getResourceResolver().getResource(assetTypeTemplatePath);
                                                                if (!ResourceUtil.isNonExistingResource(assetTypeTemplateResource)) {
                                                                    Node assetTypeTemplateResourceNode = assetTypeTemplateResource.adaptTo(Node.class);
                                                                    if (assetTypeTemplateResourceNode.hasProperty("fields") && assetTypeTemplateResourceNode.hasProperty("fieldsValues")) {
                                                                        String[] fields = Arrays.stream(assetTypeTemplateResourceNode.getProperty("fields").getValues()).toArray(String[]::new);
                                                                        String[] fieldsValues = Arrays.stream(assetTypeTemplateResourceNode.getProperty("fieldsValues").getValues()).toArray(String[]::new);
                                                                        if (fields.length == fieldsValues.length) {
                                                                            for (int i = 0; i < fields.length; i++) {
                                                                                String fieldValue = fieldsValues[i];
                                                                                //if field has regex parse it
                                                                                if (isStringRegex(fieldValue) ) {
                                                                                    fieldValue = (String)evaluateExpressionWithValue(jxlt, jc, fieldValue, resource.getPath());
                                                                                }
                                                                                assetNode.setProperty(fields[i],fieldValue);
                                                                            }
                                                                        } else {
                                                                            warn("Need to have fields[{}] and fieldsValues[{}] match in length.", (Object)fields.length, (Object)fieldsValues.length);
                                                                        }

                                                                    } else {
                                                                        warn("Need to have fields and fieldsValues in template {}.", (Object)assetTypeTemplateResource.getPath());
                                                                    }

                                                                }

                                                            }


                                                        }
                                                    }
                                                    assetIndex++;
                                                }
                                            }

                                        } else {
                                            failed("Generate - Page does not have required root node at path {}", (Object) processArgs.getGenerateContentFragmentComponentPath());
                                        }
                                    }
                                }

                            } else {
                                failed("Page does not have content resource at path {}", (Object) processArgs.getUpdateExistingRootPaths());
                            }
                        } else {
                            failed("Could not create new page {}", contentFragment.getTitle());
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
                private void warn(String message, Object... error) {
                    LOGGER.error(message, error);
                }
            };

            final ContentVisitor visitor = new ContentVisitor(generatorRunnable);

            for (final String payload : payloads) {
                final Resource resource = resourceResolver.getResource(payload);

                if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

                    visitor.accept(resource);
                } else {
                    LOGGER.info("Content Fragment does not exist: {}", payload);
                }
            }

            if (failCount.get() == 0) {
                if (session != null) {
                    session.save();
                } else {
                    LOGGER.info("Unable to save workflow session state as Session is 'null'.");
                }
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
        private final boolean throttle;
        private final String templatePage;
        private final String outputLocation;
        private final String contentFragmentComponentResourceType;
        private final boolean contentFragmentComponentCreate;
        private final String contentFragmentComponentPageRootPath;
        private final boolean updateExistingComponent;
        private final String[] updateComponentResourceType;
        private final String[] updateExistingRootPaths;
        private final String contentFragmentAttributeName;
        private final String contentFragmentComponentNodeName;
        private final boolean generateContentFragmentParagraphContent;
        private final String generateContentFragmentComponentPath;
        private final String generateFromField;
        private final String generateFromFieldAssets;
        private final String generateFromFieldAssetsIndex;
        private final String generateFromFieldAssetsType;
        private final String generateParagraphNodeIndexPrefix;
        private final String generateParagraphNodeResourceType;
        private final String generateParagraphSubComponentResourceType;
        private final String generateParagraphSubComponentAttributeName;
        private final boolean segmentationUsingPaths;
        private final String segmentationContentFragmentRootPath;
        private final String segmentationTemplatePage;

        ProcessArgs(MetaDataMap map, Config config) throws WorkflowException {
            if (map.get(ARG_TEMPLATE_PAGE, String.class) == null) {
                throw new WorkflowException("Please set template Page.");
            }

            templatePage = map.get(ARG_TEMPLATE_PAGE, "");

            if (map.get(ARG_OUTPUT_LOCATION, String.class) == null) {
                throw new WorkflowException("Please set output location for generated pages.");
            }
            outputLocation = map.get(ARG_OUTPUT_LOCATION, "");

            // create config

            if (map.get(ARG_CONTENT_FRAGMENT_RESOURCE_TYPE, String.class) == null) {
                LOGGER.warn("Content Fragment Component Resource Type not specified using default: {}.", config.content_fragment_component_resourcetype());
            }
            contentFragmentComponentResourceType = map.get(ARG_CONTENT_FRAGMENT_RESOURCE_TYPE, config.content_fragment_component_resourcetype());

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

            if (map.get(ARG_UPDATE_EXISTING_COMPONENT_RESOURCE_TYPE, String[].class) == null) {
                LOGGER.warn("Page Container Component Resource Type not specified using default: {}", (Object[]) config.update_existing_component_resourcetype());
            }
            updateComponentResourceType = map.get(ARG_UPDATE_EXISTING_COMPONENT_RESOURCE_TYPE, config.update_existing_component_resourcetype());
            LOGGER.warn("updateComponentResourceType {}", (Object[]) updateComponentResourceType);

            if (map.get(ARG_UPDATE_EXISTING_ROOT_PATHS, String[].class) == null) {
                LOGGER.warn("Page Root Path not specified using default: {}", (Object[]) config.update_page_root_path());
            }
            updateExistingRootPaths = map.get(ARG_UPDATE_EXISTING_ROOT_PATHS, config.update_page_root_path());

            // generate config

            generateContentFragmentParagraphContent = (Boolean)getConfigItem(map,config,
                "Generate Content Fragment Paragraph Content",
                ARG_GENERATE_CONTENT_FRAGMENT_PARAGRAPH_CONTENT,
                config.generate_content_fragment_paragraph_content(),
                Boolean.class
            );


            generateContentFragmentComponentPath = (String)getConfigItem(map,config,
                "Generate Content Fragment Component Path",
                ARG_GENERATE_CONTENT_FRAGMENT_COMPONENT_PATH,
                config.generate_content_fragment_component_path(),
                String.class
            );

            generateFromField = (String)getConfigItem(map,config,
                "Generate From Field field",
                ARG_GENERATE_FROM_FIELD,
                config.generate_from_field(),
                String.class
            );

            generateFromFieldAssets = (String)getConfigItem(map,config,
                "Generate From Field Assets field",
                ARG_GENERATE_FROM_FIELD_ASSETS,
                config.generate_from_field_assets(),
                String.class
            );

            generateFromFieldAssetsIndex = (String)getConfigItem(map,config,
                "Generate From Field Assets Index field",
                ARG_GENERATE_FROM_FIELD_ASSETS_INDEX,
                config.generate_from_field_assets_index(),
                String.class
            );

            generateFromFieldAssetsType = (String)getConfigItem(map,config,
                "Generate From Field Assets Type field",
                ARG_GENERATE_FROM_FIELD_ASSETS_TYPE,
                config.generate_from_field_assets_type(),
                String.class
            );


            generateParagraphNodeIndexPrefix = (String)getConfigItem(map,config,
                "Generate Paragraph Node Index Prefix",
                ARG_GENERATE_PARAGRAPH_NODE_INDEX_PREFIX,
                config.generate_paragraph_node_index_prefix(),
                String.class
            );


            generateParagraphNodeResourceType = (String)getConfigItem(map,config,
                "Generate Paragraph Node Resource Type",
                ARG_GENERATE_PARAGRAPH_NODE_RESOURCE_TYPE,
                config.generate_paragraph_node_resource_type(),
                String.class
            );

            generateParagraphSubComponentResourceType = (String)getConfigItem(map,config,
                "Generate Paragraph Sub Component Resource Type",
                ARG_GENERATE_PARAGRAPH_SUB_COMPONENT_RESOURCE_TYPE,
                config.generate_paragraph_sub_component_resource_type(),
                String.class
            );

            generateParagraphSubComponentAttributeName = (String)getConfigItem(map,config,
                "Generate Paragraph Sub Component Attribute Name",
                ARG_GENERATE_PARAGRAPH_SUB_COMPONENT_ATTRIBUTE_NAME,
                config.generate_paragraph_sub_component_attribute_name(),
                String.class
                );


            //segmentation

            segmentationUsingPaths = (boolean)getConfigItem(map,config,
                "Segmentation Content Fragment Component",
                ARG_SEGMENTATION_USING_PATHS,
                config.segmentation_using_paths(),
                Boolean.class
                );

            segmentationContentFragmentRootPath = (String)getConfigItem(map,config,
                "Segmentation Content Fragments Root Path",
                ARG_SEGMENTATION_CONTENT_FRAGMENT_ROOT_PATH,
                config.segmentation_content_fragment_root_path(),
                String.class
            );

            segmentationTemplatePage = (String)getConfigItem(map,config,
                "Segmentation Intermediate Page Template",
                ARG_SEGMENTATION_TEMPLATE_PATH,
                config.segmentation_template_page(),
                String.class
            );



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

        /***
         * check if config is specified, if log a warning and return value or default.
         * @param map step attribute map
         * @param config defaults config
         * @param warningPrefix warning text
         * @param configName config name
         * @param configDefault default value
         * @param configType value type
         * @return return value or default
         */
        private Object getConfigItem(MetaDataMap map, Config config, String warningPrefix, String configName, Object configDefault, Class<?> configType) {

            if (map.get(configName, configType) == null) {
                LOGGER.warn("{} not specified using default: {}.", warningPrefix, configDefault);
            }

            return map.get(configName, configDefault);

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

        public boolean isGenerateContentFragmentParagraphContent() { return generateContentFragmentParagraphContent; }

        public String getGenerateContentFragmentComponentPath() { return generateContentFragmentComponentPath; }

        public String getGenerateFromField() { return generateFromField; }

        public String getGenerateFromFieldAssetsIndex() { return generateFromFieldAssetsIndex; }

        public String getGenerateParagraphNodeIndexPrefix() { return generateParagraphNodeIndexPrefix; }

        public String getGenerateParagraphNodeResourceType() { return generateParagraphNodeResourceType; }

        public String getGenerateParagraphSubComponentResourceType() { return generateParagraphSubComponentResourceType; }

        public String getGenerateParagraphSubComponentAttributeName() { return generateParagraphSubComponentAttributeName; }

        public String getGenerateFromFieldAssets() {
            return generateFromFieldAssets;
        }

        public String getGenerateFromFieldAssetsType() {
            return generateFromFieldAssetsType;
        }

        public boolean isSegmentationUsingPaths() {
            return segmentationUsingPaths;
        }

        public String getSegmentationContentFragmentRootPath() {
            return segmentationContentFragmentRootPath;
        }

        public String getSegmentationTemplatePage() {
            return segmentationTemplatePage;
        }
    }
}
