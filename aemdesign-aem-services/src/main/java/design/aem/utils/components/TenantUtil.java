package design.aem.utils.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TenantUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUtil.class);

    // System config located
    // http://localhost:4502/system/console/configMgr/org.apache.sling.tenant.internal.TenantProviderImpl
    public final static List<Pattern> tenantPathPatterns = Arrays.asList(
            Pattern.compile("(?:cq:tags/)([^/]+)"),
            Pattern.compile("(?:experience-fragments/)([^/]+)"),
            Pattern.compile("^/content/([^/]+)"),
            Pattern.compile("^/conf/([^/]+)/*")
//            Pattern.compile("/apps/aemdesign/([^/]+)/*"),
//            Pattern.compile("/content/aemdesign/([^/]+)/*"),
//            Pattern.compile("/content/dam/aemdesign/([^/]+)/*"),
//            Pattern.compile("/content/experience-fragments/aemdesign/([^/]+)/*"),
//            Pattern.compile("/conf/aemdesign/([^/-]+)/*"), //match all tenant and -default
//            Pattern.compile("/content/cq:tags/aemdesign/([^/]+)/*"),
//            Pattern.compile("/etc/clientlibs/aemdesign/([^/]+)/*"),
//            Pattern.compile("/apps/settings/wcm/design/([^/]+)/*"),
//            Pattern.compile("/etc/blueprints/aemdesign/([^/]+)/*"),
//            Pattern.compile("/etc/clientcontext/aemdesign/([^/]+)/*"),
//            Pattern.compile("/etc/segmentation/aemdesign/([^/]+)/*"),
//            Pattern.compile("/conf/global/settings/workflows/models/aemdesign/([^/]+)/*"),
//            Pattern.compile("/conf/global/settings/workflows/launcher/aemdesign/([^/]+)/*"),
//            Pattern.compile("/conf/global/settings/workflows/scripts/aemdesign/([^/]+)/*"),
//            Pattern.compile("/conf/global/settings/workflows/packages/aemdesign/([^/]+)/*"),
//            Pattern.compile("/home/users/aemdesign/([^/]+)/*"),
//            Pattern.compile("/home/groups/aemdesign/([^/]+)/*"),
//            Pattern.compile("/var/aemdesign/([^/]+)/*"),
//            Pattern.compile("/content/campaigns/aemdesign/([^/]+)/*")
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


}
