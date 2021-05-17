package design.aem.utils.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.foundation.ELEvaluator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.text.MessageFormat;

public class ResolverUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResolverUtil.class);

    public static final String DEFAULT_MAP_CONFIG_SCHEMA = "http";
    public static final String SECURE_MAP_CONFIG_SCHEMA = "https";

    /**
     * map path using resolver
     * @param resolver resolver instance
     * @param path is the path to map to an actual URL
     * @return path for a local domain
     */
    public static String mappedUrl(ResourceResolver resolver, String path) {
        if (path == null) {
            return null;
        }
        return resolver.map(path);
    }

    /**
     * This method maps an absolute path to the canonical URL for HTTP
     *
     * @param resolver resource resolver for verifying path
     * @param slingRequest current sling request
     * @param path path is the path to map to an actual URL
     * @return path for a local domain
     */
    public static String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path) {
        return mappedUrl(resolver, slingRequest, "local", path, slingRequest.isSecure());
    }

    /**
     * This method maps an absolute path to the canonical URL for HTTP
     *
     * @param resolver resource resolver for verifying path
     * @param slingRequest current sling request
     * @param domain domain to use from Externalizer config
     * @param path path is the path to map to an actual URL
     * @return path for a specific domain
     */
    public static String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String domain, String path) {
        return mappedUrl(resolver, slingRequest, domain, path, slingRequest.isSecure());
    }


    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     *
     * @param resolver resource resolver for verifying path
     * @param slingRequest current sling request
     * @param path path is the path to map to an actual URL
     * @param secure force secure to be returned
     * @return mapped url string
     */
    public static String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String path, Boolean secure) {
        return mappedUrl(resolver, slingRequest, "local", path, secure);
    }

    /**
     * This method maps an absolute path to the canonical URL in the correct domain.
     * @param resolver resolver instance
     * @param slingRequest sling request
     * @param domain domain name
     * @param path is the path to map to an actual URL
     * @param secure use https
     * @return mapped url string
     */
    public static String mappedUrl(ResourceResolver resolver, SlingHttpServletRequest slingRequest, String domain, String path, Boolean secure) {
        if (path == null || resolver == null || slingRequest == null || domain == null || secure == null) {
            LOGGER.error(MessageFormat.format("mappedUrl not enough parameters: resolver=[{0}],slingRequest=[{1}],domain=[{2}],path=[{3}],secure=[{4}]", resolver, slingRequest, domain, path, secure));
            return null;
        }
        Externalizer externalizer = resolver.adaptTo(Externalizer.class);
        if (externalizer != null) {
            if (secure) {
                return externalizer.externalLink(resolver, domain, SECURE_MAP_CONFIG_SCHEMA, resolver.map(path));
            } else {
                return externalizer.externalLink(resolver, domain, DEFAULT_MAP_CONFIG_SCHEMA, resolver.map(path));
            }
        } else {
            LOGGER.error("mappedUrl: could not get Externalizer object");
        }
        return null;
    }

    /**
     * check if provided resource exists.
     * @param resourceName resource name to look for
     * @param _resource resource to use
     * @param _resourceResolver resource resolver
     * @return flag if node is found
     */
    public static boolean checkResourceHasChildResource(String resourceName, Resource _resource, ResourceResolver _resourceResolver) {
        boolean resourceExist = false;
        String foundResourceType = ResourceUtil.findResourceSuperType(_resource);
        if (foundResourceType != null) {
            Resource foundResource = _resourceResolver.getResource(ResourceUtil.findResourceSuperType(_resource) + "/" + resourceName);
            if (foundResource != null) {
                String file_name = foundResource.getName();
                if (file_name.equals(resourceName)) {
                    resourceExist = true;
                }
            }
        }
        return resourceExist;
    }

}
