package design.aem;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.Resource;

import java.util.Map;

/**
 * Notifies end users. When recipient is a group, then it notifies all its members.
 * Handles errors and returns gracefully.
 */
public interface NotificationService {

    /**
     * <p>Notifies specific authorizable when possible.</p>
     * @param notificationType notification type, cannot be null or empty!
     * @param recipient authorizable (nullable)
     * @param path resource path (nullable)
     * @param properties notification parameters (nullable)
     */
    void notify(String notificationType, Authorizable recipient, String path, Map properties) throws Exception;

    /**
     * <p>Notifies specific authorizable when possible.</p>
     * @param notificationType notification type, cannot be null or empty!
     * @param recipientId identifier of authorizable (nullable)
     * @param path Project resource path (nullable)
     * @param properties notification parameters (nullable)
     */
    void notify(String notificationType, String recipientId, String path, Map properties) throws Exception;

    /**
     * <p>Notifies specified target of resource.</p>
     * @param notificationType notification type, cannot be null or empty!
     * @param target Project authorizable type target
     * @param resource managed resource (nullable)
     * @param properties notification parameters (nullable)
     */
    void notify(String notificationType, ProjectAuthorizableType target, Resource resource, Map properties) throws Exception;

    /**
     * <p>Notifies specified target of resource.</p>
     * @param notificationType notification type, cannot be null or empty!
     * @param target Project authorizable type target
     * @param path Project managed resource path (nullable)
     * @param properties notification parameters (nullable)
     */
    void notify(String notificationType, ProjectAuthorizableType target, String path, Map properties) throws Exception;
}
