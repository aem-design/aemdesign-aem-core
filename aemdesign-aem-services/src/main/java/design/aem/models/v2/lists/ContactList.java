package design.aem.models.v2.lists;

public class ContactList extends List {
    @Override
    protected void ready() {
        detailsNameSuffix = new String[]{
            "contact-details",
            "generic-details",
        };

        super.ready();
    }

    @Override
    protected String getComponentCategory() {
        return "contactlist";
    }
}
