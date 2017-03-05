<%@ page import="com.day.cq.tagging.Tag" %>
<%@ page import="com.day.cq.tagging.TagManager" %>
<%@ page import="java.util.*" %>
<%!

    /**
     * @return a list of all the months
     */
    private List<String> getMonthsList() {
        return Arrays.asList(
                "January", "February", "March", "April",
                "May", "June", "July", "August", "September",
                "October", "November", "December"
        );
    }

    /**
     * This function builds a map of the categories that have been activated in the
     * component's dialog settings.
     *
     * @return an ordered hash map
     */
    public Map<String, String> getActivatedCategories(TagManager tagManager, String[] tags, String defaultLabel) {
        Map<String, String> categoryTags = new LinkedHashMap<String, String>();
        for (String tagString : tags) {
            Tag tag = tagManager.resolve(tagString);
            if (tag == null) {
                continue;
            }

            categoryTags.put(tag.getName(), tag.getTitle());
        }
        return categoryTags;
    }

    /**
     * Creates a list of year numbers from the minYear to currentYear + 1
     *
     * @param minYear is the first year to allow searching for
     * @return a list of strings
     */
    private List<String> getYearList(int minYear) {
        int maxYear = new Date().getYear() + 1900 + 1;

        // shouldn't happen.
        if (maxYear < minYear) {
            return null;
        }

        // range (maxYear .. minYear)
        List<String> years = new ArrayList<String>();
        for (int year = maxYear; year >= minYear; --year) {
            years.add(Integer.toString(year));
        }

        return years;
    }
%>