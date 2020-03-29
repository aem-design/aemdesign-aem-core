package design.aem.services;

import org.apache.felix.scr.annotations.Component;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Reference;

import java.util.Set;

@Component
public class ServiceAccessor {
    @Reference
    private SlingSettingsService slingSettingsService;

    public ServiceAccessor(SlingSettingsService slingSettingsService) {
        this.slingSettingsService = slingSettingsService;
    }

    public Set<String> getRunModes() {
        return this.slingSettingsService.getRunModes();
    }
}
