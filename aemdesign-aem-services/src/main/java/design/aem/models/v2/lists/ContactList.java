package design.aem.models.v2.lists;

public class ContactList extends List {
    static {
        COMPONENT_LIST_NAME = "contactlist";

        DETAILS_COMPONENT_LOOKUP_NAMES = new String[]{
            "contact-details",
            "generic-details",
        };
    }
}
