/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.utils;

import com.adobe.granite.security.user.util.AuthorizableUtil;
import com.day.cq.replication.ReplicationActionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static com.day.cq.replication.ReplicationStatus.NODE_PROPERTY_LAST_REPLICATION_ACTION;

public final class UserManagementUtil {
    private UserManagementUtil() {
    }

    public static final String PATH_PROFILE = "./profile";
    public static final String PROPERTY_PROFILE_EMAIL = PATH_PROFILE + "/email";
    public static final String PROPERTY_PROFILE_GROUP_NAME = PATH_PROFILE + "/givenName";
    public static final String PROPERTY_PROFILE_USER_GIVEN_NAME = PATH_PROFILE + "/givenName";
    public static final String PROPERTY_PROFILE_USER_FAMILY_NAME = PATH_PROFILE + "/familyName";
    public static final String PROPERTY_PROFILE_ABOUT = PATH_PROFILE + "/aboutMe";

    /***
     * <p>Gets UserManager from session.</p>
     * @param session JCR session
     * @return UserManager instance
     * @throws RepositoryException repository exception
     */
    public static UserManager getUserManager(Session session) throws RepositoryException {
        if (session == null) {
            throw new NullPointerException("session == null");
        }

        return ((JackrabbitSession) session).getUserManager();
    }

    public static User getCurrentUser(Session session) throws RepositoryException {
        return getUser(session, session.getUserID());
    }

    public static User getCurrentUser(ResourceResolver resourceResolver) throws RepositoryException {
        return getUser(resourceResolver.adaptTo(Session.class), resourceResolver.getUserID());
    }

    /***
     * <p>Gets authorizable instance from ID.</p>
     * @param session JCR session
     * @param authorizableId id of user
     * @return User instance or null if not exists
     * @throws RepositoryException repository exception
     */
    public static Authorizable getAuthorizable(Session session, String authorizableId) throws RepositoryException {
        return getUserManager(session).getAuthorizable(authorizableId);
    }

    /**
     * <p>Gets user instance from a user ID.</p>
     *
     * @param session JCR session
     * @param userId  is of user
     * @return User instance or null if not exists
     * @throws RepositoryException repository exception
     */
    public static User getUser(Session session, String userId) throws RepositoryException {
        Authorizable authorizable = getAuthorizable(session, userId);
        if (authorizable != null && !(authorizable instanceof User)) {
            throw new RepositoryException("Expected User: " + userId);
        }
        return (User) authorizable;
    }

    /**
     * <p>Gets group instance from a user ID.</p>
     *
     * @param session JCR session
     * @param groupId is of group
     * @return Group instance or null if not exists
     * @throws RepositoryException repository exception
     */
    public static Group getGroup(Session session, String groupId) throws RepositoryException {
        Authorizable authorizable = getAuthorizable(session, groupId);
        if (authorizable != null && !(authorizable instanceof Group)) {
            throw new RepositoryException("Expected Group: " + groupId);
        }
        return (Group) authorizable;
    }

    /**
     * Gets user formatted display name (given name + family name) or group name.
     *
     * @param resourceResolver sling resolver
     * @param authorizableId   is of user
     * @return formatted display name or authorizable ID when name could not be determined
     */
    public static String getDisplayName(ResourceResolver resourceResolver, String authorizableId) {
        return AuthorizableUtil.getFormattedName(resourceResolver, authorizableId);
    }

    /**
     * <p>Checks if user or group is active - enabled and not deactivated.</p>
     *
     * @param session      JCR session
     * @param authorizable user
     * @return true when active and false when non-active (disabled or deactivated)
     * @throws RepositoryException repository exception
     */
    public static boolean isActive(Session session, Authorizable authorizable) throws RepositoryException {
        if (session == null) {
            throw new NullPointerException("session == null");
        }

        if (authorizable == null) {
            return false;
        }

        if (authorizable instanceof User) {
            User user = (User) authorizable;
            if (user.isDisabled()) {
                return false;
            }
        }

        Node userNode = session.getNode(authorizable.getPath());
        if (userNode.hasProperty(NODE_PROPERTY_LAST_REPLICATION_ACTION)) {
            String lastReplicationActionValue = userNode.getProperty(NODE_PROPERTY_LAST_REPLICATION_ACTION).getString();
            if (lastReplicationActionValue != null) {
                return !ReplicationActionType.DEACTIVATE.getName().equals(lastReplicationActionValue);
            }
        }

        return true;
    }

