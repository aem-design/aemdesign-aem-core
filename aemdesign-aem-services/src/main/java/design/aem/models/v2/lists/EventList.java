package design.aem.models.v2.lists;

public class EventList extends List {
    @Override
    protected String getComponentName() {
        return "eventlist";
    }

    @Override
    protected String[] getDetailsComponentLookupNames() {
        return new String[]{"event-details", "generic-details"};
    }

    @Override
    protected void registerListFeedTypes() {
        super.registerListFeedTypes();

        if (Boolean.FALSE.equals(currentStyle.get("disableFeedTypeICS", false))) {
            listFeeds.put("ics", new ListFeed(".ics", "iCalendar Subscription List", "text/calendar"));
        }
    }
}
