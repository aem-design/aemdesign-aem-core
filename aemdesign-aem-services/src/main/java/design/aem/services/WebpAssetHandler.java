package design.aem.services;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.cache.BufferedImageCache;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.metadata.ExtractedMetadata;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.image.Layer;
import com.luciad.imageio.webp.WebPImageReaderSpi;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPReadParam;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Component(
    metatype = false,
    inherit = false
)
@Service({AssetHandler.class})
@References({@Reference(
    name = "imageCache",
    referenceInterface = BufferedImageCache.class,
    policy = ReferencePolicy.STATIC
)})
public class WebpAssetHandler extends StandardImageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebpAssetHandler.class);

    public static final String CONTENT_MIMETYPE = "image/webp";

    public WebpAssetHandler() {
    }

    @Activate
    private void activate() throws Exception {
        LOGGER.info("activate: WebpAssetHandler");
    }

    @Deactivate
    private void deactivate() {
        LOGGER.info("deactivate: WebpAssetHandler");
    }

    public String[] getMimeTypes() {
        return new String[]{ CONTENT_MIMETYPE };
    }

    protected void extractMetadata(Asset asset, ExtractedMetadata metadata) {
    }

    public BufferedImage getImage(Rendition rendition) {
        return this.getImage(rendition, (Dimension)null);
    }

    public BufferedImage getImage(Rendition rendition, Dimension maxDimension) {
        try {
            return this.dogetImage(rendition, maxDimension);
        } catch (Exception ex) {
            LOGGER.error("WebpAssetHandler.getImage: Cannot read image from {}: {}", rendition.getPath(), ex.getMessage());
            return null;
        }
    }

    private BufferedImage dogetImage(Rendition rendition, Dimension maxDimension) throws Exception {
        InputStream stream = null;

        LOGGER.warn("WebpAssetHandler.dogetImage {}", rendition.getPath());

        BufferedImage bufReturn = null;
        try {
            stream = rendition.getStream();

            //create webp readerspi
            WebPImageReaderSpi readerspi = new WebPImageReaderSpi();
            ImageReader reader = readerspi.createReaderInstance();

            // Configure the input on the ImageReader
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream outputStream = new MemoryCacheImageOutputStream(
                byteArrayOutputStream);
            reader.setInput(outputStream);

            // Configure decoding parameters
            WebPReadParam readParam = new WebPReadParam();
            readParam.setBypassFiltering(true);

            bufReturn = reader.read(0, readParam);

            //if matches current maxDimension
            if (maxDimension == null || (double)bufReturn.getWidth() <= maxDimension.getWidth() && (double)bufReturn.getHeight() <= maxDimension.getHeight()) {
                return bufReturn;
            }

            Layer thumbnail = new Layer(bufReturn);
            thumbnail.resize((int)maxDimension.getWidth(), (int)maxDimension.getHeight());
            bufReturn = thumbnail.getImage();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                }
            }

        }

        return bufReturn;
    }
}
