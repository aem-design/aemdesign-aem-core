package design.aem.utils.components;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentVariation;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentFragmentUtil {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentUtil.class);
    public static final String DEFAULT_CONTENTFRAGMENT_VARIATION = "master";

    /**
     * get content fragment content in an ordered map
     * @param contentFragmentPath path to content fragment
     * @param variationName variation of content fragment
     * @return ordered map of key value fields
     */
    @SuppressWarnings("squid:S3776")
    public static Map<String, Object> getComponentFragmentMap(String contentFragmentPath, String variationName, ResourceResolver resourceResolver){
        LinkedHashMap<String, Object> newFields = new LinkedHashMap<>();

        try {
            if (isNotEmpty(contentFragmentPath)) {
                Resource fragmentResource = resourceResolver.getResource(contentFragmentPath);
                if (!ResourceUtil.isNonExistingResource(fragmentResource) && fragmentResource != null) {

                    ContentFragment contentFragment = fragmentResource.adaptTo(ContentFragment.class);
                    if (contentFragment == null) {
                        LOGGER.error("Content Fragment can not be initialized because '{}' is not a content fragment.", fragmentResource.getPath());
                    } else {
                        //ensure that variation name is set
                        if (isEmpty(variationName)) {
                            variationName = DEFAULT_CONTENTFRAGMENT_VARIATION;
                        }
                        //load all available attributes and values into a map
                        for (Iterator contentElementIterator = contentFragment.getElements(); contentElementIterator.hasNext(); ) {
                            ContentElement contentElement = (ContentElement)contentElementIterator.next();
                            String name = contentElement.getName();
                            Object value = null;
                            //if variant name is specified get that value
                            if (isNotEmpty(variationName) && !DEFAULT_CONTENTFRAGMENT_VARIATION.equals(variationName)) {
                                ContentVariation variation = contentElement.getVariation(variationName);
                                if (variation == null) {
                                    LOGGER.warn("Non-existing variation '{}' of element '{}'", variationName, contentElement.getName());
                                } else {
                                    value = variation.getContent();
                                }
                            } else {
                                value = contentElement.getContent();
                            }
                            newFields.put(name, value);
                        }

                    }
                }
            } else {
                LOGGER.error("Could not process content fragment as with empty path");
            }

        } catch (Exception ex) {
            LOGGER.error("Could not process content fragment: {} with variant {}", contentFragmentPath, variationName);
        }
        return newFields;
    }

}
