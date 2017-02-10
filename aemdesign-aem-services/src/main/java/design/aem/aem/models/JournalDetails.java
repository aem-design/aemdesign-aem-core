package design.aem.aem.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by yawly on 5/05/2016.
 */
@Model(adaptables=Resource.class)
public class JournalDetails {

    public final static String DEFAULT_CONTENT_PATH = "article/par/journalDetails";

    public final static String DEFAULT_ATTRIBUTE_NAME = "journalDetails";

    private final Resource resource;

    private PersonDetails author;

    @Inject
    @Named("title")
    @Optional
    private String title;

    @Inject
    @Named("secondaryLanguage")
    @Optional
    private String secondaryLanguage;

    @Inject
    @Named("authorPath")
    @Optional
    private String authorPath;

    public JournalDetails(Resource resource) {
        this.resource = resource == null ? null : resource;
    }

    @PostConstruct
    protected void addDependencies() {

    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public PersonDetails getAuthor() {
        return author;
    }

    public void setAuthor(PersonDetails author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
