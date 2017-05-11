//package design.aem.impl.common;
//
//import org.apache.sling.api.resource.*;
//
//import javax.jcr.Session;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * ResourceResolver decorator that implements AutoClosable interface.
// * Also provides static `administrative(resourceResolverFactory)` method.
// */
//public abstract class CloseableResourceResolver implements ResourceResolver, AutoCloseable {
//
//    private ResourceResolver resourceResolver;
//
//    public CloseableResourceResolver(ResourceResolver resourceResolver) {
//        this.resourceResolver = resourceResolver;
//    }
//
//    public Session getSession() {
//        return resourceResolver.adaptTo(Session.class);
//    }
//
//    @Override
//    public Resource resolve(HttpServletRequest httpServletRequest, String s) {
//        return resourceResolver.resolve(httpServletRequest, s);
//    }
//
//    @Override
//    public Resource resolve(String s) {
//        return resourceResolver.resolve(s);
//    }
//
//    @Override
//    @Deprecated
//    public Resource resolve(HttpServletRequest httpServletRequest) {
//        return resourceResolver.resolve(httpServletRequest);
//    }
//
//    @Override
//    public String map(String s) {
//        return resourceResolver.map(s);
//    }
//
//    @Override
//    public String map(HttpServletRequest httpServletRequest, String s) {
//        return resourceResolver.map(httpServletRequest, s);
//    }
//
//    @Override
//    public Resource getResource(String s) {
//        return resourceResolver.getResource(s);
//    }
//
//    @Override
//    public Resource getResource(Resource resource, String s) {
//        return resourceResolver.getResource(resource, s);
//    }
//
//    @Override
//    public String[] getSearchPath() {
//        return resourceResolver.getSearchPath();
//    }
//
//    @Override
//    public Iterator<Resource> listChildren(Resource resource) {
//        return resourceResolver.listChildren(resource);
//    }
//
//    @Override
//    public Resource getParent(Resource resource) {
//        return resourceResolver.getParent(resource);
//    }
//
//    @Override
//    public Iterable<Resource> getChildren(Resource resource) {
//        return null;
//    }
//
//    @Override
//    public Iterator<Resource> findResources(String s, String s1) {
//        return resourceResolver.findResources(s, s1);
//    }
//
//    @Override
//    public Iterator<Map<String, Object>> queryResources(String s, String s1) {
//        return resourceResolver.queryResources(s, s1);
//    }
//
//    @Override
//    public boolean hasChildren(Resource resource) {
//        return false;
//    }
//
//    @Override
//    public ResourceResolver clone(Map<String, Object> map) throws LoginException {
//        return resourceResolver.clone(map);
//    }
//
//    @Override
//    public boolean isLive() {
//        return resourceResolver.isLive();
//    }
//
//    @Override
//    public void close() {
//        resourceResolver.close();
//    }
//
//    @Override
//    public String getUserID() {
//        return resourceResolver.getUserID();
//    }
//
//    @Override
//    public Iterator<String> getAttributeNames() {
//        return resourceResolver.getAttributeNames();
//    }
//
//    @Override
//    public Object getAttribute(String s) {
//        return resourceResolver.getAttribute(s);
//    }
//
//    @Override
//    public void delete(Resource resource) throws PersistenceException {
//        resourceResolver.delete(resource);
//    }
//
//    @Override
//    public Resource create(Resource resource, String s, Map<String, Object> map) throws PersistenceException {
//        return null;
//    }
//
//    @Override
//    public void revert() {
//        resourceResolver.revert();
//    }
//
//    @Override
//    public void commit() throws PersistenceException {
//        resourceResolver.commit();
//    }
//
//    @Override
//    public boolean hasChanges() {
//        return false;
//    }
//
//    @Override
//    public String getParentResourceType(Resource resource) {
//        return null;
//    }
//
//    @Override
//    public String getParentResourceType(String s) {
//        return null;
//    }
//
//    @Override
//    public boolean isResourceType(Resource resource, String s) {
//        return false;
//    }
//
//    @Override
//    public void refresh() {
//        resourceResolver.refresh();
//    }
//
//    @Override
//    public Resource copy(String s, String s1) throws PersistenceException {
//        return null;
//    }
//
//    @Override
//    public Resource move(String s, String s1) throws PersistenceException {
//        return null;
//    }
//
//    @Override
//    public <AdapterType> AdapterType adaptTo(Class<AdapterType> aClass) {
//        return resourceResolver.adaptTo(aClass);
//    }
//
//    /**
//     * Creates administrative resource resolver
//     * @param factory
//     * @return CloseableResourceResolver instance
//     */
//    public static CloseableResourceResolver administrative(ResourceResolverFactory factory) {
//        try {
//            ResourceResolver resourceResolver = factory.getAdministrativeResourceResolver(null);
//            return new CloseableResourceResolver(resourceResolver);
//        } catch (LoginException e) {
//            throw new RuntimeException("Could not create administrative resource resolver", e);
//        }
//    }
//}
