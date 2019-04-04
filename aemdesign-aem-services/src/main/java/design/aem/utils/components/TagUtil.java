package design.aem.utils.components;

import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import design.aem.services.ContentAccess;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class TagUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagUtil.class);

    public final static String TAG_VALUE = "value";
    final static String TAG_ISDEFAULT = "isdefault";
    final static String TAG_ISDEFAULT_VALUE = "false";

    //private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Create a formatted localized string that contains all tags in the tagProperty.
     * @param tagManager tag manager
     * @param tagPaths list of tag paths
     * @param locale locale to apply
     * @return comma separated list of tag titles
     */
    public static String getTags(TagManager tagManager, String[] tagPaths, Locale locale) {

        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        // convert from tag path into tag titles
        List<String> tags = new ArrayList<String>();
        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                if (locale == null) {
                    tags.add(jcrTag.getTitle());
                } else {
                    tags.add(jcrTag.getLocalizedTitle(locale));
                }
            }
        }

        // concat them into a string
        int idx = 0;
        StringBuilder builder = new StringBuilder();
        for (String tag : tags) {
            builder.append(tag);
            ++idx;
            if (idx != tags.size()) {
                builder.append(", ");
            }
        }

        // return buffer
        return builder.toString();
    }

    /**
     * Create a formatted string that contains all tags in the tagProperty.
     *
     * @param tagPaths is the property that contains all tags
     * @return a formatted string
     */
    public static String getTags(TagManager tagManager, String[] tagPaths) {
        return getTags(tagManager, tagPaths, null);
    }

    /**
     * Get tag values from a JCR node.
     * @param tagManager tag manager
     * @param thisNode node
     * @param tagPropertyName property name
     * @return list of tags
     */
    public static List<Tag> getTags(TagManager tagManager, Node thisNode, String tagPropertyName) throws RepositoryException {
        if (tagManager == null || thisNode == null) {
            return null;
        }

        Value[] pageTagValues = null;
        if (thisNode.hasProperty(tagPropertyName)) {
            if (thisNode.getProperty(tagPropertyName).isMultiple()) {
                pageTagValues = thisNode.getProperty(tagPropertyName).getValues();
            } else {
                pageTagValues = new Value[1];
                pageTagValues[0] = thisNode.getProperty(tagPropertyName).getValue();
            }
        }

        if (pageTagValues == null || pageTagValues.length == 0) {
            return null;
        }

        List<Tag> tags = new ArrayList<Tag>();
        for (Value tagValue : pageTagValues) {
            if (tagValue != null) {
                String path = tagValue.getString();
                if (path != null) {
                    Tag jcrTag = tagManager.resolve(path);
                    if (jcrTag != null) {
                        tags.add(jcrTag);
                    }
                }
            }
        }
        return tags;
    }


    /**
     * Get tag values from a JCR node.
     * @param tagManager tag manager
     * @param valueMap inheritance value map
     * @param tagPropertyName property to use
     * @return List<Tag>
     */
    public static Map<String, Tag> getTagsMap(TagManager tagManager, InheritanceValueMap valueMap, String tagPropertyName, Boolean tryInherit) throws RepositoryException {
        Value[] pageTagValues = null;
        String[] tagStrings = null;
        Map<String, Tag> tags = new HashMap<String, Tag>();

        if (tryInherit) {
            tagStrings = valueMap.get(tagPropertyName, valueMap.getInherited(tagPropertyName, new String[0]));
        } else {
            tagStrings = valueMap.get(tagPropertyName, new String[0]);
        }


        if (tagStrings == null || tagStrings.length == 0) {
            return tags;
        }

        for (String tagValue : tagStrings) {
            if (isNotEmpty(tagValue)) {
                Tag jcrTag = tagManager.resolve(tagValue);
                if (jcrTag != null) {
                    tags.put(jcrTag.getTagID(), jcrTag);
                }
            }
        }
        return tags;
    }

    /**
     * Get tag values from a JCR node.
     *
     * @param tagManager
     * @param thisNode
     * @param tagPropertyName
     * @return List<Tag>
     */
    public static List<Node> getTagsAsNodes(TagManager tagManager, Node thisNode, String tagPropertyName) throws RepositoryException {
        Value[] pageTagValues = null;
        if (thisNode.hasProperty(tagPropertyName)) {
            pageTagValues = thisNode.getProperty(tagPropertyName).getValues();
        }

        if (pageTagValues == null || pageTagValues.length == 0) {
            return null;
        }

        List<Node> tags = new ArrayList<Node>();
        for (Value tagValue : pageTagValues) {
            if (tagValue != null) {
                String path = tagValue.getString();
                if (path != null) {
                    Tag jcrTag = tagManager.resolve(path);
                    if (jcrTag != null) {
                        tags.add(jcrTag.adaptTo(Node.class));
                    }
                }
            }
        }
        return tags;
    }

    /**
     * Get tag values from a JCR node.
     *
     * @param tagManager
     * @param thisNode
     * @param tagPropertyName
     * @param tagPath
     * @return boolean
     */
    public static boolean containsTag(TagManager tagManager, Node thisNode, String tagPropertyName, String tagPath) throws RepositoryException {
        boolean containsTagFlag = false;
        Tag jcrTag = tagManager.resolve(tagPath);
        try {
            List<Tag> tags = getTags(tagManager, thisNode, tagPropertyName);
            if (jcrTag != null) {
                containsTagFlag = tags.contains(jcrTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containsTagFlag;
    }


    /**
     * Get tag values from a Page - Tags in page property.
     *
     * @param tagManager
     * @param thisPage
     * @param tagPath
     * @return boolean
     */
    public static boolean containsTag(TagManager tagManager, Page thisPage, String tagPath) throws RepositoryException {
        boolean containsTagFlag = false;
        Tag jcrTag = tagManager.resolve(tagPath);
        try {
            Tag[] pageTags = thisPage.getTags();
            List<Tag> tags = Arrays.asList(pageTags);
            if (jcrTag != null) {
                containsTagFlag = tags.contains(jcrTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containsTagFlag;
    }


    /**
     * Get tags from a Page as a string - Tags in page property.
     *
     * @param thisPage
     * @return String
     */
    public static String getTagAsString(Page thisPage) throws RepositoryException {
        String tagString = "";
        Tag[] pageTags = thisPage.getTags();
        List<Tag> tags = Arrays.asList(pageTags);
        tagString = tagString + tags.size();
        for (Tag curTag : pageTags) {
            if (curTag != null) {
                tagString = tagString + "," + curTag.getTagID();
            }
        }
        return tagString;
    }

    /**
     * Create a formatted string that contains all tags attached to the provided details node.
     *
     * @param tagManager   The TagManager used to resolve tag names to objects.
     * @param node         The details node to get the information from.
     * @param tagName      The tags property name to get the information from.
     * @param defaultValue The value to return when something goes wrong.
     * @return The list of tags, formatted correctly for ICAL presentation.
     */
    public static String getTagsTitles(TagManager tagManager, Node node, String tagName, String defaultValue) {
        try {
            if (!node.hasProperty(tagName)) {
                return defaultValue;
            }

            Property tagProperty = node.getProperty(tagName);

            // has any contents?
            Value[] values = tagProperty.getValues();
            if (values == null || values.length == 0) {
                return defaultValue;
            }

            // get the tag names
            List<String> tagPaths = new ArrayList<String>();
            for (Value tagValue : values) {
                if (tagValue != null) {
                    tagPaths.add(tagValue.getString());
                }
            }

            return getTags(tagManager, tagPaths.toArray(new String[tagPaths.size()]));
        } catch (RepositoryException e) {
            return defaultValue;
        }
    }

    /***
     * get value of tag path as admin.
     * @param tagPath path or tagid of tag
     * @param sling sling helper
     * @return comma separated list of tag values
     */
    @SuppressWarnings("Duplicates")
    public static String getTagValueAsAdmin(String tagPath, SlingScriptHelper sling) {
        String tagValue = "";

        if (isEmpty(tagPath) || sling == null) {
            return tagValue;
        }

        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

            TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

            Tag jcrTag = getTag(tagPath, adminResourceResolver, _adminTagManager);

            if (jcrTag != null) {
                tagValue = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey(TAG_VALUE)) {
                        tagValue = tagVM.get(TAG_VALUE, jcrTag.getName());
                    }
                }
            } else {
                LOGGER.error("Could not find tag path: {}", tagPath);
            }

        } catch (Exception ex) {
            LOGGER.error("getTagValueAsAdmin: " + ex.getMessage(), ex);
            //out.write( Throwables.getStackTraceAsString(ex) );
        }

        return tagValue;
    }


    /**
     * Get list of Tags from a list of tag paths.
     * @param sling sling helper
     * @param tagPaths list of tags
     * @param locale locale to yse
     * @return map of tag values
     * @throws RepositoryException
     */
    public static LinkedHashMap<String, Map> getTagsAsAdmin(SlingScriptHelper sling, String[] tagPaths, Locale locale) {
        LinkedHashMap<String, Map> tags = new LinkedHashMap<String, Map>();

        if (sling == null || tagPaths == null || tagPaths.length == 0) {
            return tags;
        }


        ContentAccess contentAccess = sling.getService(ContentAccess.class);
        try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

            TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

            for (String path : tagPaths) {
                Map<String, String> tagValues = new HashMap<String, String>();

                Tag tag = getTag(path, adminResourceResolver, tagManager);

                if (tag != null) {
                    tagValues.put("title", tag.getTitle());
                    tagValues.put("description", tag.getDescription());
                    tagValues.put("path", tag.getPath());

                    ValueMap tagVM = tag.adaptTo(Resource.class).getValueMap();
                    String tagValue = tag.getName();

                    if (tagVM.containsKey(TAG_VALUE)) {
                        tagValue = tagVM.get(TAG_VALUE, tag.getName());
                    }

                    if (tagVM.containsKey(TAG_ISDEFAULT)) {
                        tagValue = tagVM.get(TAG_ISDEFAULT, TAG_ISDEFAULT_VALUE);
                    }

                    if (locale != null) {
                        String titleLocal = JcrConstants.JCR_TITLE.concat(".").concat(org.apache.jackrabbit.util.Text.escapeIllegalJcrChars(locale.toString().toLowerCase()));
                        if (tagVM.containsKey(titleLocal)) {
                            tagValues.put("title", tagVM.get(titleLocal, tag.getName()));
                        }
                        tagValues.put("tagid", tag.getLocalTagID());

                        String valueLocal = TAG_VALUE.concat(".").concat(org.apache.jackrabbit.util.Text.escapeIllegalJcrChars(locale.toString().toLowerCase()));
                        if (tagVM.containsKey(valueLocal)) {
                            tagValue = tagVM.get(valueLocal, tag.getName());
                        }
                    }

                    tagValues.put(TAG_VALUE, tagValue);

                    tags.put(tag.getTagID(), tagValues);
                }
            }

        } catch (Exception ex) {
            LOGGER.error("getTagValueAsAdmin: " + ex.getMessage(), ex);
        }

        return tags;
    }

    /**
     * get a string of tags values.
     * @param sling
     * @param separator
     * @param tagPaths
     * @return
     */
    public static String getTagsAsValuesAsAdmin(SlingScriptHelper sling, String separator, String tagPaths[]) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();

        for (String path : tagPaths) {

            String tagValue = getTagValueAsAdmin(path,sling);

            builder.append(tagValue);
            builder.append(separator);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }


        return builder.toString();
    }

    /**
     * Get Tag values.
     * @param tagManager tag manager
     * @param tagPaths list of tags
     * @return comma separated list of tag values
     */
    @SuppressWarnings("Duplicates")
    public static String getTagsAsValues(TagManager tagManager, ResourceResolver resourceResolver, String separator, String tagPaths[]) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        int idx = 0;
        StringBuilder builder = new StringBuilder();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey(TAG_VALUE)) {
                        value = tagVM.get(TAG_VALUE, jcrTag.getName());
                    }
                }

                builder.append(value);
                builder.append(separator);
            }
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        // return buffer
        return builder.toString();
    }

    /**
     * Get Tag values.
     * @param tagManager tag manager
     * @param tagPaths list of tags
     * @return string array of tag values
     */
    @SuppressWarnings("Duplicates")
    public static String[] getTagsValues(TagManager tagManager, ResourceResolver resourceResolver, String separator, String tagPaths[]) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }

        int idx = 0;
        ArrayList<String> tagValues = new ArrayList<String>();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

                if (tagVM != null) {
                    if (tagVM.containsKey(TAG_VALUE)) {
                        value = tagVM.get(TAG_VALUE, jcrTag.getName());
                    }
                }
                tagValues.add(value);
            }
        }
        // return buffer
        return tagValues.toArray(new String[tagValues.size()]);
    }

    /**
     * Get Tag values.
     * @param tagManager tag manager
     * @param tagPaths list of tags
     * @return space separated list of tag titles
     */
    @SuppressWarnings("Duplicates")
    public static String getTagsAsKeywords(TagManager tagManager, String separator, String tagPaths[], Locale locale) {
        if (tagPaths == null || tagPaths.length == 0) {
            return null;
        }
        //TODO: maybe exclude some namespaces out of results?
        int idx = 0;
        StringBuilder builder = new StringBuilder();

        for (String path : tagPaths) {
            Tag jcrTag = tagManager.resolve(path);
            if (jcrTag != null) {
                String value = jcrTag.getName();

                String title = jcrTag.getTitle(locale);

                if (isNotEmpty(title)) {
                    value = title;
                }

                builder.append(value);
                builder.append(separator);
            }
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }

        // return buffer
        return builder.toString();
    }


    /**
     * return Tag Names as Class String.
     * @param tag list of tags
     * @return space separated list of tag names
     */
    @SuppressWarnings("Duplicates")
    public static String getTagsAsClasses(String tag[]) {
        String cssTagClass = "";

        for (int i = 0; i < tag.length; i++) {
            if (i > 0) {
                cssTagClass = cssTagClass + " ";
            }
            cssTagClass = cssTagClass + tag[i].substring(tag[i].lastIndexOf(":") + 1);
        }

        return cssTagClass;
    }


    /**
     * Returns the value of the property on the specified node with the specified tag.
     *
     * @param node         is the node to inspect
     * @param tagName      is the name of the tag
     * @param propertyName is the name of the property
     * @param defaultValue is the default value if the property is not found
     * @return the value of the property as a String
     */
    public static String getPropertyFromTag(TagManager tagManager, Node node, String tagName, String propertyName, String defaultValue) {

        try {
            List<Tag> tagList = getTags(tagManager, node, tagName);
            if (tagList != null) {
                for (Tag tag : tagList) {
                    Node tagNode = tag.adaptTo(Node.class);
                    if (tagNode.hasProperty(propertyName)) {
                        return tagNode.getProperty(propertyName).getValue().getString();
                    }
                }
            }
            return defaultValue;
        } catch (RepositoryException e) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the property on the specified node with the specified tag.
     *
     * @param node is the node to inspect
     * @param tag  is the tag
     * @return the value of the property as a String Array of the tag titles
     */
    public static List<Tag> getChildTags(TagManager tagManager, Node node, Tag tag) {

        List<Tag> children = new ArrayList<Tag>();

        for (Iterator<Tag> tagList = tag.listChildren(); tagList.hasNext(); ) {
            children.add(tagList.next());
        }

        return children;

    }


    /**
     * return tag object from path uses tag manager to resolve and resource resolver as backup.
     *
     * @param path             tag from apth path or from tagId path
     * @param resourceResolver
     * @param tagManager
     * @return return null or Tag
     */
    public static Tag getTag(String path, ResourceResolver resourceResolver, TagManager tagManager) {

        boolean possibleTagId = path.contains(String.valueOf(TagConstants.NAMESPACE_DELIMITER_CHR));
        String tagPath = path;
        if (possibleTagId) {
            Tag tag = tagManager.resolve(path);
            if (tag != null) {
                return tag;
            } else {
                //second chance to manually find the tag
                //TODO: detect tennant TAG path
                tagPath = getPathFromTagId(path, ComponentsUtil.DEFAULT_PATH_TAGS);
            }

        }

        if (isNotEmpty(tagPath)) {
            Resource rs = resourceResolver.resolve(tagPath);
            if (rs != null) {
                return rs.adaptTo(Tag.class);
            }
        }
        return null;
    }

    /**
     * manually resolve path to tag as OOTB does not have tennant support.
     *
     * @param tagID    tagId path  [namespace]:[namespace/path/to/tag]
     * @param tagsRoot root for tags default /content/cq:tags
     * @return tag id
     */
    public static String getPathFromTagId(String tagID, String tagsRoot) {

        int colonPos = tagID.indexOf(TagConstants.NAMESPACE_DELIMITER_CHR);
        String namespace;
        String localID;
        if (colonPos > 0) {
            namespace = tagID.substring(0, colonPos);
            localID = tagID.substring(colonPos + 1);
        } else if (colonPos == 0) {
            namespace = TagConstants.DEFAULT_NAMESPACE;
            localID = tagID.substring(1);
        } else {
            namespace = TagConstants.DEFAULT_NAMESPACE;
            localID = tagID;
        }

        if (localID.endsWith(TagConstants.SEPARATOR)) {
            localID = localID.substring(0, localID.length() - 1);
        } else if (localID.length() == 0) {
            return tagsRoot + TagConstants.SEPARATOR + namespace;
        }

        return tagsRoot + TagConstants.SEPARATOR + namespace + TagConstants.SEPARATOR + localID;
    }


    /***
     * return page tags and don't error.
     * @param page page to use
     * @return list of tags
     */
    public static com.day.cq.tagging.Tag[] getPageTags(Page page) {
        return getPageTags(page, new Tag[]{});
    }

    /***
     * return page tags and don't error.
     * @param page page to use
     * @param defaultTags default tags to return if none found
     * @return list of tags
     */
    public static com.day.cq.tagging.Tag[] getPageTags(Page page, com.day.cq.tagging.Tag[] defaultTags) {

        if (page == null) {
            return defaultTags;
        }

        try {
            return page.getTags();
        } catch (Exception ex) {
            LOGGER.error("could not read page tags {}", page);
        }

        return new Tag[]{};
    }
}
