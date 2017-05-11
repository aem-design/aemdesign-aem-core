package design.aem.impl.commands.handlers;

import design.aem.CommandHandler;
import design.aem.LockService;
import com.day.cq.commons.servlets.HtmlStatusResponseHelper;
import com.day.cq.wcm.api.commands.WCMCommand;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@Component
@Service
public class NodeLockCommandHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(NodeLockCommandHandler.class);

    public static final String COMMAND_LOCK_NODE = "lockNode";
    public static final String COMMAND_UNLOCK_NODE = "unlockNode";

    public static final String EXPIRE_PARAM = "expire";

    private static final long DEFAULT_LOCK_EXPIRE_IN_SECONDS = 24*60*60; //default: 1 day lock

    @Reference
    private LockService lockService;

    @Override
    public Collection<String> getSupportedCommands() {
        return Arrays.asList(COMMAND_LOCK_NODE, COMMAND_UNLOCK_NODE);
    }

    @Override
    public Object performCommand(String cmd, SlingHttpServletRequest request, SlingHttpServletResponse response) throws Exception {
        boolean lock = cmd.equals(COMMAND_LOCK_NODE);

        boolean success = false;

        String paths[] = request.getParameterValues(WCMCommand.PATH_PARAM);
        String[] msgs = new String[paths.length];

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        long lockExpireInSeconds = DEFAULT_LOCK_EXPIRE_IN_SECONDS;
        RequestParameter expireParam = request.getRequestParameter(EXPIRE_PARAM);
        if (expireParam != null) {
            try {
                lockExpireInSeconds = Long.parseLong(expireParam.getString());
            } catch (Exception ex) {
                lockExpireInSeconds = DEFAULT_LOCK_EXPIRE_IN_SECONDS;
                log.info("could parse expire param {}, {}", expireParam.getString(), ex.toString());
            }
        }

        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            try {

                Resource resource = resolver.getResource(path);
                Node node = resource.adaptTo(Node.class);
                node.addMixin(JcrConstants.MIX_LOCKABLE);
                session.save();
                if (lock) {
                    lockService.lock(resource, lockExpireInSeconds, true);
                    msgs[i] = "Locked: " + path;
                } else {
                    if (isMemeberOfGroup(resolver,"administrators") || isMemeberOfGroup(resolver,"lockadmins") || isMemeberOfGroup(resolver,"lockadmins")) {
                        lockService.unlock(resource, false, true);
                    } else {
                        lockService.unlock(resource, true, true);
                    }
                    msgs[i] = "Unlocked: " + path;
                }
                success = true;
            } catch (Exception e) {
                String errorText = ExceptionUtils.getStackTrace(e);


                if (lock) {
                    log.warn("Unable to lock: {}, {}", path, errorText);
                    msgs[i] = "Unable to lock: " + path + " " + e.getMessage();
                } else {
                    log.warn("Unable to unlock: {}, {}", path, errorText);
                    msgs[i] = "Unable to unlock: " + path + " " + e.getMessage();
                }
            }
        }

        return HtmlStatusResponseHelper.createStatusResponse(success, msgs, paths);
    }

    private boolean isMemeberOfGroup(ResourceResolver resourceResolver, String groupId) {
        final Authorizable authorizable = resourceResolver.adaptTo(Authorizable.class);
        if (authorizable != null) {

            if (authorizable instanceof User) {
                User authUser = (User) authorizable;
                if (authUser.isAdmin()) {
                    // admin has access by default
                    return true;
                }
            }

            try {
                Iterator<Group> groupIt = authorizable.memberOf();
                while (groupIt.hasNext()) {
                    Group group = groupIt.next();
                    if (groupId.equals(group.getPrincipal().getName())) {
                        return true;
                    }
                }
            } catch (Exception e) {

            }
        }
        return false;
    }

}
