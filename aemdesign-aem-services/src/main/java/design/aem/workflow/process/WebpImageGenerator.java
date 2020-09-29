package design.aem.workflow.process;

import com.adobe.acs.commons.fam.ThrottledTaskRunner;
import com.adobe.acs.commons.util.WorkflowHelper;
import com.adobe.acs.commons.util.visitors.ContentVisitor;
import com.adobe.acs.commons.util.visitors.ResourceRunnable;
import com.adobe.acs.commons.workflow.WorkflowPackageManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.renditions.RenditionMaker;
import com.day.cq.dam.commons.util.AssetUpdate;
import com.day.cq.dam.commons.util.AssetUpdateMonitor;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.dam.core.process.CreateThumbnailProcess;
import com.day.cq.dam.core.process.CreateWebEnabledImageProcess;
import com.day.cq.dam.core.process.UpdateFolderThumbnailProcess;
import com.day.cq.wcm.api.PageManager;
import com.luciad.imageio.webp.WebPWriteParam;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.jcr.Session;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Component(
        immediate = true,
        service = WorkflowProcess.class
)
@Designate(ocd = WebpImageGenerator.Config.class)
@ServiceDescription("Workflow step for generating webp images")
@ServiceRanking(1001)
@ServiceVendor("AEM.Design")
public class WebpImageGenerator implements WorkflowProcess {

    @ObjectClassDefinition(
            name = "AEM Design - Workflow - WebP Image Generator",
            description = "Workflow step for generating WebP images for an image."
    )
    public @interface Config {

        @AttributeDefinition(
                name = "Lossless",
                description = "Encode image losslessly, default: lossless",
                type = AttributeType.BOOLEAN
        )
        boolean lossless() default true;

        @AttributeDefinition(
            name = "Skip mimetypes",
            description = "Path of root nodes in page to use"
        )
        String[] skip_mimetypes() default { };

        @AttributeDefinition(
            name = "Rendition Prefix",
            description = "String to add to prepend file name, default: cq5dam.web."
        )
        String rendition_prefix() default "cq5dam.web.";

        @AttributeDefinition(
            name = "Rendition Mimetype",
            description = "Rendition mimetype to use, default: image/webp"
        )
        String rendition_mimetype() default "image/webp";

        @AttributeDefinition(
            name = "Rendition Extension",
            description = "Rendition extension to use, default: .webp"
        )
        String rendition_extension() default ".webp";

    }

