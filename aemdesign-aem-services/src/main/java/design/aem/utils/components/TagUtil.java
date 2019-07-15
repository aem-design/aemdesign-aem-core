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
        if (contentAccess != null) {
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                TagManager _adminTagManager = adminResourceResolver.adaptTo(TagManager.class);

                Tag jcrTag = getTag(tagPath, adminResourceResolver, _adminTagManager);

                if (jcrTag != null) {
                    tagValue = jcrTag.getName();

                    Resource jcrTagResource = jcrTag.adaptTo(Resource.class);

                    if (jcrTagResource != null) {
                        ValueMap tagVM = jcrTagResource.getValueMap();

                        if (tagVM != null) {
                            if (tagVM.containsKey(TAG_VALUE)) {
                                tagValue = tagVM.get(TAG_VALUE, jcrTag.getName());
                            }
                        }
                    } else {
                        LOGGER.error("getTagValueAsAdmin: could not convert tag to Resource, jcrTag={}", jcrTag);
                    }
                } else {
                    LOGGER.error("getTagValueAsAdmin: Could not find tag path: {}", tagPath);
                }

            } catch (Exception ex) {
                LOGGER.error("getTagValueAsAdmin: {}", ex);
                //out.write( Throwables.getStackTraceAsString(ex) );
            }
        } else {
            LOGGER.error("getTagValueAsAdmin: could not get ContentAccess service.");
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
        if (contentAccess != null) {
            try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);

                for (String path : tagPaths) {
                    Map<String, String> tagValues = new HashMap<String, String>();

                    Tag tag = getTag(path, adminResourceResolver, tagManager);

                    if (tag != null) {
                        tagValues.put("title", tag.getTitle());
                        tagValues.put("description", tag.getDescription());
                        tagValues.put("path", tag.getPath());

                        Resource tagResource = tag.adaptTo(Resource.class);
                        if (tagResource != null) {
                            ValueMap tagVM = tagResource.getValueMap();
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
                        } else {
                            LOGGER.error("getTagsAsAdmin: could not get convert tag to Resource, tag={}", tag);
                        }

                        tags.put(tag.getTagID(), tagValues);
                    }
                }

            } catch (Exception ex) {
                LOGGER.error("getTagsAsAdmin: {}", ex);
            }
        } else {
            LOGGER.error("getTagsAsAdmin: could not get ContentAccess service.");
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

                Resource jcrTagResource = jcrTag.adaptTo(Resource.class);

                if (jcrTagResource != null) {
                    ValueMap tagVM = jcrTagResource.getValueMap();

                    if (tagVM != null) {
                        if (tagVM.containsKey(TAG_VALUE)) {
                            value = tagVM.get(TAG_VALUE, jcrTag.getName());
                        }
                    }

                    builder.append(value);
                    builder.append(separator);
                } else {
                    LOGGER.error("getTagsAsValues: could not convert tag to Resource, jcrTag={}", jcrTag);
                }
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

                Resource jcrTagResource = jcrTag.adaptTo(Resource.class);

                if (jcrTagResource != null) {
                    ValueMap tagVM = jcrTagResource.getValueMap();

                    if (tagVM != null) {
                        if (tagVM.containsKey(TAG_VALUE)) {
                            value = tagVM.get(TAG_VALUE, jcrTag.getName());
                        }
                    }
                    tagValues.add(value);
                } else {
                    LOGGER.error("getTagsAsValues: could not convert tag to Resource, jcrTag={}", jcrTag);
                }
            }
        }
        // return buffer
        return tagValues.toArray(new String[tagValues.size()]);
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
