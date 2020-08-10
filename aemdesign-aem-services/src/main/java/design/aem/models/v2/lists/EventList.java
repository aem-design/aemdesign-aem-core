package design.aem.models.v2.lists;

public class EventList extends List {
    static {
        COMPONENT_LIST_NAME = "eventlist";

        DETAILS_COMPONENT_LOOKUP_NAMES = new String[]{
            "event-details",
            "generic-details",
        };
    }

    @Override
    protected void registerListFeedTypes() {
        super.registerListFeedTypes();

        if (Boolean.FALSE.equals(currentStyle.get("disableFeedTypeICS", false))) {
            listFeeds.put("ics", new ListFeed(".ics", "iCalendar Subscription List", "text/calendar"));
        }
    }
}
