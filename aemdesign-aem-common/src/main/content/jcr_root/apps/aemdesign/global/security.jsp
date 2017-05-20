<%@ page import="java.util.*" %>
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
                authInfo.put(org.apache.sling.jcr.resource.JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
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
%>
