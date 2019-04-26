package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagConstants;
import com.google.gson.*;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.TagUtil.getPageTags;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;

public class LocationDetails extends GenericDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationDetails.class);


    @Override
    @SuppressWarnings("Duplicates")
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());


        final String DEFAULT_I18N_CATEGORY = "location-detail";
        final String DEFAULT_I18N_LABEL = "variantHiddenLabel";
        final String DEFAULT_TITLE = getPageTitle(getResourcePage(), getResource());

        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"title", DEFAULT_TITLE, "data-title"},
                {"latitude", 0.0, "data-latitude"},
                {"longitude", 0.0, "data-longitude"},
                {"description", getPageDescription(getResourcePage()),"data-description"},
                {"pages", new String[0]},
                {TagConstants.PN_TAGS, getPageTags(getResourcePage())},
                {FIELD_PAGE_URL, getPageUrl(getResourcePage())},
                {FIELD_PAGE_TITLE, DEFAULT_TITLE},
                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage())},
                {"variantHiddenLabel", getDefaultLabelIfEmpty("", DEFAULT_I18N_CATEGORY, DEFAULT_I18N_LABEL, DEFAULT_I18N_CATEGORY, _i18n)},
        };


        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});
        componentProperties.put("category",getTagsAsAdmin(getSlingScriptHelper(), tags, getRequest().getLocale()));


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
