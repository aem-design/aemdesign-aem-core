package design.aem.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ContentAccess {

    default ResourceResolver getAdminResourceResolver() {
        throw new UnsupportedOperationException();
    }

    default String getSubServiceUser() {
        throw new UnsupportedOperationException();
    }

    default String getBundleServiceUser() {
        throw new UnsupportedOperationException();
    }

}
