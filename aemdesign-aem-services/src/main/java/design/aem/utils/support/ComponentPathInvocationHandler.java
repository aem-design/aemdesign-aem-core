package design.aem.utils.support;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import design.aem.utils.components.TenantUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.Session;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_TENANT;

@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class ComponentPathInvocationHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentPathInvocationHandler.class);

    @Inject
    private QueryBuilder queryBuilder;

    @Inject
    private Session session;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (ArrayUtils.isEmpty(args)) {
            throw new IllegalArgumentException("There must be a parameter for method: " + method.getName());
        }

        if (!"get".equals(method.getName())) {
            throw new UnsupportedOperationException("Unsupported method: " + method.getName());
        }

        return handle(args[0]);
    }

    private Object handle(Object componentName) {
        return generateComponentPath((String) componentName);
    }

    private String generateComponentPath(String componentName) {
        Map<String, String> predicates = new HashMap<>();

        // TODO: Implement OSGI service to allow this to be configurable
        String tenantPath = DEFAULT_TENANT;

        predicates.put("orderby", "nodename");
        predicates.put("path", "/apps/" + tenantPath + "/components");
        predicates.put("type", "q:Component");
        predicates.put("1_property", "sling:resourceSuperType");
        predicates.put("1_property.operation", "like");
        predicates.put("1_property.value", "%" + componentName);
        predicates.put("p.limit", "1");

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);

        if (query != null) {
            try {
                String componentPath = query.getResult().getHits().get(0).getPath();

                return componentPath.replace("/apps/", StringUtils.EMPTY);
            } catch (Exception ex) {
                LOGGER.error("Unexpected error occurred while trying to retrieve the component path: {}", ex.getLocalizedMessage());
            }
        }

        return null;
    }
}
