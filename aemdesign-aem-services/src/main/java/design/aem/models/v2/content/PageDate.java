package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.replication.ReplicationStatus;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static design.aem.utils.components.ComponentsUtil.*;

public class PageDate extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PageDate.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }
    private static String PUBLISH_DATE_FORMAT = "yyyy-MM-dd";
    private static String PUBLISH_DATE_DISPLAY_FORMAT = "EEEE dd MMMM YYYY";

    @Override
    public void activate() throws Exception {

        com.day.cq.i18n.I18n _i18n = new I18n(getRequest());


        final String DEFAULT_I18N_CATEGORY = "pagedate";


        //not using lamda is available so this is the best that can be done
        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"publishDate", getPageProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED,getPageProperties().get(JcrConstants.JCR_CREATED, Calendar.getInstance()))},
                {"jcr:created", ""}
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        Calendar publishDate = componentProperties.get("publishDate", Calendar.getInstance()); //_pageProperties.get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED,_pageProperties.get(JcrConstants.JCR_CREATED, Calendar.getInstance()));

//    componentProperties.put("publishDate",publishDate);

        //get format strings from dictionary
        String dateFormatString = _i18n.get("publishDateFormat",DEFAULT_I18N_CATEGORY);
        String dateDisplayFormatString = _i18n.get("publishDateDisplayFormat",DEFAULT_I18N_CATEGORY);

        //could not read dictionary
        if (dateFormatString.equals("publishDateFormat")) {
            dateFormatString = PUBLISH_DATE_FORMAT;
        }

        //could not read dictionary
        if (dateDisplayFormatString.equals("publishDateDisplayFormat")) {
            dateDisplayFormatString = PUBLISH_DATE_DISPLAY_FORMAT;
        }

        try {

            //format date into formatted date
            FastDateFormat dateFormat = FastDateFormat.getInstance(dateFormatString);
            String publishDateText = dateFormat.format(publishDate.getTime());

            //format date into display date
            dateFormat = FastDateFormat.getInstance(dateDisplayFormatString);
            String publishDisplayDateText = dateFormat.format(publishDate.getTime());

            componentProperties.put("publishDateText", publishDateText);
            componentProperties.put("publishDisplayDateText", publishDisplayDateText);

            componentProperties.attr.add("datetime", publishDateText);

        } catch (Exception ex) {
            LOGGER.error("PageDate: dateFormatString={},dateDisplayFormatString={},publishDate={},path={},ex.message={},ex={}", dateFormatString,dateDisplayFormatString,publishDate,getResource().getPath(),ex.getMessage(), ex);
        }

        componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));


    }



}