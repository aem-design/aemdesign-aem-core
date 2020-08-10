package design.aem.models.v2.lists;

public class PageList extends List {
    static {
        COMPONENT_LIST_NAME = "pagelist";

        DETAILS_COMPONENT_LOOKUP_NAMES = new String[]{
            "page-details",
            "generic-details",
        };
    }
}
