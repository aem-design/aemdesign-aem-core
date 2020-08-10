package design.aem.models.v2.lists;

public class NewsList extends List {
    static {
        COMPONENT_LIST_NAME = "newslist";

        DETAILS_COMPONENT_LOOKUP_NAMES = new String[]{
            "news-details",
            "generic-details",
        };
    }
}
