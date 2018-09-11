package design.aem.impl.services;

import com.adobe.granite.workflow.WorkflowException;
import design.aem.NotificationService;
import design.aem.ProjectAuthorizableType;
import design.aem.utils.ProjectsUtil;
import design.aem.utils.UserManagementUtil;
import design.aem.utils.WorkflowUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Service
public class NotificationServiceImpl implements NotificationService {

  private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

  private static final String WORKFLOW_ROOT_PATH = "/etc/workflow/models/aemdesign";
  private static final String WORKFLOW_RELATIVE_MODEL_PATH = "jcr:content/model";

  private static final String RECIPTIENT_NAME_PROPERTY = "username";
  private static final String RECIPTIENT_EMAIL_PROPERTY = "toEmail";

  private static final String PATH_PROPERTY = "path";


  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public void notify(String notificationType, Authorizable recipient, String path, Map properties) throws Exception {
    if (recipient != null) {

      try {
        ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
        notify(resourceResolverFactory.getResourceResolver(null), notificationType, recipient, path, properties);
      } catch (Exception ex) {
        log.error("Could not notify authorizable: " + recipient.getPath(), ex);
      }
    }
  }

  @Override
  public void notify(String notificationType, String recipientId, String path, Map properties) throws Exception {
    if (recipientId != null && recipientId.isEmpty()) {

      try {
        ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
        Session session = resourceResolver.adaptTo(Session.class);
        try {
          Authorizable recipient = UserManagementUtil.getAuthorizable(session, recipientId);
          if (recipient != null) {
            notify(resourceResolver, notificationType, recipient, path, properties);
          }
        } catch (RepositoryException ex) {
          log.error("Could not fetch authorizable: {}, {}", recipientId, ex);
        }
      } catch (Exception ex) {
        log.error("Could not notify authorizable: {}, {}, {}", recipientId, path, ex);
      }
    }
  }

  @Override
  public void notify(String notificationType, ProjectAuthorizableType target, Resource resource, Map properties) throws Exception {
    Set<Authorizable> recipients;

    if (ProjectsUtil.isProject(resource)) {
      recipients = Collections.singleton(ProjectsUtil.getAuthorizable(resource, target));
    } else {
      recipients = ProjectsUtil.findRelatedActiveProjects(resource)
              .stream()
              .map(project -> ProjectsUtil.getAuthorizable(project, target))
              .collect(Collectors.toSet());
    }

    for (Authorizable recipient : recipients) {
      notify(notificationType, recipient, resource.getPath(), properties);
    }
  }

  @Override
  public void notify(String notificationType, ProjectAuthorizableType target, String path, Map properties) throws Exception {
    try {
      ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
      notify(notificationType, target, resourceResolver.getResource(path), properties);
    } catch (Exception ex) {
      log.error("Could not notify authorizable: {}, {}, {}", target.name(), path, ex);
    }
  }

  @SuppressWarnings("unchecked")
  private void notify(ResourceResolver resourceResolver, String notificationType, Authorizable recipient, String path, Map properties) {
    if (notificationType == null) {
      throw new NullPointerException("notificationType == null");
    }

    try {
      for (Authorizable emailAuthorizable : UserManagementUtil.provideAuthorizablesHavingEmail(recipient)) {
        String workflowModelPath = WORKFLOW_ROOT_PATH + "/" + notificationType + "_workflow/" + WORKFLOW_RELATIVE_MODEL_PATH;

        Map<String, Object> wfMetadata = new LinkedHashMap<String, Object>();

        if (path != null) {
          wfMetadata.put(PATH_PROPERTY, path);
        } else {
          wfMetadata.put(PATH_PROPERTY, "");
        }

        try {
          wfMetadata.put(RECIPTIENT_NAME_PROPERTY, UserManagementUtil.getDisplayName(resourceResolver, emailAuthorizable.getID()));
          wfMetadata.put(RECIPTIENT_EMAIL_PROPERTY, UserManagementUtil.getNameAndEmail(resourceResolver, emailAuthorizable));
        } catch (RepositoryException ex) {
          log.error("Could not fetch authorizable properties: {}", ex);
          return;
        }

        if (properties != null) {
          wfMetadata.putAll(properties);
        }

        try {
          if (path == null || StringUtils.isEmpty(path)) {
            WorkflowUtil.start(resourceResolver, workflowModelPath, wfMetadata);
          } else {
            WorkflowUtil.start(resourceResolver, workflowModelPath, WorkflowUtil.PayloadType.JCR_PATH, path, wfMetadata);
          }
        } catch (WorkflowException ex) {
          log.error("Notification workflow error {}, {} " , workflowModelPath, ex);
        }
      }
    } catch (RepositoryException ex) {
      log.error("Could not fetch e-mail targets {}", ex);
    }

  }
}
