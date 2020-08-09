package design.aem.models.v2.lists;

public class PageList extends List {
    static {
        DETAILS_COMPONENT_LOOKUP_NAMES = new String[]{
            "page-details",
            "generic-details",
        };
    }


    private final String DEFAULT_I18N_CATEGORY = "pagelist";

//    @Override
//    @SuppressWarnings("Duplicates")
//    public void ready() {
//        I18n i18n = new I18n(getRequest());
//
////        loadConfig();
//
//        //override properties
//        getComponentProperties().put(LISTITEM_LINK_TEXT, getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TEXT, DEFAULT_I18N_CATEGORY, i18n));
//        getComponentProperties().put(LISTITEM_LINK_TITLE, getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LIST_ITEM_LINK_TITLE, DEFAULT_I18N_CATEGORY, i18n));
//    }
}