    private Config config;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebpImageGenerator.class);

    protected static final String ARG_THROTTLE = "throttle";
    protected static final String ARG_SKIP_MIMETYPES = "skipmimetypes";
    protected static final String ARG_LOSSLESS = "lossless";
    protected static final String ARG_WIDTH = "width";
    protected static final String ARG_HEIGHT = "height";
    protected static final String ARG_RENDITION_PREFIX = "rendition_prefix";
    protected static final String ARG_RENDITION_MIMETYPE = "rendition_mimetype";
    protected static final String ARG_RENDITION_EXTENSION = "rendition_extension";

    @Reference
    private AssetUpdateMonitor monitor;

    @Reference
    private RenditionMaker renditionMaker;

    @Reference
    private WorkflowHelper workflowHelper;

    @Reference
    private WorkflowPackageManager workflowPackageManager;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ThrottledTaskRunner throttledTaskRunner;

    @Reference
    private ResourceResolver resourceResolver;

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
            final ResourceResolver resourceResolver = workflowHelper.getResourceResolver(workflowSession);
            final String originalPayload = (String) workItem.getWorkflowData().getPayload();
            final List<String> payloads = workflowPackageManager.getPaths(resourceResolver, originalPayload);
            final ProcessArgs processArgs = new ProcessArgs(metaDataMap, config);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger failCount = new AtomicInteger(0);
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            final Session session = resourceResolver.adaptTo(Session.class);
            final CreateThumbnailProcess thumbnailCreator = new CreateThumbnailProcess();
            final CreateWebEnabledImageProcess webEnabledImageCreator = new CreateWebEnabledImageProcess();
            final UpdateFolderThumbnailProcess folderThumbnailUpdater = new UpdateFolderThumbnailProcess();

            // Anonymous inner class to facilitate counting of processed payloads
            final ResourceRunnable generatorRunnable = new ResourceRunnable() {
                @Override
                public void run(final Resource resource) throws Exception {
                    if (processArgs.isThrottle()) {
                        throttledTaskRunner.waitForLowCpuAndLowMemory();
                    }

                    try {
                        Asset asset = getAssetFromPayload(workItem);
                        if (asset != null) {
                            try {

                                String scene7File = asset.getMetadataValueFromJcr("dam:scene7File");
                                boolean isScene7Video = DamUtil.isVideo(asset) && !StringUtils.isEmpty(scene7File);
                                if (isScene7Video) {
                                    LOGGER.info("Skip to create static thumbnails/webImage for a scene7 processed video.");
                                } else if (ArrayUtils.contains(processArgs.getSkipMimeTypes(), asset.getMimeType())) {
                                    LOGGER.info("Skip asset mimetype.");
                                } else {

                                    // create webp rendition
                                    try {

                                        Rendition originRendition = asset.getRendition("original");
                                        // Obtain an image to encode from somewhere
                                        BufferedImage orignalImage = ImageIO.read(originRendition.getStream());

                                        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                                        // Configure encoding parameters
                                        WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());

                                        if (processArgs.getLossless()) {
                                            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                                            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
                                        }

                                        // Configure the output on the ImageWriter
                                        ByteArrayInOutStream byteArrayInOutStream = new ByteArrayInOutStream();
                                        writer.setOutput(byteArrayInOutStream);

                                        // Encode
                                        writer.write(null, new IIOImage(orignalImage, null, null), writeParam);

                                        String renditionName = MessageFormat.format("{}{}.{}{}",
                                            processArgs.getRenditionPrefix(),
                                            processArgs.getWidth(),
                                            processArgs.getHeight(),
                                            processArgs.getRenditionExtension());

                                        String mimeType = processArgs.rendition_mimetype;

                                        asset.addRendition(renditionName, byteArrayInOutStream.getInputStream(), mimeType);

                                    } catch (Exception ex) {
                                        throw new WorkflowException(ex);
                                    }

                                }


                            } catch (Exception ex) {
                                throw new WorkflowException(ex);
                            }
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
                    LOGGER.info("Asset does not exist: {}", payload);
                }
            }

            if (failCount.get() == 0) {
                session.save();
            } else {
                LOGGER.info("There were failures total of {} not saving", failCount.get());
            }

            LOGGER.error("Asset renditions generated [{}] in {} ms", count.get(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOGGER.error("Error occurred Asset renditions were not saved.");
            throw new WorkflowException(e);
        }

    }




    /**
     * ProcessArgs parsed from the Workflow metadata map.
     */
    protected static class ProcessArgs {
        private final boolean lossless;
        private final boolean throttle;
        private final String rendition_prefix;
        private final String rendition_extension;
        private final String rendition_mimetype;
        private final String width;
        private final String height;
        private final String[] skipMimeTypes;

        ProcessArgs(MetaDataMap map, Config config) throws WorkflowException {

            // basic config

            if (map.get(ARG_LOSSLESS, Boolean.class) == null) {
                LOGGER.warn("Lossless not specified defaulting to disabled.");
//                throw new WorkflowException("Please set Lossless setting.");
            }
            lossless = Boolean.parseBoolean(map.get(ARG_LOSSLESS, "true"));

            if (map.get(ARG_WIDTH, String.class) == null) {
                throw new WorkflowException("Please set width.");
            }
            width = map.get(ARG_WIDTH, "0");

            if (map.get(ARG_HEIGHT, String.class) == null) {
                throw new WorkflowException("Please set height.");
            }
            height = map.get(ARG_HEIGHT, "0");


            // config

            if (map.get(ARG_SKIP_MIMETYPES, String[].class) == null) {
                LOGGER.warn("Skip mimetypes not specified.");
            }
            skipMimeTypes = map.get(ARG_SKIP_MIMETYPES, config.skip_mimetypes());

            if (map.get(ARG_RENDITION_PREFIX, String.class) == null) {
                LOGGER.warn("Skip mimetypes not specified.");
            }
            rendition_prefix = map.get(ARG_RENDITION_PREFIX, config.rendition_prefix());

            if (map.get(ARG_RENDITION_MIMETYPE, String.class) == null) {
                LOGGER.warn("Rendition mimetype not specified, using default:" + config.rendition_mimetype());
            }
            rendition_mimetype = map.get(ARG_RENDITION_MIMETYPE, config.rendition_mimetype());

            if (map.get(ARG_RENDITION_EXTENSION, String.class) == null) {
                LOGGER.warn("Rendition mimetype not specified, using default:" + config.rendition_extension());
            }
            rendition_extension = map.get(ARG_RENDITION_EXTENSION, config.rendition_extension());

            // advanced config

            if (map.get(ARG_THROTTLE, Boolean.class) == null) {
                LOGGER.warn("Throttle not specified defaulting to throttle enabled.");
            }
            throttle = Boolean.parseBoolean(map.get(ARG_THROTTLE, "true"));

        }

        public Boolean getLossless() { return lossless; }
        public String getRenditionPrefix() { return rendition_prefix; }
        public String getRenditionExtension() { return rendition_extension; }
        public String getRenditionMimetype() { return rendition_mimetype; }
        public String getWidth() { return width; }
        public String getHeight() { return height; }
        public String[] getSkipMimeTypes() { return skipMimeTypes; }

        public boolean isThrottle() {
            return throttle;
        }


    }


    private Asset getAssetFromPayload(WorkItem item) {
        Asset asset = null;
        if (item.getWorkflowData().getPayloadType().equals("JCR_PATH")) {
            String path = item.getWorkflowData().getPayload().toString();
            Resource resource = resourceResolver.getResource(path);
            if (null != resource && !ResourceUtil.isNonExistingResource(resource)) {
                asset = DamUtil.resolveToAsset(resource);
            } else {
                LOGGER.error("getAssetFromPaylod: asset [{}] in payload of workflow [{}] does not exist.", path, item.getWorkflow().getId());
            }
        }

        return asset;
    }

    /**
     * This class extends the ByteArrayOutputStream by
     * providing a method that returns a new ByteArrayInputStream
     * which uses the internal byte array buffer. This buffer
     * is not copied, so no additional memory is used. After
     * creating the ByteArrayInputStream the instance of the
     * ByteArrayInOutStream can not be used anymore.
     * <p>
     * The ByteArrayInputStream can be retrieved using <code>getInputStream()</code>.
     * @author Nick Russler
     */
    public class ByteArrayInOutStream extends ByteArrayOutputStream {
        /**
         * Creates a new ByteArrayInOutStream. The buffer capacity is
         * initially 32 bytes, though its size increases if necessary.
         */
        public ByteArrayInOutStream() {
            super();
        }

        /**
         * Creates a new ByteArrayInOutStream, with a buffer capacity of
         * the specified size, in bytes.
         *
         * @param   size   the initial size.
         * @exception  IllegalArgumentException if size is negative.
         */
        public ByteArrayInOutStream(int size) {
            super(size);
        }

        /**
         * Creates a new ByteArrayInputStream that uses the internal byte array buffer
         * of this ByteArrayInOutStream instance as its buffer array. The initial value
         * of pos is set to zero and the initial value of count is the number of bytes
         * that can be read from the byte array. The buffer array is not copied. This
         * instance of ByteArrayInOutStream can not be used anymore after calling this
         * method.
         * @return the ByteArrayInputStream instance
         */
        public ByteArrayInputStream getInputStream() {
            // create new ByteArrayInputStream that respects the current count
            ByteArrayInputStream in = new ByteArrayInputStream(this.buf, 0, this.count);

            // set the buffer of the ByteArrayOutputStream
            // to null so it can't be altered anymore
            this.buf = null;

            return in;
        }
    }

}
