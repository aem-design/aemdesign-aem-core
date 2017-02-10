package design.aem.aem.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;


/**
 * Created by yawly on 5/15/2016.
 */
public class PageTagList {

    Logger LOG = LoggerFactory.getLogger(getClass());

    private final Resource resource;

    final String PAGE_TAG_NAMESPACE = "content-type:page";

    final String PN_TAGS = "cq:tags";

    private String namespace;

    private Tag displayTag;

    private String pageTag;

    private List<Tag> cqTags;

    private ArrayList<DataTag> dataTags;

    public PageTagList(Resource resource, String namespace) {
        this.resource = resource;
        this.namespace = namespace;

        if(resource != null) {
            TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
            Page containingPage = pageManager.getContainingPage(resource);


            // get cq:tags via a matching namespace
            this.displayTag = tagManager.resolve(this.namespace);
            cqTags = this.getTagsByNamespace(tagManager, this.resource, this.displayTag);
            if (cqTags != null) {
                dataTags = new ArrayList<DataTag>(cqTags.size());
                for (Tag tag : cqTags) {
                    DataTag dataTag = new DataTag();
                    dataTag.setTitle(tag.getTitle());
                    dataTag.setUrl(tag.getName().replace("_", "-"));
                    dataTags.add(dataTag);
                }
            }

            // get section from the page tag namespace
            Tag pageTagNamespace = tagManager.resolve(PAGE_TAG_NAMESPACE);
            List<Tag> pageTags = this.getTagsByNamespace(tagManager, containingPage.getContentResource(), pageTagNamespace);

            if(pageTags != null && !pageTags.isEmpty()) {
                pageTag = pageTags.get(0).getTitle();
            }
        }
    }

    public List<Tag> getTagsByNamespace(TagManager tagManager, Resource resource, Tag namespaceTag) {
        if(resource == null) {
            LOG.debug("Failed to retrieve tags from null resource. ");

            return null;
        }

        if(tagManager == null) {
            LOG.debug("Failed to retrieve Tag Manager. ");

            return null;
        }

        ArrayList<Tag> filteredTags = new ArrayList<Tag>();

        try {
            List<Tag> tags = getTags(tagManager, resource, PN_TAGS);

            if (namespaceTag != null && tags != null) {
                String namespace = namespaceTag.getTagID();

                for (Tag tag : tags) {
                    if (StringUtils.startsWith(tag.getTagID(), namespace)) {
                        filteredTags.add(tag);
                    }
                }

            } else {
                if(tags != null) {
                    filteredTags.addAll(tags);
                }
            }
        } catch(RepositoryException exception) {
            LOG.error("Error occurred while retrieving tags for Resource '%s'",
                    resource.getPath(),
                    exception);
        }

        return filteredTags;
    }

    private List<Tag> getTags(TagManager tagManager, Resource resource, String tagPropertyName) throws RepositoryException {
        if (tagManager==null || resource==null) {
            return null;
        }

        String[] pageTagValues = null;
        ValueMap properties = resource.getValueMap();

        pageTagValues = properties.get(tagPropertyName, String[].class);

        if (pageTagValues == null || pageTagValues.length == 0) {
            return null;
        }

        List<Tag> tags = new ArrayList<Tag>();
        for (String tagValue : pageTagValues) {
            if (tagValue != null) {
                Tag jcrTag = tagManager.resolve(tagValue);
                if (jcrTag != null) {
                    tags.add(jcrTag);
                }
            }
        }

        return tags;
    }


    public Resource getResource() {
        return resource;
    }

    public Tag getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Tag displayTag) {
        this.displayTag = displayTag;
    }

    public String getPageTag() {
        return pageTag;
    }

    public void setPageTag(String pageTag) {
        this.pageTag = pageTag;
    }

    public List<Tag> getCqTags() {
        return cqTags;
    }

    public void setCqTags(List<Tag> cqTags) {
        this.cqTags = cqTags;
    }

    public int getSize() {
        return this.cqTags.size();
    }

    public String getTagsJson() {
        Gson gson = new Gson();
        return escapeHtml4(gson.toJson(dataTags));
    }
}