package design.aem.aem.components;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import design.aem.aem.helper.TagHelper;
import design.aem.aem.models.ArticleDetails;

import java.util.List;

/**
 * Implementation for the BadgeImageTitleTags Component.
 *
 * @author yawly
 * @date 19.05.2016
 */
public class BadgeImageTitleTags extends AbstractComponent {

    protected void init() {
        // Change the current page to the one from the request.
        Page currentPage = (Page) getSlingRequest().getAttribute("badgePage");
        super.setPage(currentPage);
    }

    public ArticleDetails getArticleDetails() {
        return getCurrentPage().getContentResource(ArticleDetails.DEFAULT_CONTENT_PATH).adaptTo(ArticleDetails.class);
    }

    public Page getArticleDetailsPage() {
        return getArticleDetails().getArticlePage();
    }

    public List<Tag> getTags() {
        return TagHelper.getTagsForResource(getCurrentPage().getContentResource());
    }

    public String getCurrentPath() {
        return getCurrentPage().getPath();
    }

}
