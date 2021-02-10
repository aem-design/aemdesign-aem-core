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
import static design.aem.utils.components.ComponentsUtil.*;

public class LocationDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LocationDetails.class);

    @Override
    protected void ready() {
        super.ready();

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

            // List of fields we need
            ArrayList<String> fieldList = new ArrayList<>();

            fieldList.add("title");
            fieldList.add("description");
            fieldList.add("category");
            fieldList.add("pageImage");
            fieldList.add("path");

            Gson gson = gsonBuilder.create();

            JsonElement pagesJson = gson.toJsonTree(pageList);
            JsonArray pagesJsonArray = pagesJson.getAsJsonArray();

            if (pagesJsonArray != null && pagesJsonArray.size() > 0) {
                for (int i = 0; i < pagesJsonArray.size(); i++) {
                    JsonObject object = pagesJsonArray.get(i).getAsJsonObject();

                    List<String> keys = object.entrySet()
                        .stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(ArrayList::new));

                    // Remove fields that we dont need
                    keys.stream().filter(key -> !fieldList.contains(key)).forEach(object::remove);
                }

                componentProperties.put("pagesJson", pagesJson.toString());
                componentProperties.attr.add("data-pages", pagesJson.toString());
            }
        } catch (Exception ex) {
            LOGGER.error("Could not convert location list to JSON {}", ex);
        }
    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {"latitude", 0.0, "data-latitude"},
            {"longitude", 0.0, "data-longitude"},
            {"pages", new String[0]},
            {DETAILS_DATA_SCHEMA_ITEMSCOPE, "true", DETAILS_DATA_SCHEMA_ITEMSCOPE},
            {DETAILS_DATA_SCHEMA_ITEMTYPE, "http://schema.org/Place", DETAILS_DATA_SCHEMA_ITEMTYPE},
        });
    }

    @Override
    protected String getComponentCategory() {
        return "location-detail";
    }
}
