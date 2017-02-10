package design.aem.aem.models;

import com.day.cq.wcm.api.PageManager;
import design.aem.aem.components.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;

/**
 * Created by yawly on 5/05/2016.
 */
@Model(adaptables=Resource.class)
public class PersonDetails {

    public static final String DETAILS_PATH = "article/par/personDetails";

    public final static String DEFAULT_ATTRIBUTE_NAME = "personDetails";

    private static final String KEY_CONTENT_PATH = "path";

    final String DEFAULT_FORMAT_TITLE = "${title}";

    @Inject
    @Named("title")
    @Optional
    private String title;

    @Inject
    @Named("secondaryLanguage")
    @Optional
    private String secondaryLanguage;

    @Inject
    @Named("disableLinking")
    @Optional
    private Boolean disableLinking;

    @Inject
    @Named("displayFormat")
    @Optional
    private String displayFormat;

    @Inject
    @Named("description")
    @Optional
    private String description;

    private final Resource resource;

    public PersonDetails(Resource resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getDisableLinking() {
        return disableLinking;
    }

    public void setDisableLinking(Boolean disableLinking) {
        this.disableLinking = disableLinking;
    }

    public String getDisplayFormat() {
        return displayFormat;
    }

    public void setDisplayFormat(String displayFormat) {
        this.displayFormat = displayFormat;
    }

    public String getFormattedText() {

        if(resource != null && StringUtils.isNotBlank(displayFormat)) {
            PageManager pageManager = this.resource.getResourceResolver().adaptTo(PageManager.class);

            HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.putAll(this.resource.getValueMap());

            properties.put(KEY_CONTENT_PATH, pageManager.getContainingPage(this.resource).getPath());

            String formattedText = "";
            try {
                formattedText = CommonUtil.compileSubstituteMessage(displayFormat, DEFAULT_FORMAT_TITLE, properties, resource);
            } catch (Exception ex) {
                Logger LOG = LoggerFactory.getLogger(getClass());
                LOG.error("getFormattedText: " + ex.getMessage(), ex);
            }
            return formattedText;
        }

        return "";
    }

    public String getPath() {
        if (resource != null) {
            PageManager pageManager = this.resource.getResourceResolver().adaptTo(PageManager.class);
            return pageManager.getContainingPage(this.resource).getPath();
        }
        return "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }
}
