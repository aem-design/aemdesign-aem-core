package design.aem.impl.services;


import design.aem.services.ContentAccess;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Service
@Component(
        name = "AEM.Design - Content Access Helper",
        immediate=true)
public class ContentAccessImpl implements ContentAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAccessImpl.class);

    private static final String SERVICE_NAME = "content-services";
    private static final Map<String, Object> AUTH_INFO;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    static {
        AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, (Object) SERVICE_NAME);
    }



    @Activate
    protected void activate() {
        LOGGER.error("activate: resourceResolverFactory={}",resourceResolverFactory);
    }

    @Deactivate
    protected void deactivate() {
        LOGGER.error("deactivate: resourceResolverFactory={}",resourceResolverFactory);
    }


    @Override
    public ResourceResolver getAdminResourceResolver() {

        try {
            return resourceResolverFactory.getServiceResourceResolver(AUTH_INFO);
        }  catch (LoginException ex) {
            LOGGER.error("openAdminResourceResolver: Login Exception when getting admin resource resolver, ex={0}", ex);
        } catch (Exception ex) {
            LOGGER.error("openAdminResourceResolver: could not get elevated resource resolver, returning non elevated resource resolver. ex={0}",ex);

        }
        return null;
    }

    /**
     * Example for getting the Bundle SubService User.
     * @return the user ID
     */
    @Override
    public String getSubServiceUser() {

        // Create the Map to pass in the Service Account Identifier
        // Remember, "SERVICE_ACCOUNT_IDENTIFIER" is mapped  to the CRX User via a SEPARATE ServiceUserMapper Factory OSGi Config
        final Map<String, Object> authInfo = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                (Object) SERVICE_NAME);

        // Get the auto-closing Service resource resolver
        try (ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            if (serviceResolver != null) {
                // Do some work w your service resource resolver
                return serviceResolver.getUserID();
            } else {
                LOGGER.error("getSubServiceUser: Could not obtain a User for the Service: {}", SERVICE_NAME);
            }
        } catch (LoginException ex) {
            LOGGER.error("getSubServiceUser: Login Exception when obtaining a User for the Bundle Service: {}, ex={}", SERVICE_NAME, ex);
        }
        return "";
    }


    /**
     * Example for getting the Bundle Service User
     * @return the user ID
     */
    @Override
    public  String getBundleServiceUser() {

        // Get the auto-closing Service resource resolver
        try (ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            if (serviceResolver != null) {
                // Do some work w your service resource resolver
                return serviceResolver.getUserID();
            } else {
                LOGGER.error("getSubServiceUser: Could not obtain a User for Bundle Service");
            }
        } catch (LoginException ex) {
            LOGGER.error("getSubServiceUser: Login Exception when obtaining a User for the Bundle Service ex={0}", ex);
        }
        return "";
    }
}
