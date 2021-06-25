package design.aem.models.v2.lists;

public class NewsList extends List {
    @Override
    @SuppressWarnings("Duplicates")
    protected void ready() {
        detailsNameSuffix = new String[]{
            "news-details",
            "generic-details",
        };

        super.ready();
    }

    @Override
    protected String getComponentCategory() {
        return "newslist";
    }
}
