package design.aem.helper;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import design.aem.models.DataTag;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 * Helper class for handling Tags.
 *
 * @author yawly
 * @date 07.06.2016
 */
public class TagHelper {

    /**
     * Load all Tags for a specific resource.
     *
     * @param aResource Resource
     * @return List<Tag>
     */
    public static List<Tag> getTagsForResource(Resource aResource) {
        List<Tag> retList = new ArrayList<Tag>();
        if (aResource == null) {
            return retList;
        }

        TagManager tagManager = aResource.getResourceResolver().adaptTo(TagManager.class);
        Tag[] tags = tagManager.getTags(aResource);
        Collections.addAll(retList, tags);
        return retList;
    }

    /**
     * Transforms a List<Tag> into a List<DataTag>.
     *
     * @param tags List<Tag>
     * @return ArrayList<DataTag>
     */
    public static ArrayList<DataTag> createDataTags(List<Tag> tags) {
        ArrayList<DataTag> dataTags = new ArrayList<DataTag>();
        for (Tag aTag : tags) {
            dataTags.add(new DataTag(aTag.getTitle(), aTag.getPath()));
        }

        return dataTags;
    }

    /**
     * Get Tags as a JSON Object.
     *
     * @return String
     */
    public static String getTagsJson(List<Tag> tags) {
        Gson gson = new Gson();
        return escapeHtml4(gson.toJson(TagHelper.createDataTags(tags)));
    }
}
