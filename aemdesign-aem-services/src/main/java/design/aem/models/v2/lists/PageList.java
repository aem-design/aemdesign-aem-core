package design.aem.models.v2.lists;

public class PageList extends List {
    @Override
    protected void ready() {
        detailsNameSuffix = new String[]{
            "page-details",
            "generic-details",
        };

        super.ready();
    }

    @Override
    protected String getComponentCategory() {
        return "pagelist";
    }
}
