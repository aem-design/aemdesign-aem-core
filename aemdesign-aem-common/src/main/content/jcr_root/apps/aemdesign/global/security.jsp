<%@ page import="org.apache.jackrabbit.api.security.user.Authorizable" %>
<%@ page import="org.apache.jackrabbit.api.security.user.Group" %>
<%@ page import="org.apache.jackrabbit.api.security.user.User" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%!
        @SuppressWarnings("unchecked")
        public org.apache.sling.api.resource.ResourceResolver openAdminResourceResolver(org.apache.sling.api.scripting.SlingScriptHelper _sling){

            org.apache.sling.api.resource.ResourceResolver _adminResourceResolver  = null;

            org.apache.sling.jcr.api.SlingRepository _slingRepository = _sling.getService(org.apache.sling.jcr.api.SlingRepository.class);
            org.apache.sling.api.resource.ResourceResolverFactory resolverFactory = _sling.getService(org.apache.sling.api.resource.ResourceResolverFactory.class);
            javax.jcr.Session session = null;
            try{
                session = _slingRepository.loginAdministrative(null);
                Map authInfo = new HashMap();
                authInfo.put(org.apache.sling.jcr.resource.api.JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
                _adminResourceResolver  = resolverFactory.getResourceResolver(authInfo);
            }catch (Exception ex){
                // ex.printStackTrace();
            }

            return _adminResourceResolver;

        }


        public void closeAdminResourceResolver(org.apache.sling.api.resource.ResourceResolver _adminResourceResolver){

            if (_adminResourceResolver != null && _adminResourceResolver.isLive()){
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
%>
