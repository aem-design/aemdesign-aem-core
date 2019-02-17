package design.aem.models.v2.lists;

import com.day.cq.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageList extends List  {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageList.class);

    @Override
    public void activate() throws Exception {

        LOGGER.error("PageList: loaded");

        I18n _i18n = new I18n(getRequest());

        detailsNameSuffix = new String[]{"page-details"};

        loadConfig();

    }


}