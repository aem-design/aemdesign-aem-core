package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.google.gson.*;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.FIELD_PAGE_TITLE_NAV;
import static design.aem.utils.components.ConstantsUtil.FIELD_PAGE_URL;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;

public class LocationDetails extends GenericDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationDetails.class);


    @Override
    @SuppressWarnings("Duplicates")
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());


        final String DEFAULT_I18N_CATEGORY = "location-detail";
        final String DEFAULT_I18N_LABEL = "variantHiddenLabel";


        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"title", getPageTitle(getResourcePage()), "data-title"},
                {"latitude", 0.0, "data-latitude"},
                {"longitude", 0.0, "data-longitude"},
                {"pages", new String[0]},
                {FIELD_PAGE_URL, getPageUrl(getResourcePage())},
                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage())},
                {"variantHiddenLabel", getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LABEL, DEFAULT_I18N_CATEGORY, _i18n)},
                {DETAILS_LINK_TEXT, getPageNavTitle(getResourcePage())},
                {DETAILS_LINK_TITLE, getPageTitle(getResourcePage())},
        };


        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        //get additional page list
        String[] supportedDetails = DEFAULT_LIST_DETAILS_SUFFIX;
        String[] supportedRoots = DEFAULT_LIST_PAGE_CONTENT;
        String[] pages = componentProperties.get("pages", new String[0]);
        java.util.List<ComponentProperties> pageList = getPageListInfo(getContextObjects(this), getPageManager(), getResourceResolver(), pages, supportedDetails, supportedRoots, 0, true);

        componentProperties.put("pageList", pageList);


        //create a json string for pageList with selected fields
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            //do not serialise system fields
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);

            //list of fields we need
            ArrayList<String> fieldList = new ArrayList<>();
            fieldList.add("title");
            fieldList.add("description");
            fieldList.add("category");
            fieldList.add("pageImage");
            fieldList.add("path");

            Gson gson = gsonBuilder.create();
            //convert pageList to Json
            JsonElement pagesJson = gson.toJsonTree(pageList);
            JsonArray pagesJsonArray = pagesJson.getAsJsonArray();

            if (pagesJsonArray != null && pagesJsonArray.size() > 0) {
                for (int i = 0; i < pagesJsonArray.size(); i++) {
                    //get first element of array
                    JsonObject object = pagesJsonArray.get(i).getAsJsonObject();
                    //get list of all keys from object
                    List<String> keys = object.entrySet()
                            .stream()
                            .map(k -> k.getKey())
                            .collect(Collectors.toCollection(ArrayList::new));

                    //remove fields that we dont need
                    for (String key : keys) {
                        if (!fieldList.contains(key)) {
                            object.remove(key);
                        }
                    }

                }
                componentProperties.put("pagesJson", pagesJson.toString());
                componentProperties.attr.add("data-pages", pagesJson.toString());
            }
        } catch (Exception ex) {
            LOGGER.error("Could not convert pageList to Json {}", ex);
        }

        processCommonFields();

    }

}