    /**
     * <p>Gets e-mail from authorizable's profile.</p>
     *
     * @param authorizable Group or User
     * @return email string or empty string when user does not have e-mail property (never null)
     * @throws RepositoryException repository exception
     */
    public static String getEmail(Authorizable authorizable) throws RepositoryException {
        if (authorizable == null) {
            throw new NullPointerException("authorizable == null");
        }

        String email = null;
        Value value = getSingleValuedProperty(authorizable, PROPERTY_PROFILE_EMAIL);
        if (value != null) {
            email = value.getString();
        }
        if (email == null) {
            email = "";
        }
        return email;
    }

    /**
     * <p>Returns mailing target in format: "NAME" [E-MAIL].</p>
     *
     * @param resourceResolver resolver instance
     * @param authorizable     user to check
     * @return Mailing target or null in case of non-specified email
     * @throws RepositoryException if cant read content
     */
    public static String getNameAndEmail(ResourceResolver resourceResolver, Authorizable authorizable) throws RepositoryException {
        String email = getEmail(authorizable);
        if (StringUtils.isNotEmpty(email)) {
            return "\"" + getDisplayName(resourceResolver, authorizable.getID()) + "\" <" + email + ">";
        }
        return null;
    }

    public static void disableUser(User user) throws RepositoryException {
        disableUser(user, "");
    }

    public static void disableUser(User user, String reason) throws RepositoryException {
        if (user == null || reason == null) {
            throw new NullPointerException("user == null or reason == null");
        }

        user.disable(reason);
    }

    public static void enableUser(User user) throws RepositoryException {
        if (user == null) {
            throw new NullPointerException("user == null");
        }

        user.disable(null);
    }

    /**
     * <p>Return single value of a property from authorizable.</p>
     *
     * @param authorizable user
     * @param property     prop name
     * @return value or null in case of no value or multiple values
     * @throws RepositoryException repository exception
     */
    public static Value getSingleValuedProperty(Authorizable authorizable, String property) throws RepositoryException {
        if (authorizable == null || property == null) {
            throw new NullPointerException("user == null or property == null");
        }

        Value[] values = authorizable.getProperty(property);
        if (values != null && values.length == 1) {
            return values[0];
        }
        return null;
    }

    public static String getSingleValuedPropertyString(Authorizable authorizable, String property) throws RepositoryException {
        Value value = getSingleValuedProperty(authorizable, property);
        if (value != null) {
            return value.getString();
        }
        return null;
    }

    /***
     * <p>Checks provided as an argument authorizable and all of its members (when it's a group) to find authorizables having e-mail address.</p>
     * @param rootAuthorizable root user
     * @return collection of authorizables, may be empty but never null
     * @throws RepositoryException repository exception
     */
    public static Collection<Authorizable> provideAuthorizablesHavingEmail(Authorizable rootAuthorizable) throws RepositoryException {
        Collection<Authorizable> authorizables = new LinkedHashSet<>();
        if (StringUtils.isNotEmpty(getEmail(rootAuthorizable))) {
            authorizables.add(rootAuthorizable);
        }

        if (rootAuthorizable.isGroup()) {
            Iterator<Authorizable> membersIterator = ((Group) rootAuthorizable).getMembers();
            while (membersIterator.hasNext()) {
                Authorizable member = membersIterator.next();
                if (member instanceof User) {
                    if (StringUtils.isNotEmpty(getEmail(member))) {
                        authorizables.add(member);
                    }
                }
            }
        }

        return authorizables;
    }


}
