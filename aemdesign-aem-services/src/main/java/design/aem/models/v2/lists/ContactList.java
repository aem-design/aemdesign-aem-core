package design.aem.models.v2.lists;

public class ContactList extends List {
    @Override
    protected String getComponentName() {
        return "contactlist";
    }

    @Override
    protected String[] getDetailsComponentLookupNames() {
        return new String[]{"contact-details", "generic-details"};
    }
}
