package design.aem.models.v2.lists;

public class PageList extends List {
    @Override
    protected String getComponentName() {
        return "pagelist";
    }

    @Override
    protected String[] getDetailsComponentLookupNames() {
        return new String[]{"page-details", "generic-details"};
    }
}
