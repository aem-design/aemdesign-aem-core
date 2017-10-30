
package apps.aemdesign.components.media.image;

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
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Collections;

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

    /***
     * check if image has a diff of type removed
     * @param c
     * @return
     */
    private boolean isRemovedDiff(AbstractImageServlet.ImageContext c) {
        if (c.diffInfo == null) {
            return false;
        } else {
            Resource diffContent = c.diffInfo.getContent();
            if (diffContent == null) {
                return false;
            } else {
                ImageResource img1 = this.createImageResource(diffContent);
                if (!img1.hasContent()) {
                    return false;
                } else if (c.diffInfo.getType().toString().equals("REMOVED")) {
                    return true;
                } else {
                    ImageResource img0 = this.createImageResource(c.resource);
                    return !img0.hasContent();
                }
            }
        }
    }

    private Boolean objectHasProperty(Object obj, String propertyName){
        List<Field> properties = getAllFields(obj);
        for(Field field : properties){
            if(field.getName().equalsIgnoreCase(propertyName)){
                return true;
            }
        }
        return false;
    }

    private List<Field> getAllFields(Object obj){
        List<Field> fields = new ArrayList<Field>();
        getAllFieldsRecursive(fields, obj.getClass());
        return fields;
    }

    private List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {

        Collections.addAll(fields, type.getDeclaredFields());

        if (type.getSuperclass() != null) {
            fields = getAllFieldsRecursive(fields, type.getSuperclass());
        }

        return fields;
    }


    @Override
    protected void writeLayer(SlingHttpServletRequest req,
                              SlingHttpServletResponse resp,
                              ImageContext c, Layer layer)
            throws IOException, RepositoryException {


        Image image = new Image(c.resource);
        if (!image.hasContent()) {

            Resource defaultResource = null;
            try {
                if (objectHasProperty(c,"defaultResource")) {
                        Object defaultResourceValue = c.getClass().getDeclaredField("defaultResource").get(c);
                        if (defaultResourceValue != null) {
                            defaultResource = (Resource) defaultResourceValue;
                        }
            }
            } catch (Exception ex) {
            }

            if (defaultResource != null) {
                if (isRemovedDiff(c)) {
                    image = new Image(c.diffInfo.getContent());
                } else {
                    image = new Image(defaultResource);
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