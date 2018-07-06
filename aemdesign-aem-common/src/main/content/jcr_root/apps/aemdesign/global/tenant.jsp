<%@ page session="false" %>
<%!
    // System config located
    // http://localhost:4502/system/console/configMgr/org.apache.sling.tenant.internal.TenantProviderImpl
    final List<Pattern> tenantPathPatterns = Arrays.asList(
            Pattern.compile("(aemdesign)"),
            Pattern.compile(".*/(.+)-showcase"),
            Pattern.compile(".*/(.+)-training"),
            Pattern.compile("/content/([^/]+)")
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

    protected String resolveTenantIdFromPath(String path) {
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


%>