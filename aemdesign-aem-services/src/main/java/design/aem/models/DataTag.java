package design.aem.models;

/**
 * Created by yawly on 17/05/16.
 */
public class DataTag {

    private String title;

    private String url;

    public DataTag(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public DataTag() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
