package design.aem.aem.models;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import design.aem.aem.helper.TagHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by yawly on 5/05/2016.
 */
@Model(adaptables = Resource.class)
public class ArticleDetails {

    public final static String DEFAULT_CONTENT_PATH = "article/par/articleDetails";

    public final static String DEFAULT_ATTRIBUTE_NAME = "articleDetails";

    private final static String PROPERTY_CONTRIBUTORS = "contributors";

    private final Resource resource;

    private List<PersonDetails> contributorsDetails;

    @Inject
    @Named("title")
    @Optional
    private String title;

    @Inject
    @Named("secondaryLanguage")
    @Optional
    private String secondaryLanguage;

    @Inject
    @Named("contributors")
    @Optional
    private String[] contributors;

    @Inject
    @Named("subSectionLevel")
    @Optional
    private Integer subSectionLevel;

    @Inject
    @Named("sectionLevel")
    @Optional
    private Integer sectionLevel;

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

    private Page articlePage;

    private PageTagList pageTagList;

    public ArticleDetails(Resource resource) {
        this.resource = resource;
        this.contributorsDetails = new DetailsList(resource, PROPERTY_CONTRIBUTORS, PersonDetails.DETAILS_PATH)
                .toList(PersonDetails.class);
    }

    @PostConstruct
    protected void addDependencies() {
        if (resource != null) {
            PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
            Page containingPage = pageManager.getContainingPage(resource);

            this.articlePage = containingPage;

            pageTagList = new PageTagList(resource, namespace);
        }

    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public Page getArticlePage() {
        return articlePage;
    }

    public void setArticlePage(Page articlePage) {
        this.articlePage = articlePage;
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

    public String[] getContributors() {
        return contributors;
    }

    public void setContributors(String[] contributors) {
        this.contributors = contributors;
    }

    public Resource getResource() {
        return resource;
    }

    private Page getParentResourcePage(String parentResourcePath) {

        Page parentPage = null;

        if (StringUtils.isNotEmpty(parentResourcePath)) {

            // traverse the content tree upwards until you find the parent resource
            parentPage = articlePage.getParent();

            while (parentPage != null) {
                Resource parentResource = parentPage.getContentResource(parentResourcePath);
                if (parentResource != null) {
                    return parentPage;
                }
                parentPage = parentPage.getParent();
            }
        }

        return parentPage;
    }

    public Integer getSubSectionLevel() {
        return subSectionLevel;
    }

    public void setSubSectionLevel(Integer subSectionLevel) {
        this.subSectionLevel = subSectionLevel;
    }

    public Integer getSectionLevel() {
        return sectionLevel;
    }

    public void setSectionLevel(Integer sectionLevel) {
        this.sectionLevel = sectionLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PersonDetails> getContributorsDetails() {
        return contributorsDetails;
    }

    public void setContributorsDetails(List<PersonDetails> contributorsDetails) {
        this.contributorsDetails = contributorsDetails;
    }

    /**
     * Get Tags for the page where this component is embedded inside.
     *
     * @return PageTagList
     */
    public PageTagList getPageTagList() {
        return pageTagList;
    }

    public String getImageFileReference() {
        return imageFileReference;
    }

    public void setImageFileReference(String imageFileReference) {
        this.imageFileReference = imageFileReference;
    }

    /**
     * Get Tags which are stored on the ArticleDetails component.
     *
     * @return List<Tag>
     */
    public List<Tag> getArticleDetailsTags() {
        return TagHelper.getTagsForResource(this.resource);
    }

    /**
     * Get Tags as a JSON Object.
     *
     * @return String
     */
    public String getTagsJson() {
        return TagHelper.getTagsJson(getArticleDetailsTags());
    }
}
