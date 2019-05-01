package design.aem.utils.components;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.SlingPostConstants;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

public class SlingPostUtil {

    /**
     * Copied from Sling. Later on Sling POST Servlet will be refactored to provide a generic service for this.
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
     */
    public static void writeContent(final Node parentNode, final HttpServletRequest req) throws Exception {
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

    public static List<String> getProcessedTags(TagManager tagManager, String name, final HttpServletRequest req)
            throws Exception {
        List<String> processedTags = new ArrayList<String>();

        if (tagManager != null) {
            String[] tags = req.getParameterValues(name);

            if (tags != null) {
                for (String tagId : tags) {
                    if (tagId.length() == 0) continue;

                    if (tagId.indexOf(":") < 0) {
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
     * @param req
     * @return
     * @throws Exception
     */
    public static List<String> getTagRequestParameters(final HttpServletRequest req) throws Exception {
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
     * @param name
     * @return
     * @throws Exception
     */
    public static String getPropertyName(String name) throws Exception {
        if (name.startsWith("./")) {
            name = name.substring("./".length());

            if (name.indexOf("/") > -1) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
        }
        return name;
    }

    /**
     * Get the parent node
     * If the parent doesn't exist, create it
     * @param parentNode
     * @param name
     * @return
     * @throws Exception
     */
    public static Node getParentNode(Node parentNode, String name) throws Exception {
        if (name.startsWith("./")) {
            name = name.substring("./".length());

            if (name.indexOf("/") > -1) {
                String relPath = name.substring(0, name.lastIndexOf("/"));
                if (parentNode.hasNode(relPath)) {
                    parentNode = parentNode.getNode(relPath);
                } else {
                    parentNode = JcrUtil.createPath(parentNode.getPath() + "/" + relPath, "nt:unstructured",
                            parentNode.getSession());
                }
            }
        }

        return parentNode;
    }
}
