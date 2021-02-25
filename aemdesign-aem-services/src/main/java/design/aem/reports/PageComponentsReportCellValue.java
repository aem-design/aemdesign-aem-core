package design.aem.reports;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static design.aem.utils.components.CommonUtil.RESOURCE_TYPE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Model for rendering component paths in a page.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class PageComponentsReportCellValue {

    private static final Logger log = LoggerFactory.getLogger(PageComponentsReportCellValue.class);
    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue
    @Optional
    @Default(values = "")
    private String componentattribute;

    public PageComponentsReportCellValue() {

    }

    public static String getResourceAttribute(Resource resource, String attribute) {
        String returnValue = "";

        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            ValueMap attributes = resource.getValueMap();
            if (attributes.containsKey(attribute)) {
                return attributes.get(attribute, String.class);
            }
        }

        return returnValue;
    }

    public static String getComponentAttribute(Resource resource, String attribute, Collection<Component> allComponents) {
        String returnValue = "";

        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

            returnValue = resource.getName();

            Component component = allComponents.stream()
                .filter(x -> x.getResourceType().equals(resource.getResourceType()))
                .findFirst()
                .orElse(null);


            if (component != null) {

                returnValue = component.getName();

                if (isNotEmpty(attribute)) {
                    ValueMap componentProperties = component.getProperties();
                    if (componentProperties != null) {
                        returnValue = componentProperties.get(attribute, String.class);
                    }
                }
            }
        }

        return returnValue;
    }

    public static HashMap getResourceChildrenComponents(Resource resource, String attribute) {
        HashMap components = new HashMap();
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            //return my self
            if (isNotEmpty(attribute)) {
                components.put(getResourceAttribute(resource, attribute), "");
            } else {
                components.put(resource.getPath(), "");
            }
            //return my children
            for (Resource child : resource.getChildren()) {
                if (isNotEmpty(attribute)) {
                    components.put(getResourceAttribute(child, attribute), "");
                } else {
                    components.put(child.getPath(), "");
                }
                if (child.hasChildren()) {
                    components.putAll(getResourceChildrenComponents(child, attribute));
                }
            }
        }
        components.remove("");

        return components;
    }


    public String getComponents() {

        Resource resource = (Resource)this.request.getAttribute("result");

        log.debug("Finding components in {}", resource.getPath());
        HashMap components = new HashMap();


        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

            Page page = resource.adaptTo(Page.class);
            Resource pageContent = page.getContentResource();

            components.putAll(getResourceChildrenComponents(pageContent, RESOURCE_TYPE));
            components.remove("");
        }

        log.debug("Found components: {}", components);

        return StringUtils.join(components.keySet(), "\n");
    }


    public static List<TreeNode> getResourceChildrenComponentsTreeList(Resource resource, String attribute, Collection<Component> allComponents) {
        List<TreeNode> components = new LinkedList();
        List<TreeNode> childComponents = new LinkedList();

        //skip me if
        // resource is null
        // resource does not exist
        // resource does not have sling:resourceType resource type
        if (resource == null
            || ResourceUtil.isNonExistingResource(resource)
            || !resource.getValueMap().containsKey(RESOURCE_TYPE)) {
            return components;
        }

        if (resource.hasChildren()) {

            //return my children
            for (Resource child : resource.getChildren()) {

                if (child.hasChildren()) {
                    //return child with sub children
                    childComponents.addAll(getResourceChildrenComponentsTreeList(child, attribute, allComponents));
                } else {
                    if (isNotEmpty(attribute)) {
                        childComponents.add(new TreeNode(getComponentAttribute(resource, attribute, allComponents), new LinkedList()));
                    } else {
                        childComponents.add(new TreeNode(resource.getName(), new LinkedList()));
                    }
                }
            }

        }

        //return self
        if (isNotEmpty(attribute)) {
            components.add(new TreeNode(getComponentAttribute(resource, attribute, allComponents), childComponents));
        } else {
            components.add(new TreeNode(resource.getName(), childComponents));
        }

        return components;
    }

    public String getComponentTree() {
        ComponentManager compMgr = this.request.getResourceResolver().adaptTo(ComponentManager.class);
        Collection<Component> components = compMgr.getComponents();
        Resource resource = (Resource)this.request.getAttribute("result");

        List<TreeNode> children = new LinkedList();
        String returnValue = "";

        log.debug("Finding components in {} with componentattribute: {}", resource.getPath(), componentattribute);


        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

            Page page = resource.adaptTo(Page.class);
            Resource pageContent = page.getContentResource();

            children.addAll(getResourceChildrenComponentsTreeList(pageContent, componentattribute, components));
            if (children.size() == 1) {
                returnValue = children.get(0).toString();
            } else if (children.size() > 1) {
                returnValue = StringUtils.join(children, "\r");
            }
        }

        log.debug("Found components: {}", children);

        return returnValue;
    }

    public static class TreeNode {

        final String name;
        final List<TreeNode> children;

        public TreeNode(String name, List<TreeNode> children) {
            this.name = name;
            this.children = children;
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder(50);
            print(buffer, "", "");
            return buffer.toString();
        }

        private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            buffer.append( isNotEmpty(name) ? name : "unknown");
            buffer.append('\r');
            if (children != null) {
                for (Iterator<TreeNode> it = children.iterator(); it.hasNext(); ) {
                    TreeNode next = it.next();
                    if (it.hasNext()) {
                        next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "|   ");
                    } else {
                        next.print(buffer, childrenPrefix + "|-- ", childrenPrefix + "    ");
                    }
                }
            }
        }
    }

}
