package design.aem.utils;

import design.aem.ProjectAuthorizableType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

public final class ProjectsUtil {

    private static final Logger log = LoggerFactory.getLogger(ProjectsUtil.class);

    public static final String PROJECTS_ROOT_PATH = "/content/projects";
    public static final String PROJECTS_TEMPLATE = "/libs/cq/core/content/projects/templates/default";

    private ProjectsUtil() { }

    public static Group getOwners(Resource project) {
        return getAuthorizable(project, ProjectAuthorizableType.OWNERS);
    }

    public static Group getApprovers(Resource project) {
        return getAuthorizable(project, ProjectAuthorizableType.APPROVERS);
    }

    public static Group getObservers(Resource project) {
        return getAuthorizable(project, ProjectAuthorizableType.OBSERVERS);
    }

    public static Group getAuthorizable(Resource project, ProjectAuthorizableType projectAuthorizable) {
        ValueMap valueMap = project.adaptTo(ValueMap.class);
        String authorizableId = valueMap.get(projectAuthorizable.getPropertyName(), String.class);
        if (authorizableId != null) {
            try {
                Session session = project.getResourceResolver().adaptTo(Session.class);
                return (Group) UserManagementUtil.getAuthorizable(session, authorizableId);
            } catch (RepositoryException e) {
                throw new RuntimeException("Could not fetch project authorizable", e);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static boolean isProject(Resource resource) {
        if (resource != null) {
            Resource content = resource.getChild(JCR_CONTENT);
            if (content != null) {
                return ObjectUtils.equals(PROJECTS_TEMPLATE, content.adaptTo(ValueMap.class).get("cq:template", String.class));
            }
        }

        return false;
    }

    public static boolean isActive(Resource project) {
        return project.getChild(JCR_CONTENT).adaptTo(ValueMap.class).get("active", false);
    }

    public static String getDAMFolderPath(Resource project) {
        return project.getChild(JCR_CONTENT).adaptTo(ValueMap.class).get("damFolderPath", String.class);
    }

    public static Collection<String> getAllProjectsPaths(ResourceResolver resourceResolver) {
        Map<String, String> query = new LinkedHashMap<String, String>();
        query.put("path", PROJECTS_ROOT_PATH);
        query.put("property", "jcr:content/cq:template");
        query.put("property.value", PROJECTS_TEMPLATE);
        try {
            return QueryBuilderUtil.queryForPaths(resourceResolver, query);
        } catch (RepositoryException e) {
            log.error("Could not execute all PROJECTSs query", e);
            return Collections.emptyList();
        }
    }

    public static Collection<String> getActiveProjectsPaths(ResourceResolver resourceResolver) {
        Map<String, String> query = new LinkedHashMap<String, String>();
        query.put("path", PROJECTS_ROOT_PATH);
        query.put("group.0_property", "jcr:content/cq:template");
        query.put("group.0_property.value", PROJECTS_TEMPLATE);
        query.put("group.1_property", "jcr:content/active");
        query.put("group.1_property.operation", "equals");
        query.put("group.1_property.value", "true");
        try {
            return QueryBuilderUtil.queryForPaths(resourceResolver, query);
        } catch (RepositoryException e) {
            log.error("Could not execute active projects query", e);
            return Collections.emptyList();
        }
    }

    public static Collection<Resource> findRelatedProjects(Resource resource) {
        return findRelatedProjectsByPaths(resource, getAllProjectsPaths(resource.getResourceResolver()));
    }

    public static Collection<Resource> findRelatedActiveProjects(Resource resource) {
        return findRelatedProjectsByPaths(resource, getActiveProjectsPaths(resource.getResourceResolver()));
    }

    private static Collection<Resource> findRelatedProjectsByPaths(Resource resource, Collection<String> projectsPaths) {
        Collection<Resource> matchingProjects = new LinkedHashSet<Resource>();
        //quick fail
        if (resource == null || projectsPaths == null) {
            return matchingProjects;
        }

        ResourceResolver resourceResolver = resource.getResourceResolver();

        Node node = resource.adaptTo(Node.class);
        boolean isAsset = false;
        boolean isTask = false;

        if (node != null) {
            try {
                isAsset = node.getPrimaryNodeType().isNodeType("dam:Asset");
                isTask = node.getPrimaryNodeType().isNodeType("granite:Task");
            } catch (RepositoryException ignore) {
                isAsset = false;
                isTask = false;
            }
        }

        String path = resource.getPath();

        Collection<String> projectPathsAssociatedToAsset = new LinkedHashSet<String>();

        if (isAsset) {
            if (path.startsWith("/content/dam/projects")) {
                Resource checkResource = resource;
                while (checkResource != null) {
                    String[] projectPaths = checkResource.adaptTo(ValueMap.class).get("projectPath", String[].class);
                    if (projectPaths != null) {
                        Collections.addAll(projectPathsAssociatedToAsset, projectPaths);
                        break; //TODO: should ancestor projects tree be also taken into account ?
                    }
                    checkResource = checkResource.getParent();
                    if (checkResource != null && checkResource.getPath().equals("/content/dam")) {
                        break;
                    }
                }
            }
        }

        for (String projectPath : projectsPaths) {
            boolean matches = false;

            Resource project = resourceResolver.getResource(projectPath);

            if (projectPathsAssociatedToAsset.contains(projectPath)) {
                matches = true;
            }

            if (isAsset) {
                //check DAM Asset path
                String damFolderPath = getDAMFolderPath(project);
                if (path.equals(damFolderPath + "/" + resource.getName())) {
                    matches = true;
                } else {
                    //check existing DAM Asset paths associated with specified collection
                    String collectionPath = project.getChild("jcr:content/dashboard/gadgets/collection").adaptTo(ValueMap.class).get("collectionPath", String.class);
                    if (collectionPath != null) {
                        Resource collection = resourceResolver.getResource(collectionPath);
                        Resource collectionMembers = collection.getChild("sling:members");
                        if (collectionMembers != null) {
                            Iterator<Resource> memberIterator = collectionMembers.listChildren();
                            while (memberIterator.hasNext()) {
                                Resource member = memberIterator.next();
                                String memberTargetPath = member.adaptTo(ValueMap.class).get("sling:resource", String.class);
                                if (memberTargetPath != null) {
                                    if (path.equals(memberTargetPath) || path.startsWith(memberTargetPath + "/")) {
                                        matches = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isTask) {
                if (path.startsWith(projectPath + "/jcr:content/tasks")) {
                    matches = true;
                }
            }

            if (matches) {
                matchingProjects.add(project);
            }
        }

        return matchingProjects;
    }

}
