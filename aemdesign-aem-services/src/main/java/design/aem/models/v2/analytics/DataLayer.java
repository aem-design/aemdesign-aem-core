package design.aem.models.v2.analytics;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.replication.ReplicationStatus;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.ComponentsUtil.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DataLayer extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLayer.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    @SuppressWarnings({"squid:S2637","squid:S2259"})
    public void activate() throws Exception {

        componentProperties = ComponentsUtil.getNewComponentProperties(this);

        //set defaults variant template before potentially overriding
        componentProperties.put(COMPONENT_VARIANT_TEMPLATE, DEFAULT_VARIANT_TEMPLATE);

        componentProperties.put("pagePath", getResourcePage().getPath());
        if (isNotEmpty(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, ""))) {
            componentProperties.put("effectiveDate", DateFormatUtils.format(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.getInstance()), "yyyy-MM-dd"));
        } else {
            componentProperties.put("effectiveDate", "");
        }
        componentProperties.put("contentCountry", getResourcePage().getLanguage(false).getDisplayCountry());
        componentProperties.put("contentLanguage", getResourcePage().getLanguage(false).getDisplayLanguage().toLowerCase());

        try {
            String detailsPath = findComponentInPage(getResourcePage(), DEFAULT_LIST_DETAILS_SUFFIX);

            if (detailsPath != null && StringUtils.isNotEmpty(detailsPath)) {

                Resource details = getResourceResolver().getResource(detailsPath);
                ValueMap detailsProperties = getProperties();

                //get details properties if its found
                if (!ResourceUtil.isNonExistingResource(details)) {
                    detailsProperties = details.adaptTo(ValueMap.class);
                }

                if (detailsProperties != null) {
                    componentProperties.put("pageName", detailsProperties.get(DETAILS_ANALYTICS_PAGENAME, ""));
                    componentProperties.put("pageType", detailsProperties.get(DETAILS_ANALYTICS_PAGETYPE, ""));
                    componentProperties.put("platform", detailsProperties.get(DETAILS_ANALYTICS_PLATFORM, "aem"));
                    componentProperties.put("abort", detailsProperties.get(DETAILS_ANALYTICS_ABORT, "false"));

                    String variant = detailsProperties.get(DETAILS_ANALYTICS_VARIANT, DEFAULT_VARIANT);
                    String variantTemplate = getComponentVariantTemplate(getComponent(), format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant), getSlingScriptHelper());
                    componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);
                }
            } else {
                componentProperties.put("detailsMissing", isEmpty(detailsPath));
                LOGGER.error("Data layer detailsPath missing under {}", getResourcePage().getPath());
            }

        } catch (Exception ex) {
            LOGGER.error("datalayer: {}", ex);
        }
    }




}