package design.aem.models.v2.analytics;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import design.aem.components.ComponentProperties;
import design.aem.services.ContentAccess;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.DateTimeUtil.formatDate;
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
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        componentProperties = ComponentsUtil.getNewComponentProperties(this);

        Component detailsComponent = null;

        try {
            String detailsPath = findComponentInPage(getResourcePage(), DEFAULT_LIST_DETAILS_SUFFIX);
            ValueMap detailsProperties = getProperties();
            Resource details = getResourceResolver().getResource(detailsPath);

            //get details properties if its found
            if (!ResourceUtil.isNonExistingResource(details)) {
                detailsProperties = details.adaptTo(ValueMap.class);
            }

            componentProperties.put("pageName", detailsProperties.get(DETAILS_ANALYTICS_PAGENAME, ""));
            componentProperties.put("pageType", detailsProperties.get(DETAILS_ANALYTICS_PAGETYPE, ""));
            componentProperties.put("pagePath", getResourcePage().getPath());
            if (isNotEmpty(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, ""))) {
                componentProperties.put("effectiveDate", formatDate(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.getInstance()), "yyyy-MM-dd"));
            } else {
                componentProperties.put("effectiveDate", "");
            }
            componentProperties.put("contentCountry", getResourcePage().getLanguage(false).getDisplayCountry());
            componentProperties.put("contentLanguage", getResourcePage().getLanguage(false).getDisplayLanguage().toLowerCase());

            componentProperties.put("platform", detailsProperties.get(DETAILS_ANALYTICS_PLATFORM, "aem"));
            componentProperties.put("abort", detailsProperties.get(DETAILS_ANALYTICS_ABORT, "false"));
            componentProperties.put("detailsMissing", isEmpty(detailsPath));

            componentProperties.put("pageName", detailsProperties.get(DETAILS_ANALYTICS_PAGENAME, ""));

            String variant = detailsProperties.get(DETAILS_ANALYTICS_VARIANT, DEFAULT_VARIANT);
            String variantTemplate = format(COMPONENT_VARIANT_TEMPLATE_FORMAT, variant);

            if (!ResourceUtil.isNonExistingResource(details)) {

                ContentAccess contentAccess = getSlingScriptHelper().getService(ContentAccess.class);
                try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {

                    ComponentManager componentManager = getResourceResolver().adaptTo(ComponentManager.class);
                    Component resourceComponent = componentManager.getComponentOfResource(details);

                    variantTemplate = getComponentVariantTemplate(resourceComponent, variant, adminResourceResolver);

                } catch (Exception ex) {
                    LOGGER.error("getComponentProperties: error processing properties: component={}, ex.message={}, ex={}", detailsComponent.getPath(), ex.getMessage(), ex);
                }

            }

            //compile variantTemplate param
            componentProperties.put(COMPONENT_VARIANT_TEMPLATE, variantTemplate);

        } catch (Exception ex) {
            LOGGER.error("datalayer: {}", ex);
        }
    }




}