package design.aem.models.v2.lists;

import com.day.cq.i18n.I18n;
import design.aem.components.ComponentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.I18nUtil.*;

public class PageList extends List  {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageList.class);

    private final String DEFAULT_I18N_CATEGORY = "pagelist";


    @Override
    @SuppressWarnings("Duplicates")
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        detailsNameSuffix = new String[]{"page-details"};

        loadConfig();

        //override properties
        getComponentProperties().put(LISTITEM_LINK_TEXT, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TEXT,DEFAULT_I18N_CATEGORY,_i18n));
        getComponentProperties().put(LISTITEM_LINK_TITLE, getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LIST_ITEM_LINK_TITLE,DEFAULT_I18N_CATEGORY,_i18n));

    }


}