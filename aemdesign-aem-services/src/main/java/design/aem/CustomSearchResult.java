package design.aem;

public class CustomSearchResult {
    private String title;
    private String subTitle;
    private String pathUrl;
    private String path;
    private String thumbnailUrl;
    private String excerpt;

    private Boolean isAsset;
    private Boolean isPage;
    private String detailsPath;

    public CustomSearchResult(String path) {
        this.path = path;
    }

    public Boolean getIsPage() {
        return this.isPage;
    }

    public void setIsPage(Boolean isPage) {
        this.isPage = isPage;
    }

    public Boolean getIsAsset() {
        return this.isAsset;
    }

    public void setIsAsset(Boolean isAsset) {
        this.isAsset = isAsset;
    }

    public String getExcerpt() {
        return this.excerpt;
    }

    public void setExcerpt(String newExcerpt) {
        this.excerpt = newExcerpt;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String newSubTitle) {
        this.subTitle = newSubTitle;
    }

    public String getPathUrl() {
        return this.pathUrl;
    }

    public void setPathUrl(String newPathUrl) {
        this.pathUrl = newPathUrl;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }


    public String getDetailsPath() {
        return this.detailsPath;
    }

    public void setDetailsPath(String detailsPath) {
        this.detailsPath = detailsPath;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}