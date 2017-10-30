//package design.aem.impl.jobs;
//
//import com.day.cq.search.QueryBuilder;
//import design.aem.utils.QueryBuilderUtil;
//import design.aem.utils.UserManagementUtil;
//import design.aem.utils.WorkflowUtil;
//import org.apache.felix.scr.annotations.*;
//import org.apache.jackrabbit.api.security.user.Authorizable;
//import org.apache.sling.api.resource.ResourceResolver;
//import org.apache.sling.api.resource.ResourceResolverFactory;
//import org.apache.sling.event.jobs.Job;
//import org.apache.sling.event.jobs.consumer.JobExecutionContext;
//import org.apache.sling.event.jobs.consumer.JobExecutionResult;
//import org.apache.sling.event.jobs.consumer.JobExecutor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.jcr.RepositoryException;
//import javax.jcr.Session;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.LinkedHashSet;
//import java.util.Map;
//
//@Component
//@Service
//@Properties({
//        @Property(name="job.topics", value={"aemdesign/granite/maintenance/job/TaskOverdueTask"}, propertyPrivate=true),
//        @Property(name="granite.maintenance.name", value={"design.aem.impl.jobs.TaskOverdueTask"}, propertyPrivate=true),
//        @Property(name="granite.maintenance.title", value={"Task Due Time Notification"}, propertyPrivate=true),
//        @Property(name="granite.maintenance.schedule", value={"daily"}),
//        @Property(name="granite.maintenance.isConservative", boolValue={false})
//})
//public class TaskOverdueTask implements JobExecutor {
//
//    private static final Logger log = LoggerFactory.getLogger(TaskOverdueTask.class);
//
//    private static final String WORKFLOW_MODEL_PATH = "/etc/workflow/models/aemdesign/task-overdue-workflow/jcr:content/model";
//
//    private static final String NOTIFICATIONS_LINK = "/notifications.html";
//
//    @Reference
//    private ResourceResolverFactory resourceResolverFactory;
//
//    @Reference
//    private QueryBuilder queryBuilder;
//
//
//    @Override
//    public JobExecutionResult process(Job job, JobExecutionContext jobExecutionContext) {
//        log.info("Overdue tasks notifier triggered");
//
//        try {
//            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
//            Map<Authorizable, Collection<String>> taskAssigneeToPaths = new LinkedHashMap<>();
//            Session session = resourceResolver.adaptTo(Session.class);
//            for (String path : collectOverdueTasks(session)) {
//                Authorizable rootAssignee;
//                try {
//                    String rootAssigneeID = session.getNode(path).getProperty("assignee").getString();
//                    rootAssignee = UserManagementUtil.getAuthorizable(session, rootAssigneeID);
//                } catch (RepositoryException e) {
//                    log.error("Could not read information from task [{}]", path);
//                    continue;
//                }
//
//                try {
//                    for (Authorizable assignee : UserManagementUtil.provideAuthorizablesHavingEmail(rootAssignee)) {
//                        taskAssigneeToPaths.computeIfAbsent(assignee, s -> new LinkedHashSet<>()).add(path);
//                    }
//                } catch (RepositoryException e) {
//                    log.error("Could not get assignees e-mails of task [{}]", path);
//                }
//            }
//
//            for (Map.Entry<Authorizable, Collection<String>> entry : taskAssigneeToPaths.entrySet()) {
//                Authorizable assignee = entry.getKey();
//                Collection<String> paths = entry.getValue();
//
//                Map<String, Object> wfMetadata = new LinkedHashMap<String, Object>();
//                wfMetadata.put("paths", paths.toArray(new String[paths.size()]));
//                wfMetadata.put("username", UserManagementUtil.getDisplayName(resourceResolver, assignee.getID()));
//                wfMetadata.put("toEmail", UserManagementUtil.getNameAndEmail(resourceResolver, assignee));
//                wfMetadata.put("notificationsLink", NOTIFICATIONS_LINK);
//
//                WorkflowUtil.start(resourceResolver, WORKFLOW_MODEL_PATH, wfMetadata);
//            }
//
//            return jobExecutionContext.result().succeeded();
//        } catch (Exception e) {
//            log.error("Overdue tasks notifier error", e);
//        }
//
//        return jobExecutionContext.result().failed();
//    }
//
//    private Collection<String> collectOverdueTasks(Session session) throws RepositoryException {
//        Map<String, String> queryMap = new LinkedHashMap<String, String>();
//
//        queryMap.put("path", "/content/projects");
//        queryMap.put("type", "granite:Task");
//        queryMap.put("orderby", "path");
//
//        queryMap.put("group.0_property", "status");
//        queryMap.put("group.0_property.operation", "equals");
//        queryMap.put("group.0_property.value", "ACTIVE");
//
//        queryMap.put("group.1_property", "taskDueDate");
//        queryMap.put("group.1_property.operation", "exists");
//
//        queryMap.put("group.2_relativedaterange.property", "taskDueDate");
//        queryMap.put("group.2_relativedaterange.upperBound", "0");
//
//        return QueryBuilderUtil.queryForPaths(queryBuilder, session, queryMap);
//    }
//
//}
