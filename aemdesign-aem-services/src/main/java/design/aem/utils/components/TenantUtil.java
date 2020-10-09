package design.aem.utils.components;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.tenant.Tenant;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static design.aem.utils.components.ComponentsUtil.DEFAULT_TENANT;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class TenantUtil {
    // System config located
    // http://localhost:4502/system/console/configMgr/org.apache.sling.tenant.internal.TenantProviderImpl
    @SuppressWarnings({"squid:S4784"})
    private static final List<Pattern> tenantPathPatterns = Arrays.asList(
        Pattern.compile("(?:cq:tags/)([^/]+)"),
        Pattern.compile("(?:experience-fragments/)([^/]+)"),
        Pattern.compile("^/content/([^/]+)"),
        Pattern.compile("^/conf/([^/]+)/*")
    );

    public static String resolveTenantIdFromPath(String path) {
        if (isEmpty(path)) {
            return null;
        }

        Iterator i$ = tenantPathPatterns.iterator();

        while (i$.hasNext()) {
            Pattern pathPattern = (Pattern) i$.next();
            Matcher matcher = pathPattern.matcher(path);

            if (matcher.find() && matcher.groupCount() >= 1) {
                String tenantId = matcher.group(1);
                return tenantId;
            }
        }

        return null;
    }

    public static String resolveTenantWithTenantResolver(ResourceResolver resourceResolver, Resource resource) {
        Tenant tenant = resourceResolver.adaptTo(Tenant.class);

        if (tenant == null) {
            tenant = resource.adaptTo(Tenant.class);

            if (tenant != null) {
                return tenant.getId();
            }

            String finalTenantId = resolveTenantIdFromPath(resource.getPath());

            if (isNotEmpty(finalTenantId)) {
                return finalTenantId;
            }
        }

        return DEFAULT_TENANT;
    }
}
