package design.aem.utils.htl;

import design.aem.utils.support.ComponentPathInvocationHandler;
import design.aem.utils.support.InvocationProxyFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConsumerType
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class TemplateSupport {
    private Map<String, String> componentPathProxy;

    @ScriptVariable
    private Resource resource;

    @PostConstruct
    protected void init() {
        componentPathProxy = InvocationProxyFactory.create(resource.adaptTo(ComponentPathInvocationHandler.class));
    }

    public Map<String, String> getComponentPath() {
        return componentPathProxy;
    }
}
