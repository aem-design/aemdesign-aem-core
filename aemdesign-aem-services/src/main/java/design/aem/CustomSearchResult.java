package design.aem;

public class CustomSearchResult {
    private String extension;
    private String title;
    private String subTitle;
    private String pathUrl;
    private String path;
    private String imgResource;
    private String excerpt;

    private Boolean damAsset;
    private Boolean pageDetails;

    public CustomSearchResult(String path) {
        this.path = path;
    }

    public Boolean getPageDetails() {
        return pageDetails;
    }

    public void setPageDetails(Boolean newPageDetails) {
        pageDetails = newPageDetails;
    }

    public Boolean getDamAsset() {
        return damAsset;
    }

    public void setDamAsset(Boolean newDamAsset) {
        damAsset = newDamAsset;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String newExcerpt) {
        excerpt = newExcerpt;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extensionType) {
        extension = extensionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String newSubTitle) {
        subTitle = newSubTitle;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String newPathUrl) {
        pathUrl = newPathUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String newPath) {
        path = newPath;
    }

    public String getImgResource() {
        return imgResource;
    }

    public void setImgResource(String newImgResource) {
        imgResource = newImgResource;
    }
}