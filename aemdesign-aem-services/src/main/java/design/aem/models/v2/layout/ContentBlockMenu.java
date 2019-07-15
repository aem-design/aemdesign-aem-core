package design.aem.models.v2.layout;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.LinkedHashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ImagesUtil.DEFAULT_BACKGROUND_IMAGE_NODE_NAME;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentBlockMenu extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentBlockMenu.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() throws Exception {

        final String DEFAULT_MENUSOURCE_PARENT = "parent";
        final String DEFAULT_MENUSOURCE_PAGEPATH = "pagepath";
        final String FIELD_MENUSOURCE = "menuSource";
        final String FIELD_MENUSOURCEPAGEPATH = "menuSourcePagePath";

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_MENUSOURCE, DEFAULT_MENUSOURCE_PARENT},
                {FIELD_MENUSOURCEPAGEPATH, ""},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        Map<String, String> contentBlockList = new LinkedHashMap<String, String>();
        Resource menuSource = getResource().getParent();

        if(componentProperties.get(FIELD_MENUSOURCE, DEFAULT_MENUSOURCE_PARENT).equals(DEFAULT_MENUSOURCE_PAGEPATH)) {
            String menuSourcePagePath = componentProperties.get(FIELD_MENUSOURCEPAGEPATH, "");
            if (isNotEmpty(menuSourcePagePath)) {
                Resource menuSourcePagePathRes = getResourceResolver().getResource(menuSourcePagePath);
                if (menuSourcePagePathRes != null) {
                    menuSource = menuSourcePagePathRes;
                }
            }
        }


        if (menuSource != null) {
            contentBlockList = getContentBlockMenu(menuSource);
        }

        componentProperties.put("contentBlockList",contentBlockList);

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,getBackgroundImageRenditions(this));

    }


    /**
     * Checks whether a jcr node is a content block we want to be processing
     *
     * @param childNode is the childnode
     * @return true if this is a content block component
     * @throws RepositoryException
     */
    private static boolean isContentBlockComponent(Node childNode) throws RepositoryException {

        return
                (childNode.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) &&
                        childNode.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getString().endsWith("contentblock") ) ||
                        (childNode.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY) &&
                                childNode.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).getString().endsWith("contentblocklock") );

    }

    /**
     * Return true if this content block node should be shown in the content menu. This
     * is indicated by the avialability of the hideInMenu property. If it's not there, it should
     * be visible, if it is set to true, it should be visible, otherwise hide it.
     *
     * @param childNode is the content block child node to inspect
     * @return true if the content block's title should be shown in the menu
     *
     * @throws RepositoryException when something weird happens retrieving the properties.
     */
    private static boolean isVisibleInMenu(Node childNode) throws RepositoryException {
        return
                // not been set? it's visible
                !childNode.hasProperty(FIELD_HIDEINMENU) ||
                        // set to true? it's visible.
                        "true".equals(childNode.getProperty(FIELD_HIDEINMENU).getString());
    }

    /**
     * Get the content block menu for page <code>page</code>
     *
     * @param parSys is the Resource to
     * @return a sequenced map of the content block anchor names and their titles
     * @throws RepositoryException
     */
    private static Map<String, String> getContentBlockMenu(Resource parSys) throws RepositoryException {
        Map<String, String> contentMenu = new LinkedHashMap<String, String>();

        if (parSys != null) {

            Node contentResourceNode = parSys.adaptTo(Node.class);
            if (contentResourceNode != null) {
                NodeIterator nodeIterator = contentResourceNode.getNodes();

                // iterate over children
                if (nodeIterator != null) {

                    while (nodeIterator.hasNext()) {

                        Node childNode = nodeIterator.nextNode();
                        if (childNode == null) {
                            continue;
                        }

                        if (isContentBlockComponent(childNode) && isVisibleInMenu(childNode)) {
                            String childTitle = childNode.getName();
                            String childName = childNode.getName();

                            if (childNode.hasProperty(FIELD_STYLE_COMPONENT_ID)) {
                                String componentId = childNode.getProperty(FIELD_STYLE_COMPONENT_ID).getString();
                                if (isNotEmpty(componentId)) {
                                    childName = componentId;
                                }
                            }

                            if (childNode.hasProperty("title")) {
                                childTitle = childNode.getProperty("title").getString();
                                if (isEmpty(childTitle)) {
                                    childTitle = childName;
                                }
                            }
                            contentMenu.put(childName, childTitle);

                        }
                    }

                    return contentMenu;
                }
            }
        }

        return contentMenu;
    }

}