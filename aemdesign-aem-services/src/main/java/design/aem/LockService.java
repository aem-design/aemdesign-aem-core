package design.aem;

import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;

public interface LockService {
    String LOCK_EXPIRE_PROPERTY = "lockExpire";

    /**
     * <p>Locks resource when it is JCR Node.</p>
     *
     * @param resource Resource
     * @param lockExpireInSeconds lock expiration time in seconds
     * @param save should save session?
     * @throws RepositoryException jcr repository error
     */
    void lock(Resource resource, long lockExpireInSeconds, boolean save) throws RepositoryException;

    /**
     * <p>Unlocks resource when it is JCR Node.</p>
     *
     * @param resource Resource
     * @param onlyIfOwns when set to TRUE, then if current lock belongs to different owner it will throw LockException
     * @param save should save session?
     * @return TRUE if unlocked or FALSE if path was not locked
     * @throws RepositoryException jcr repository error
     */
    boolean unlock(Resource resource, boolean onlyIfOwns, boolean save) throws RepositoryException;
}
