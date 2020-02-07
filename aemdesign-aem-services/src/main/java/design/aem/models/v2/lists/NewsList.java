package design.aem.models.v2.lists;

import com.day.cq.i18n.I18n;

import static design.aem.utils.components.I18nUtil.*;

public class NewsList extends List {

    private final String DEFAULT_I18N_CATEGORY = "newslist";

    @Override
    @SuppressWarnings("Duplicates")
    protected void ready() {
        I18n i18n = new I18n(getRequest());

        detailsNameSuffix = new String[]{"news-details", "generic-details"};

        loadConfig();

        //override properties
        getComponentProperties().put(LISTITEM_LINK_TEXT, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TEXT,DEFAULT_I18N_CATEGORY,i18n));
        getComponentProperties().put(LISTITEM_LINK_TITLE, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TITLE,DEFAULT_I18N_CATEGORY,i18n));
    }
}