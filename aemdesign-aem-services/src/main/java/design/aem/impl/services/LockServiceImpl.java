package design.aem.impl.services;

import design.aem.LockService;
import design.aem.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import java.util.Calendar;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

@Component
@Service
public class LockServiceImpl implements LockService {

    private static final Logger log = LoggerFactory.getLogger(LockServiceImpl.class);
    @Reference
    private NotificationService notificationService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    @Override
    public void lock(Resource resource, long lockExpireInSeconds, boolean save) throws RepositoryException {
        Session session = resource.getResourceResolver().adaptTo(Session.class);
        String path = resource.getPath();

        LockManager lockMan = session.getWorkspace().getLockManager();

        //unlock previous lock if possible to make a longer lock
        unlock(resource, true, false);

        String user = session.getUserID();

        lockMan.lock(path, true, false, lockExpireInSeconds, user);

        if (lockExpireInSeconds > 0 && lockExpireInSeconds < Integer.MAX_VALUE) {
            Node node = session.getNode(path);
            if (node.hasNode(Node.JCR_CONTENT)) {
                Calendar lockExpireDate = Calendar.getInstance();
                lockExpireDate.add(Calendar.SECOND, (int)lockExpireInSeconds);
                Node jcrContentNode = node.getNode(Node.JCR_CONTENT);
                try {
                    jcrContentNode.setProperty(LOCK_EXPIRE_PROPERTY, lockExpireDate);
                } catch (ConstraintViolationException ex) {
                    log.info("could not lock asset {}, {}", path, ex.toString());
                } //could not set automatic lock expiration on this node
            }
        }

        if (save) {
            session.save();
        }

//        notificationService.notify("asset-locked-unlocked", ProjectAuthorizableType.OWNERS, resource, Collections.singletonMap("operation", "locked"));
    }

    @Override
    public boolean unlock(Resource resource, boolean onlyIfOwns, boolean save) throws RepositoryException {

        boolean unlocked = false;

        try {

//            ResourceResolver resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(null);
            //Session adminSession = resourceResolver.adaptTo(Session.class);


            Session session = resourceResolver.adaptTo(Session.class);
            String path = resource.getPath();

            LockManager lockMan = session.getWorkspace().getLockManager();

            String user = session.getUserID();

            if (lockMan.holdsLock(path)) {
                if (onlyIfOwns) {
                    Lock currentLock = lockMan.getLock(path);
                    if (ObjectUtils.notEqual(currentLock.getLockOwner(), user)) {
                        throw new NotOwnsLockException(currentLock.getLockOwner());
                    }
                }

                lockMan.unlock(path);
                unlocked = true;
            }

            Node node = session.getNode(path);

            if (node.hasNode(JCR_CONTENT)) {
                Node jcrContentNode = node.getNode(JCR_CONTENT);
                if (jcrContentNode.hasProperty(LOCK_EXPIRE_PROPERTY)) {
                    //remove lock expire date
                    jcrContentNode.setProperty(LOCK_EXPIRE_PROPERTY, (Value)null);
                }
            }

            if (save) {
                session.save();
            }

//            notificationService.notify("asset-locked-unlocked", ProjectAuthorizableType.OWNERS, resource, Collections.singletonMap("operation", "unlocked"));


        } catch (Exception ex) {

        }

        return unlocked;
    }


    public static class NotOwnsLockException extends LockException {
        private String owner;

        public NotOwnsLockException(String owner) {
            super("Resource is currently locked by " + owner);
            this.owner = owner;
        }

        public String getOwner() {
            return owner;
        }
    }
}
