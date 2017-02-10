package design.aem.aem.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import design.aem.aem.components.CommonUtil;
import javax.jcr.RepositoryException;

/**
 * Created by yawly on 5/05/2016.
 */
@Model(adaptables=Resource.class)
public class IssueDetails {

    public final static String DEFAULT_CONTENT_PATH = "article/par/issueDetails";

    public final static String DEFAULT_ATTRIBUTE_NAME = "issueDetails";

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
    @Named("displayStyle")
    @Optional
    private String displayStyle;

    @Inject
    @Named("description")
    @Optional
    private String description;

    @Inject
    @Named("namespace")
    @Optional
    private String namespace;


    @Inject
    @Named("issueDetailsType")
    @Optional
    private String issueDetailsType;

    @Inject
    @Named("blockType")
    @Optional
    private String blockType;

    @Inject
    @Named("componentModifiers")
    @Optional
    private String[] componentModifiers;

    private final PageManager pageManager;

    private final Page issuePage;

    private PageTagList pageTagList;

    public IssueDetails(Resource resource) {
        this.resource = resource == null ? null : resource;

        pageManager =  this.resource.getResourceResolver().adaptTo(PageManager.class);

        issuePage = pageManager.getContainingPage(this.resource);
    }

    @PostConstruct
    protected void addDependencies() {
        pageTagList = new PageTagList(resource, namespace);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public String getDisplayStyle() {
        return displayStyle;
    }

    public void setDisplayStyle(String displayStyle) {
        this.displayStyle = displayStyle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getIssueDetailsType() {
        return issueDetailsType;
    }

    public void setIssueDetailsType(String issueDetailsType) {
        this.issueDetailsType = issueDetailsType;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String[] getComponentModifiers() {
        return componentModifiers;
    }

    public void setComponentModifiers(String[] componentModifiers) {
        this.componentModifiers = componentModifiers;
    }

    public Resource getResource() {
        return resource;
    }

    public Page getIssuePage() {
        return issuePage;
    }

    public PageTagList getPageTagList() {
        return pageTagList;
    }

    public String getTrackprogress() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("title", title == null ? "" : title);
            jsonObject.put("issue", issuePage == null ? "" : "Issue " + CommonUtil.currentPageNumberByContentPath(issuePage,DEFAULT_CONTENT_PATH));
            jsonObject.put("link",  issuePage == null ? "" : issuePage.getPath() + ".html");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            return "";
        }

        return jsonObject.toString();
    }
}
