package design.aem.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ContentAccess {

    ResourceResolver getAdminResourceResolver();
    String getSubServiceUser();
    String getBundleServiceUser();

}
