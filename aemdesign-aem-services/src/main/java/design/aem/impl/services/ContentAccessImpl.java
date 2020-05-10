package design.aem.impl.services;

import design.aem.services.ContentAccess;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Service
@Component(immediate = true)
public class ContentAccessImpl implements ContentAccess {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentAccessImpl.class);

    private static final String SERVICE_NAME = "content-services";
    private static final Map<String, Object> AUTH_INFO;

    static {
        AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);
    }

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    protected void activate() {
        LOGGER.info("activate: resourceResolverFactory={}", resourceResolverFactory);
    }

    @Deactivate
    protected void deactivate() {
        LOGGER.info("deactivate: resourceResolverFactory={}", resourceResolverFactory);
    }


    @Override
    public ResourceResolver getAdminResourceResolver() {

        try {
            return resourceResolverFactory.getServiceResourceResolver(AUTH_INFO);
        } catch (LoginException ex) {
            LOGGER.error("openAdminResourceResolver: Login Exception when getting admin resource resolver, ex={0}", ex);
        } catch (Exception ex) {
            LOGGER.error("openAdminResourceResolver: could not get elevated resource resolver, returning non elevated resource resolver. ex={0}", ex);

        }
        return null;
    }

    /**
     * Example for getting the Bundle SubService User.
     *
     * @return the user ID
     */
    @Override
    public String getSubServiceUser() {

        // Create the Map to pass in the Service Account Identifier
        // Remember, "SERVICE_ACCOUNT_IDENTIFIER" is mapped  to the CRX User via a SEPARATE ServiceUserMapper Factory OSGi Config
        final Map<String, Object> authInfo = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);

        // Get the auto-closing Service resource resolver
        try (ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            // Do some work w your service resource resolver
            return serviceResolver.getUserID();
        } catch (LoginException ex) {
            LOGGER.error("getSubServiceUser: Login Exception when obtaining a User for the Bundle Service: {}, ex={}", SERVICE_NAME, ex);
        }
        return "";
    }


    /**
     * Example for getting the Bundle Service User
     *
     * @return the user ID
     */
    @Override
    public String getBundleServiceUser() {

        // Get the auto-closing Service resource resolver
        try (ResourceResolver serviceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            // Do some work w your service resource resolver
            return serviceResolver.getUserID();
        } catch (LoginException ex) {
            LOGGER.error("getBundleServiceUser: Login Exception when obtaining a User for the Bundle Service ex={0}", ex);
        }
        return "";
    }
}
