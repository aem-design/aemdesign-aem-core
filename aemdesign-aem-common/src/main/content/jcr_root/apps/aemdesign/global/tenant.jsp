<%@ page session="false" %>
<%!
    final List<Pattern> tenantPathPatterns = Arrays.asList(
            Pattern.compile("/apps/aemdesign/([^/]+)/*"),
            Pattern.compile("/content/aemdesign/([^/]+)/*"),
            Pattern.compile("/content/dam/aemdesign/([^/]+)/*"),
            Pattern.compile("/content/experience-fragments/aemdesign/([^/]+)/*"),
            Pattern.compile("/conf/aemdesign/([^/-]+)/*"), //match all tenant and -default
            Pattern.compile("/etc/tags/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/clientlibs/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/designs/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/blueprints/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/clientcontext/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/segmentation/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/workflows/models/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/workflows/launcher/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/workflows/scripts/aemdesign/([^/]+)/*"),
            Pattern.compile("/etc/workflows/packages/aemdesign/([^/]+)/*"),
            Pattern.compile("/home/users/aemdesign/([^/]+)/*"),
            Pattern.compile("/home/groups/aemdesign/([^/]+)/*"),
            Pattern.compile("/var/aemdesign/([^/]+)/*"),
            Pattern.compile("/content/campaigns/aemdesign/([^/]+)/*")
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