
package aemdesign.components.media.image;

import com.day.cq.commons.ImageHelper;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.commons.AbstractImageServlet;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Renders an image
 */
public class img_GET extends AbstractImageServlet {

    @Override
    protected Layer createLayer(ImageContext c)
            throws RepositoryException, IOException {
        // don't create the layer yet. handle everything later
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Override default ImageResource creation to support assets
     */
    @Override
    protected ImageResource createImageResource(Resource resource) {
        return new Image(resource);
    }

    @Override
    protected void writeLayer(SlingHttpServletRequest req,
                              SlingHttpServletResponse resp,
                              ImageContext c, Layer layer)
            throws IOException, RepositoryException {

        Image image = new Image(c.resource);
        if (!image.hasContent()) {
            if (c.defaultResource != null) {
                if (isRemovedDiff(c)) {
                    image = new Image(c.diffInfo.getContent());
                } else {
                    image = new Image(c.defaultResource);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // get style and set constraints
        image.loadStyleData(c.style);

        // get pure layer
        layer = image.getLayer(false, false, false);

        boolean modified = false;
        
        if (layer != null) {
            // crop
            modified = image.crop(layer) != null;

            // rotate
            modified |= image.rotate(layer) != null;

            // resize
            modified |= image.resize(layer) != null;

            // apply diff if needed (because we create the layer inline)
            modified |= applyDiff(layer, c);
        }

        // don't cache images on authoring instances
        // Cache-Control: no-cache allows caching (e.g. in the browser cache) but
        // will force revalidation using If-Modified-Since or If-None-Match every time,
        // avoiding aggressive browser caching
        if (!WCMMode.DISABLED.equals(WCMMode.fromRequest(req))) {
            resp.setHeader("Cache-Control", "no-cache");
        }

        if (modified) {
            String mimeType = image.getMimeType();
            if (ImageHelper.getExtensionFromType(mimeType) == null) {
                // get default mime type
                mimeType = "image/png";
            }
            resp.setContentType(mimeType);
            layer.write(mimeType, mimeType.equals("image/gif") ? 255 : 1.0, resp.getOutputStream());
        } else {
            // do not re-encode layer, just spool
            Property data = image.getData();
            InputStream in = data.getStream();
            resp.setContentLength((int) data.getLength());
            resp.setContentType(image.getMimeType());
            IOUtils.copy(in, resp.getOutputStream());
            in.close();
        }
        resp.flushBuffer();
    }
}