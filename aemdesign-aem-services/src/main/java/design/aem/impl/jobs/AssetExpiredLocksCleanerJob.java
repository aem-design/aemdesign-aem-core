package design.aem.impl.jobs;

import design.aem.LockService;
import design.aem.utils.AssetUtil;
import design.aem.utils.QueryBuilderUtil;
import design.aem.utils.UserManagementUtil;
import design.aem.utils.WorkflowUtil;
import com.day.cq.search.QueryBuilder;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.xss.XSSAPI;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component(immediate = true)
@Service(value = Runnable.class)
public class AssetExpiredLocksCleanerJob extends AbstractJob {

    private static final long PERIOD_SECONDS = 30;

    private static final String WORKFLOW_PAYLOAD_TYPE = "EXPIRED_LOCK_ACTION";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private LockService lockService;

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private XSSAPI xss;

    @Override
    protected String getJobDescription() {
        return "Asset expired lock automatic unlocker";
    }

    @Override
    protected ScheduleOptions provideScheduleOptions(Scheduler scheduler) {
        return scheduler.NOW(UNLIMITED, PERIOD_SECONDS).canRunConcurrently(false);
    }

    @Override
    public void triggered() throws Exception {
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
            Session session = resourceResolver.adaptTo(Session.class);
            LockManager lockManager = session.getWorkspace().getLockManager();

            int count = 0;

            Map<String, List<String>> locksOwnersPathsMap = new LinkedHashMap<String, List<String>>();

            Collection<String> expiredLockPaths = collectExpiredLockPaths(session);

            for (String path : expiredLockPaths) {
                try {
                    Lock currentLock = lockManager.getLock(path);
                    String lockOwner = currentLock.getLockOwner();

                    List<String> pathList = locksOwnersPathsMap.computeIfAbsent(lockOwner, s -> new LinkedList<>());
                    pathList.add(path);

                    log.info("Unlocking expired lock on asset: [{}] locked by [{}]", path, lockOwner);

                    lockService.unlock(resourceResolver.getResource(path), false, false);
                } catch (Exception e) {
                    log.warn("Could not unlock [{}]", path);
                }

                count++;

                if (count%100 == 0) {
                    //save session every 100 changes to ensure that it won't crash after reaching dead count point
                    session.save();
                }
            }

            session.save();

            for (Map.Entry<String, List<String>> lockOwnerPaths : locksOwnersPathsMap.entrySet()) {
                String lockOwner = lockOwnerPaths.getKey();
                List<String> paths = lockOwnerPaths.getValue();

                String email = UserManagementUtil.getEmail(UserManagementUtil.getUser(session, lockOwner));

                Map<String, Object> wfMetadata = new LinkedHashMap<String, Object>();
                wfMetadata.put("paths", paths.toArray(new String[paths.size()]));
                wfMetadata.put("username", lockOwner);
                wfMetadata.put("toEmail", email);
                wfMetadata.put("emailBody", generateHtmlTable(resourceResolver, paths));

                WorkflowUtil.start(resourceResolver, "/etc/workflow/models/aemdesign/asset-lock-expired-workflow/jcr:content/model", wfMetadata);
            }
        } catch (Exception ex) {
            log.error("Expiring Asset Licenses Notification error", ex);
        }
    }

    private Collection<String> collectExpiredLockPaths(Session session) throws RepositoryException {
        Map<String, String> queryMap = new LinkedHashMap<String, String>();
        queryMap.put("path", "/content/dam");
        queryMap.put("type", "dam:Asset");
        queryMap.put("group.0_property", "jcr:lockOwner");
        queryMap.put("group.0_property.operation", "exists");
        queryMap.put("group.1_property", "jcr:content/lockExpire");
        queryMap.put("group.1_property.operation", "exists");
        queryMap.put("group.2_daterange.property", "jcr:content/lockExpire");
        queryMap.put("group.2_daterange.upperBound", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        queryMap.put("group.2_daterange.upperOperation", "<=");

        return QueryBuilderUtil.queryForPaths(queryBuilder, session, queryMap);
    }

    private String generateHtmlTable(ResourceResolver resourceResolver, List<String> paths) {
        String[] columns = new String[] { "Title", "Path" };

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table>").append("<thead>");
        for (String column : columns) {
            htmlBuilder.append("<th>").append(column).append("</th>"); //TODO: I18N
        }
        htmlBuilder.append("</thead>").append("<tbody>");
        for (String path : paths) {
            String assetTitle = AssetUtil.getAssetTitle(AssetUtil.getAsset(resourceResolver, path));

            String link = "${authorPath}/assetdetails.html" + path;
            htmlBuilder
                .append("<tr>")
                    .append("<td style=\"text-weight: bold\">")
                        .append("<a href=\"").append(xss.encodeForHTMLAttr(link)).append("\">")
                            .append(xss.encodeForHTML(assetTitle))
                        .append("</a>")
                    .append("</td>")
                    .append("<td style=\"text-size: 80%\">")
                        .append(xss.encodeForHTML(path))
                    .append("</td>")
                .append("</tr>");
        }
        htmlBuilder.append("</tbody>").append("</table>");

        return htmlBuilder.toString();
    }

}
