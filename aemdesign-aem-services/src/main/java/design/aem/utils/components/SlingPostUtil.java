package design.aem.utils.components;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class SlingPostUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlingPostUtil.class);

    /**
     * Copied from Sling. Later on Sling POST Servlet will be refactored to provide a generic service for this.
     * @param parentNode node to delete content from
     * @param req sling request
     * @throws Exception when can't read content
     */
    public static void processDeletes(final Node parentNode, final HttpServletRequest req) throws Exception {
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();

            if (!name.startsWith("./")) continue;
            if (!name.endsWith(SlingPostConstants.SUFFIX_DELETE)) continue;

            if (parentNode.hasProperty(name)) {
                parentNode.getProperty(name).remove();
            } else if (parentNode.hasNode(name)) {
                parentNode.getNode(name).remove();
            }
        }
    }

    /**
     * Copied from Sling. Later on Sling POST Servlet will be refactored to provide a generic service for this.
     * @param parentNode node to write into
     * @param req sling request
     * @throws RepositoryException when can't read content
     */
    public static void writeContent(final Node parentNode, final HttpServletRequest req) throws RepositoryException {
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();

            if (!name.startsWith("./")) continue;
            // ignore all tags, they are handled separately
            if (name.indexOf("cq:tags") > 0) continue;
            if (name.startsWith("jcr:primaryType")) continue;
            if (name.startsWith("jcr:mixinTypes")) continue;
            if (name.endsWith(SlingPostConstants.TYPE_HINT_SUFFIX)) continue;

            String[] values = req.getParameterValues(name);

            String typeHint = req.getParameter(name + SlingPostConstants.TYPE_HINT_SUFFIX);
            boolean multiple = false;

            if (typeHint != null && typeHint.endsWith("[]")) {
                typeHint = typeHint.substring(0, typeHint.length() - "[]".length());
                multiple = true;
            }

            int type = PropertyType.STRING;
            if (typeHint != null) {
                type = PropertyType.valueFromName(typeHint);
            }

            List<String> jcrValues = new ArrayList<String>();

            for (String value : values) {
                if (value.length() > 0) {
                    jcrValues.add(value);
                }
            }

            multiple = multiple || jcrValues.size() > 1;

            String n = getPropertyName(name);
            Node finalNode = getParentNode(parentNode, name);

            if (multiple) {
                finalNode.setProperty(n, jcrValues.toArray(new String[0]), type);
            } else if (!jcrValues.isEmpty()) {
                finalNode.setProperty(n, jcrValues.get(0), type);
            }
        }
    }

    /**
     * Get tags form request and resolve to tag id strings.
     * @param tagManager tag manager instance
     * @param name request param name
     * @param req sling request
     * @return list of tag ids
     * @throws Exception when can't read content
     */
    public static List<String> getProcessedTags(TagManager tagManager, String name, final HttpServletRequest req)
            throws Exception {
        List<String> processedTags = new ArrayList<String>();

        if (tagManager != null) {
            String[] tags = req.getParameterValues(name);

            if (tags != null) {
                for (String tagId : tags) {
                    if (tagId.length() == 0) continue;

                    if (tagId.contains(":")) {
                        Tag tag = tagManager.createTagByTitle(tagId, Locale.ENGLISH); // This is fixed to "en" in old siteadmin also
                        tagId = tag.getTagID();
                    }

                    processedTags.add(tagId);
                }
            }
        }
        return processedTags;
    }

    /**
     * Find all cq:tags parameters as they have to be handled separated
     * @param req sling request instance
     * @return list of tags
     */
    public static List<String> getTagRequestParameters(final HttpServletRequest req) {
        List<String> tagsParameters = new ArrayList<String>();
        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
            String name = en.nextElement().toString();
            if (name.endsWith("cq:tags")) {
                tagsParameters.add(name);
            }
        }
        return tagsParameters;
    }

    /**
     * Get property name for repository
     * @param name property name name
     * @return property name
     */
    public static String getPropertyName(String name) {
        if (name.startsWith("./")) {
            name = name.substring("./".length());

            if (name.contains("/")) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
        }
        return name;
    }

    /**
     * Get the parent node, if the node doesn't exist, create it.
     * @param parentNode parent node to look in
     * @param name node name to look for
     * @return returns child node
     */
    public static Node getParentNode(Node parentNode, String name) {
        try {
            if (name.startsWith("./")) {
                name = name.substring("./".length());

                if (name.contains("/")) {
                    String relPath = name.substring(0, name.lastIndexOf("/"));
                    if (parentNode.hasNode(relPath)) {
                        parentNode = parentNode.getNode(relPath);
                    } else {
                        parentNode = JcrUtil.createPath(parentNode.getPath() + "/" + relPath, "nt:unstructured", parentNode.getSession());
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("getParentNode: could not get node named {} in {}, ex: {}", name, parentNode, ex);
        }
        return parentNode;
    }
}
