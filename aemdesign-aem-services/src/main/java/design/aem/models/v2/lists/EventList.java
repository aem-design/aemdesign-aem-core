package design.aem.models.v2.lists;

public class EventList extends List {
    @Override
    protected void ready() {
        detailsNameSuffix = new String[]{
            "event-details",
            "generic-details",
        };

        super.ready();
    }

    @Override
    protected String getComponentCategory() {
        return "eventlist";
    }
}
