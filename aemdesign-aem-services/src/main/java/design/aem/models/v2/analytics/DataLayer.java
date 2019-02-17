package design.aem.models.v2.analytics;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.replication.ReplicationStatus;
import com.google.gson.Gson;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.DateTimeUtil.formatDate;
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

        String digitalDataJson = "{\"error\":\"could not load data layer\"}";

        componentProperties = ComponentsUtil.getNewComponentProperties(this);

        componentProperties.put("digitalDataJson",digitalDataJson);

        try {
            String detailsPath = findComponentInPage(getResourcePage(), DEFAULT_LIST_DETAILS_SUFFIX);
            ValueMap detailsProperties = getProperties();
            Resource details = getResourceResolver().getResource(detailsPath);
            //get details properties if its found
            if (details != null && !ResourceUtil.isNonExistingResource(details)) {
                detailsProperties = details.adaptTo(ValueMap.class);
                if (detailsProperties == null) {
                    detailsProperties = getProperties();
                }
            }

//        String pageName = _currentPage.getPath().substring(1).replace('/', ':');
//        pageName = pageName.replace("content:", "");


            HashMap<String, Object> digitalData = new HashMap<String, Object>();
            HashMap<String, Object> digitalDataPage = new HashMap<String, Object>();
            HashMap<String, Object> digitalDataPagePageInfo = new HashMap<String, Object>();
            ArrayList<String> digitalDataPageEvent = new ArrayList<String>();
            ArrayList<String> digitalDataPageError = new ArrayList<String>();
            HashMap<String, Object> digitalDataPageAttributes = new HashMap<String, Object>();

            digitalDataPagePageInfo.put("pageName", detailsProperties.get("analyticsPageName", ""));
            digitalDataPagePageInfo.put("pageType", detailsProperties.get("analyticsPageType", ""));
            digitalDataPagePageInfo.put("pagePath", getResourcePage().getPath());
            if (isNotEmpty(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, ""))) {
                digitalDataPagePageInfo.put("effectiveDate", formatDate(getProperties().get(ReplicationStatus.NODE_PROPERTY_LAST_REPLICATED, Calendar.getInstance()), "yyyy-MM-dd"));
            } else {
                digitalDataPagePageInfo.put("effectiveDate", "");
            }
            digitalDataPagePageInfo.put("contentCountry", getResourcePage().getLanguage(false).getDisplayCountry());
            digitalDataPagePageInfo.put("contentLanguage", getResourcePage().getLanguage(false).getDisplayLanguage().toLowerCase());

            digitalDataPageAttributes.put("platform", detailsProperties.get("analyticsPlatform", "aem"));
            digitalDataPageAttributes.put("abort", detailsProperties.get("analyticsAbort", "false"));
            digitalDataPageAttributes.put("detailsMissing", isEmpty(detailsPath));

            digitalDataPage.put("pageInfo", digitalDataPagePageInfo);
            digitalDataPage.put("attributes", digitalDataPageAttributes);

            digitalData.put("page", digitalDataPage);
            digitalData.put("event", digitalDataPageEvent);
            digitalData.put("error", digitalDataPageError);

            Gson gson = new Gson();

            digitalDataJson = gson.toJson(digitalData);

            componentProperties.put("digitalDataJson",digitalDataJson);

        } catch (Exception ex) {
            LOGGER.error("datalayer: {}", ex);
        }
    }




}