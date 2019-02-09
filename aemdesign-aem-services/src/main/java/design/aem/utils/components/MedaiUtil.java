package design.aem.utils.components;

import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

public class MedaiUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(MedaiUtil.class);

    public static final String PLAYER_ADDRESS = "/etc/clientlibs/aemdesign/vendorlib/flash/player.swf";
    public static final String GALLERIA_THEME = "/etc/clientlibs/aemdesign/vendorlib/galleria/classic/galleria.classic.min.js";

    /**
     * Get an integer property from the JCR, or null when it's not a proper integer value
     *
     * @param properties
     * @param propertyName
     * @return
     * @throws RepositoryException
     */
    public static Integer getIntegerProperty(ValueMap properties, String propertyName) throws RepositoryException {

        String strInt = properties.get(propertyName, (String) null);
        try {
            return Integer.parseInt(strInt);
        }
        catch (NumberFormatException nfEx) {
            return null;
        }
    }

    /**
     * Get the dimensions of the video player
     *
     * @param properties are the properties to read from
     * @return a map with width and height keys that are the dimensions for the youtube video.
     */
    public static Map<String, Integer> getVideoDimensions(ValueMap properties) throws RepositoryException{

        String sizeName = properties.get("video-size", (String) null);
        Map<String, Integer> dimensionMap = new HashMap<String, Integer>();

        // no selection?
        if (sizeName == null) {
            return null;
        }

        // auto fit?
        else if (sizeName.equals("autofit")) {
            dimensionMap.put("width", null);
            dimensionMap.put("height", null);
        }

        // custom size?
        else if (sizeName.equals("custom")) {
            dimensionMap.put("width", getIntegerProperty(properties, "custom-width"));
            dimensionMap.put("height", getIntegerProperty(properties, "custom-height"));
        }

        // predefined size?
        else {
            String[] sizes = sizeName.split(",");
            dimensionMap.put("width", Integer.parseInt(sizes[0]));
            dimensionMap.put("height", Integer.parseInt(sizes[1]));
        }

        return dimensionMap;
    }

}
