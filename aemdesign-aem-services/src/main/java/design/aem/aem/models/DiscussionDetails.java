package design.aem.aem.models;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.aem.helper.TagHelper;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Model for the DiscussionDetails.
 *
 * @author yawly
 * @date 08.06.2016
 */
@Model(adaptables = Resource.class)
public class DiscussionDetails {


    public final static String DEFAULT_ATTRIBUTE_NAME = "discussionDetails";

    private final Resource resource;

    @Inject
    @Named("title")
    @Optional
    private String title;

    @Inject
    @Named("secondaryLanguage")
    @Optional
    private String secondaryLanguage;

    @Inject
    @Named("namespace")
    @Optional
    private String namespace;

    @Inject
    @Named("fileReference")
    @Optional
    private String imageFileReference;

    @Inject
    @Named("description")
    @Optional
    private String description;

    private Page containingPage;


    public DiscussionDetails(Resource resource) {
        this.resource = resource;
    }

    @PostConstruct
    protected void addDependencies() {
        if (resource != null) {
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
            this.containingPage = pageManager.getContainingPage(resource);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Page getContainingPage() {
        return containingPage;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Resource getResource() {
        return resource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageFileReference() {
        return imageFileReference;
    }

    public void setImageFileReference(String imageFileReference) {
        this.imageFileReference = imageFileReference;
    }

    /**
     * Get Tags which are stored on the DiscussionDetails component.
     *
     * @return List<Tag>
     */
    public List<Tag> getDiscussionDetailsTags() {
        return TagHelper.getTagsForResource(this.resource);
    }

    /**
     * Get Tags as a JSON Object.
     *
     * @return String
     */
    public String getTagsJson() {
        return TagHelper.getTagsJson(getDiscussionDetailsTags());
    }

    /**
     * Get the current ComponentStyle.
     *
     * @return ComponentStyle
     */
    public ComponentStyle getComponentStyle() {
        return this.resource.adaptTo(ComponentStyle.class);
    }
}
