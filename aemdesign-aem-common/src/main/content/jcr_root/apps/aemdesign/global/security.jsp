<%@ page import="com.adobe.granite.security.user.UserProperties" %>
<%@ page import="com.adobe.granite.security.user.UserPropertiesManager" %>
<%@ page import="org.apache.jackrabbit.api.security.user.Authorizable" %>
<%@ page import="org.apache.jackrabbit.api.security.user.Group" %>
<%@ page import="org.apache.jackrabbit.api.security.user.User" %>
<%@ page import="org.apache.jackrabbit.api.security.user.UserManager" %>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@ page import="static org.apache.commons.lang3.StringUtils.isEmpty" %>
<%@ page import="javax.jcr.Session" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>

<%!
    private final Logger SECLOG = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public org.apache.sling.api.resource.ResourceResolver openAdminResourceResolver(org.apache.sling.api.scripting.SlingScriptHelper _sling) {

        org.apache.sling.api.resource.ResourceResolver _adminResourceResolver = null;

        org.apache.sling.jcr.api.SlingRepository _slingRepository = _sling.getService(org.apache.sling.jcr.api.SlingRepository.class);
        org.apache.sling.api.resource.ResourceResolverFactory resolverFactory = _sling.getService(org.apache.sling.api.resource.ResourceResolverFactory.class);
        try {
            javax.jcr.Session adminResourceSession = _slingRepository.loginAdministrative(null);
//            Credentials adminCredentials = new SimpleCredentials("admin", new char[0]);
//            adminResourceSession = _slingRepository.impersonateFromService("workflow", credentials, null);
            Map authInfo = new HashMap();
            authInfo.put(org.apache.sling.jcr.resource.api.JcrResourceConstants.AUTHENTICATION_INFO_SESSION, adminResourceSession);
            _adminResourceResolver = resolverFactory.getResourceResolver(authInfo);

        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        return _adminResourceResolver;

    }


    public void closeAdminResourceResolver(org.apache.sling.api.resource.ResourceResolver _adminResourceResolver) {

        if (_adminResourceResolver != null && _adminResourceResolver.isLive()) {

            //need to close the sessions before resolver as it will not be closed by resolver.close()
            //this is a know "pattern"
            javax.jcr.Session adminResourceSession = _adminResourceResolver.adaptTo(Session.class);
            if (adminResourceSession != null && adminResourceSession.isLive()) {
                adminResourceSession.logout();
            }

            _adminResourceResolver.close();
        }

    }

    public boolean isUserMemberOf(Authorizable authorizable, List<String> groups) {
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
                if (groups.contains(group.getPrincipal().getName())) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public String getUserEmail(UserManager _userManager, UserPropertiesManager _userPropertiesManager, String userId, String defaultValue) {
        if (isEmpty(defaultValue)) {
            defaultValue = "";
        }
        String email = defaultValue;
        try {

            Authorizable userAuth = _userManager.getAuthorizable(userId);
            if (userAuth != null) {
                UserProperties userProps = _userPropertiesManager.getUserProperties(userAuth, "profile");
                if (userProps != null) {
                    email = userProps.getProperty("email");

                    if (email == null) {
                        email = defaultValue;
                    }

                } else {
                    SECLOG.warn(">> getUserEmail: userId: {0}, has not profile", userId);
                }

            } else {
                SECLOG.warn(">> getUserEmail: userId: {0}, does not exist1", userId);
            }

        } catch (Exception ex) {
            SECLOG.error("could not get user email {0}, {1}", userId, ex);
        }
        return email;
    }

    public String getUserFullName(UserManager _userManager, UserPropertiesManager _userPropertiesManager, String userId, String defaultValue) {
        if (isEmpty(defaultValue)) {
            defaultValue = "";
        }
        String fullName = defaultValue;
        String givenName = "";
        String familyName = "";
        try {

            Authorizable userAuth = _userManager.getAuthorizable(userId);
            if (userAuth != null) {
                fullName = userAuth.getPrincipal().getName();

                UserProperties userProps = _userPropertiesManager.getUserProperties(userAuth, "profile");
                if (userProps != null) {

                    givenName = userProps.getProperty("givenName");

                    if (givenName == null) {
                        givenName = "";
                    }

                    familyName = userProps.getProperty("familyName");

                    if (familyName == null) {
                        familyName = "";
                    }

                    fullName = MessageFormat.format("{0} {1}", givenName, familyName).trim();

                    if (isEmpty(fullName)) {
                        fullName = defaultValue;
                    }

                } else {
                    SECLOG.warn(">> getUserFullName: userId: {0}, has not profile", userId);
                }

            } else {
                SECLOG.warn(">> getUserFullName: userId: {0}, does not exist1", userId);
            }

        } catch (Exception ex) {
            SECLOG.error("could not get user full name {0}, {1}", userId, ex);
        }
        return fullName;
    }


%>
