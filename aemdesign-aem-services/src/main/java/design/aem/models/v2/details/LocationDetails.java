package design.aem.models.v2.details;

import com.google.gson.*;
import design.aem.components.ComponentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_PAGE_CONTENT;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.getContextObjects;

public class LocationDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LocationDetails.class);

    static {
        COMPONENT_DETAILS_NAME = "location-details";

        USE_SIDE_EFFECTS = false;
    }

    protected static final String FIELD_LATITUDE = "latitude";
    protected static final String FIELD_LONGITUDE = "longitude";
    protected static final String FIELD_PAGES = "pages";

    @Override
    public void ready() {
        super.ready();

        processComponentProperties();
    }

    @Override
    protected void setAdditionalComponentFields() {
        setComponentFields(new Object[][]{
            {FIELD_LATITUDE, 0.0, "data-latitude"},
            {FIELD_LONGITUDE, 0.0, "data-longitude"},
            {FIELD_PAGES, new String[0]},
        });
    }

    protected void processComponentProperties() {
        String[] pages = componentProperties.get("pages", new String[0]);

        List<ComponentProperties> pageList = getPageListInfo(
            getContextObjects(this),
            getPageManager(),
            getResourceResolver(),
            pages,
            DEFAULT_LIST_DETAILS_SUFFIX,
            DEFAULT_LIST_PAGE_CONTENT,
            0,
            true);

        componentProperties.put("pageList", pageList);

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();

            // Do not serialise system fields
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);

            // List of fields needed for each item
            ArrayList<String> fieldList = new ArrayList<>();

            fieldList.add("title");
            fieldList.add("description");
            fieldList.add("category");
            fieldList.add("pageImage");
            fieldList.add("path");

            Gson gson = gsonBuilder.create();

            // Convert pageList to JSON
            JsonElement pagesJson = gson.toJsonTree(pageList);
            JsonArray pagesJsonArray = pagesJson.getAsJsonArray();

            if (pagesJsonArray != null && pagesJsonArray.size() > 0) {
                for (int i = 0; i < pagesJsonArray.size(); i++) {
                    // Get first element of array
                    JsonObject object = pagesJsonArray.get(i).getAsJsonObject();

                    // Get list of all keys from object
                    List<String> keys = object.entrySet()
                        .stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(ArrayList::new));

                    // Remove fields that we dont need
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
            LOGGER.error("Could not convert page list to JSON {0}", ex);
        }
    }
}
