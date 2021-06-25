package design.aem.workflow.process;

import com.adobe.acs.commons.fam.ThrottledTaskRunner;
import com.adobe.acs.commons.util.visitors.ContentVisitor;
import com.adobe.acs.commons.util.visitors.ResourceRunnable;
import com.adobe.acs.commons.workflow.WorkflowPackageManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.luciad.imageio.webp.WebPWriteParam;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.*;
import org.osgi.service.component.annotations.Component;
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
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.jcr.Session;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.InputStream;

import static design.aem.utils.components.CommonUtil.tryParseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * This workflow steps creates Webp thumbnails.
 * Default parameter values are defined in the dialog help.
 */
@Component(
    immediate = true,
    service = WorkflowProcess.class,
    property = { "process.label=Workflow step for generating webp images" }
)
@Service
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
            description = "Which mimetype to ski["
        )
        String[] skip_mimetypes() default {};

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
    protected static final String ARG_QUALITY = "quality";
    protected static final String ARG_EMULATE_JPEG = "emulatejpeg";
    protected static final String ARG_RENDITION_PREFIX = "rendition_prefix";
    protected static final String ARG_RENDITION_MIMETYPE = "rendition_mimetype";
    protected static final String ARG_RENDITION_EXTENSION = "rendition_extension";
    public static final int IMAGE_JPEG = 0;
    public static final int IMAGE_PNG = 1;
    public static final int IMAGE_GIF = 2;

    @Reference
    private WorkflowPackageManager workflowPackageManager;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private ThrottledTaskRunner throttledTaskRunner;

    @Activate
    @Modified
    protected void activate(Config config) {
        LOGGER.warn("activate: WebpImageGenerator, config: {}", config);

        ImageIO.scanForPlugins();
        String[] imageWrites = ImageIO.getWriterFileSuffixes();

        LOGGER.warn("activate: WebpImageGenerator, registered image writers: {}",
            StringUtils.join(imageWrites, ","));

        this.config = config;
    }

    @Deactivate
    @Modified
    protected void deactivate(Config config) {
        LOGGER.warn("activate: WebpAssetHandler, config: {}", config);
    }

    @Override
    public final void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        final long start = System.currentTimeMillis();

        try {
            final Session session = workflowSession.adaptTo(Session.class);
            @SuppressWarnings("DuplicatedCode") final ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            @SuppressWarnings("DuplicatedCode") final String originalPayload = (String) workItem.getWorkflowData().getPayload();
            final List<String> payloads = workflowPackageManager.getPaths(resourceResolver, originalPayload);
            final ProcessArgs processArgs = new ProcessArgs(metaDataMap, config);
            final AtomicInteger count = new AtomicInteger(0);
            final AtomicInteger failCount = new AtomicInteger(0);

            // Anonymous inner class to facilitate counting of processed payloads
            final ResourceRunnable generatorRunnable = new ResourceRunnable() {
                @Override
                public void run(final Resource resource) throws Exception {
                    if (processArgs.isThrottle()) {
                        throttledTaskRunner.waitForLowCpuAndLowMemory();
                    }

                    try {
                        Asset asset = getAssetFromPayload(workItem, resourceResolver);
                        if (asset != null) {
                            try {

                                String scene7File = asset.getMetadataValueFromJcr(
                                    Scene7Constants.PN_S7_FILE);
                                boolean isScene7Video = DamUtil.isVideo(asset) && !isEmpty(scene7File);
                                if (isScene7Video) {
                                    LOGGER.warn("Skip to create static thumbnails/webImage for a scene7 processed video.");
                                } else if (ArrayUtils.contains(processArgs.getSkipMimeTypes(), asset.getMimeType())) {
                                    LOGGER.warn("Skip asset mimetype.");
                                } else {

                                    // create webp rendition
                                    try {

                                        Rendition originRendition = asset.getOriginal();


                                        // Obtain an image to encode from somewhere
                                        BufferedImage originalImage = ImageIO.read(originRendition.getStream());

                                        //check image type
                                        int imageType = IMAGE_PNG;
                                        if (originRendition.getMimeType().contains("jp")) {
                                            imageType = IMAGE_JPEG;
                                        } else if (originRendition.getMimeType().contains("gi")) {
                                            imageType = IMAGE_GIF;
                                        }

                                        //resize image
                                        originalImage = resizeImage(originalImage, imageType, processArgs.getWidth(), processArgs.getHeight());

                                        //create webp writter
                                        WebPImageWriterSpi writerspi = new WebPImageWriterSpi();
                                        ImageWriter writer = writerspi.createWriterInstance();

                                        // Get Asset language
                                        String assetLanguageCode = LanguageUtil.getLanguageRoot(asset.getPath());
                                        assetLanguageCode = isEmpty(assetLanguageCode) ? assetLanguageCode : "en";
                                        Locale locale = LanguageUtil.getLocale(assetLanguageCode);
                                        // Configure encoding parameters
                                        WebPWriteParam writeParam = new WebPWriteParam(locale);

                                        //set compression mode to explicit to allow setting quality and types
                                        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                                        if (processArgs.getLossless()) {
                                            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
                                        } else {
                                            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
                                            //set compression quality after setting type
                                            writeParam.setCompressionQuality((float) processArgs.getQuality() / 100);
                                            writeParam.setAlphaCompression(processArgs.getQuality());
                                        }

                                        // ensure file size smaller than jpeg
                                        writeParam.setEmulateJpegSize(processArgs.getEmulateJpeg());

                                        // Configure the output on the ImageWriter
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        ImageOutputStream outputStream = new MemoryCacheImageOutputStream(
                                            byteArrayOutputStream);
                                        writer.setOutput(outputStream);

                                        // Encode
                                        writer.write(null, new IIOImage(originalImage, null, null), writeParam);
                                        // Write bytes to byteArrayOutputStream
                                        outputStream.flush();

                                        String renditionName = MessageFormat.format("{0}{1}.{2}{3}",
                                            processArgs.getRenditionPrefix(),
                                            String.valueOf(processArgs.getWidth()),
                                            String.valueOf(processArgs.getHeight()),
                                            processArgs.getRenditionExtension());

                                        String mimeType = processArgs.rendition_mimetype;

                                        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                                        asset.addRendition(renditionName, inputStream, mimeType);

                                        LOGGER.warn("Added rendition {} with size {}", renditionName, byteArrayOutputStream.size());

                                    } catch (Exception ex) {
                                        throw new WorkflowException(ex);
                                    }

                                }


                            } catch (Exception ex) {
                                throw new WorkflowException(ex);
                            }
                        }

                    } catch (Exception e) {
                        failed(e);
                    }

                    count.incrementAndGet();

                }

                private void failed(Object... error) {
                    failCount.incrementAndGet();

                    LOGGER.error("Error occurred creating a new rendition, error: {}", error);
                }
            };

            final ContentVisitor visitor = new ContentVisitor(generatorRunnable);

            if (resourceResolver == null) {
                throw new Error("Unable to execute workflow due to ResourceResolver been 'null'.");
            }

            for (final String payload : payloads) {
                final Resource resource = resourceResolver.getResource(payload);

                if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
                    visitor.accept(resource);
                } else {
                    LOGGER.warn("Asset does not exist: {}", payload);
                }
            }

            if (failCount.get() == 0) {
                if (session != null) {
                    session.save();
                } else {
                    LOGGER.info("Unable to save workflow session state as Session is 'null'.");
                }
            } else {
                LOGGER.warn("There were failures total of {} not saving", failCount.get());
            }

            LOGGER.warn("Asset renditions generated [{}] in {} ms", count.get(), System.currentTimeMillis() - start);
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
        private final boolean emulate_jpeg;
        private final String rendition_prefix;
        private final String rendition_extension;
        private final String rendition_mimetype;
        private final int width;
        private final int height;
        private final int quality;
        private final String[] skipMimeTypes;

        ProcessArgs(MetaDataMap map, Config config) {
            if (map.get(ARG_LOSSLESS, Boolean.class) == null) {
                LOGGER.warn("Lossless not specified defaulting to disabled.");
            }
            lossless = Boolean.parseBoolean(map.get(ARG_LOSSLESS, String.class));

            if (map.get(ARG_WIDTH, String.class) == null) {
                LOGGER.warn("Width not specified, using default:" + 512);
            }
            width = tryParseInt(map.get(ARG_WIDTH, String.class), 512);

            if (map.get(ARG_HEIGHT, String.class) == null) {
                LOGGER.warn("Height not specified, using default:" + 512);
            }
            height = tryParseInt(map.get(ARG_HEIGHT, String.class), 512);

            if (map.get(ARG_QUALITY, String.class) == null) {
                LOGGER.warn("Quality not specified, using default:" + 60);
            }
            quality = tryParseInt(map.get(ARG_QUALITY, String.class), 60);

            if (map.get(ARG_EMULATE_JPEG, Boolean.class) == null) {
                LOGGER.warn("Emulate Jpeg not specified defaulting to disabled.");
            }
            emulate_jpeg = Boolean.parseBoolean(map.get(ARG_EMULATE_JPEG, String.class));

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

        public Boolean getLossless() {
            return lossless;
        }

        public String getRenditionPrefix() {
            return rendition_prefix;
        }

        public String getRenditionExtension() {
            return rendition_extension;
        }

        public String getRenditionMimetype() {
            return rendition_mimetype;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getQuality() {
            return quality;
        }

        public boolean getEmulateJpeg() {
            return emulate_jpeg;
        }

        public String[] getSkipMimeTypes() {
            return skipMimeTypes;
        }

        public boolean isThrottle() {
            return throttle;
        }


    }


    private Asset getAssetFromPayload(WorkItem item, ResourceResolver resourceResolver) {
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
     * Resizes an image.
     *
     * @param image     The image to resize
     * @param maxWidth  The image's max width
     * @param maxHeight The image's max height
     * @param type      int
     * @return A resized <code>BufferedImage</code>
     */
    public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight) {
        Dimension largestDimension = new Dimension(maxWidth, maxHeight);

        // Original size
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        float aspectRatio = (float) imageWidth / imageHeight;

        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            if ((float) largestDimension.width / largestDimension.height > aspectRatio) {
                largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRatio);
            } else {
                largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRatio);
            }

            imageWidth = largestDimension.width;
            imageHeight = largestDimension.height;
        }

        return createHeadlessSmoothBufferedImage(image, type, imageWidth, imageHeight);
    }


    /**
     * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can
     * function on a completely headless system. This especially includes Linux and Unix systems
     * that do not have the X11 libraries installed, which are required for the AWT subsystem to
     * operate. The resulting image will be smoothly scaled using bilinear filtering.
     *
     * @param source The image to convert
     * @param width  The desired image width
     * @param height The desired image height
     * @param type   int
     * @return The converted image
     */
    public static BufferedImage createHeadlessSmoothBufferedImage(BufferedImage source, int type, int width, int height) {
        if (type == IMAGE_PNG && hasAlpha(source)) {
            type = BufferedImage.TYPE_INT_ARGB;
        } else {
            type = BufferedImage.TYPE_INT_RGB;
        }

        BufferedImage dest = new BufferedImage(width, height, type);

        int sourcex;
        int sourcey;

        double scalex = (double) width / source.getWidth();
        double scaley = (double) height / source.getHeight();

        int x1;
        int y1;

        double xdiff;
        double ydiff;

        int rgb;
        int rgb1;
        int rgb2;

        for (int y = 0; y < height; y++) {
            sourcey = y * source.getHeight() / dest.getHeight();
            ydiff = scale(y, scaley) - sourcey;

            for (int x = 0; x < width; x++) {
                sourcex = x * source.getWidth() / dest.getWidth();
                xdiff = scale(x, scalex) - sourcex;

                x1 = Math.min(source.getWidth() - 1, sourcex + 1);
                y1 = Math.min(source.getHeight() - 1, sourcey + 1);

                rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey), xdiff);
                rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff);

                rgb = getRGBInterpolation(rgb1, rgb2, ydiff);

                dest.setRGB(x, y, rgb);
            }
        }

        return dest;
    }

    private static double scale(int point, double scale) {
        return point / scale;
    }

    private static int getRGBInterpolation(int value1, int value2, double distance) {
        int alpha1 = (value1 & 0xFF000000) >>> 24;
        int red1 = (value1 & 0x00FF0000) >> 16;
        int green1 = (value1 & 0x0000FF00) >> 8;
        int blue1 = (value1 & 0x000000FF);

        int alpha2 = (value2 & 0xFF000000) >>> 24;
        int red2 = (value2 & 0x00FF0000) >> 16;
        int green2 = (value2 & 0x0000FF00) >> 8;
        int blue2 = (value2 & 0x000000FF);

        return ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24)
            | ((int) (red1 * (1.0 - distance) + red2 * distance) << 16)
            | ((int) (green1 * (1.0 - distance) + green2 * distance) << 8)
            | (int) (blue1 * (1.0 - distance) + blue2 * distance);
    }

    /**
     * Determines if the image has transparent pixels.
     *
     * @param image The image to check for transparent pixel.s
     * @return <code>true</code> of <code>false</code>, according to the result
     */
    public static boolean hasAlpha(Image image) {
        try {
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            pg.grabPixels();

            return pg.getColorModel().hasAlpha();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return false;
        }
    }
}
