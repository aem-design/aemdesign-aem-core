package design.aem.models.v2.details;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.*;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.TagUtil.getTagValueAsAdmin;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ContactDetails extends GenericDetails {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContactDetails.class);

    final String COMPONENT_DETAILS_NAME = "contact-details";
    final String componentPath = "./" + PATH_DEFAULT_CONTENT + "/" + COMPONENT_DETAILS_NAME;

    final String DEFAULT_FORMAT_TITLE = "${honorificPrefix} ${givenName} ${familyName}";
    final String DEFAULT_FORMAT_DESCRIPTION = "${jobTitle}";
    final String FIELD_FORMAT_TITLE = "titleFormat";
    final String FIELD_FORMAT_DESCRIPTION = "descriptionFormat";
    final String FIELD_FORMATTED_TITLE = "titleFormatted";
    final String FIELD_FORMATTED_TITLE_TEXT = "titleFormattedText";
    final String FIELD_FORMATTED_DESCRIPTION = "descriptionFormatted";
    final String I18N_CATEGORY = "contact-detail";


    @Override
    @SuppressWarnings("Duplicates")
    protected void ready() {
        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_TITLE_TAG_TYPE = "div";
        final Boolean DEFAULT_HIDE_DESCRIPTION = false;
        final Boolean DEFAULT_SHOW_BREADCRUMB = true;
        final Boolean DEFAULT_SHOW_TOOLBAR = true;
        final String DEFAULT_I18N_CATEGORY = "contact-detail";
        final String DEFAULT_I18N_LABEL = "variantHiddenLabel";

        final String DEFAULT_TITLE = getPageTitle(getResourcePage(), getResource());

        setComponentFields(new Object[][]{
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {FIELD_TITLE_TAG_TYPE, DEFAULT_TITLE_TAG_TYPE},
                {"title", getResourcePage().getProperties().get(JcrConstants.JCR_TITLE, getResourcePage().getName())},
                {"honorificPrefix", ""}, //tag path
                {"givenName",""},
                {"familyName",""},
                {"titleFormat", ""}, //tag path, will be resolved to value in processComponentFields
                {"jobTitle",""},
                {"employee",""},
                {"email",""},
                {"descriptionFormat", ""}, //tag path, will be resolved to value in processComponentFields
                {"hideDescription", DEFAULT_HIDE_DESCRIPTION},
                {TagConstants.PN_TAGS, new String[]{},"data-tags", Tag.class.getCanonicalName()},
                {"showBreadcrumb", DEFAULT_SHOW_BREADCRUMB},
                {"showToolbar", DEFAULT_SHOW_TOOLBAR},
                {FIELD_PAGE_URL, getPageUrl(getResourcePage())},
                {FIELD_PAGE_TITLE, DEFAULT_TITLE},
                {FIELD_PAGE_TITLE_NAV, getPageNavTitle(getResourcePage())},
                {"variantHiddenLabel", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_DETAILS_OPTIONS);

        String[] tags = componentProperties.get(TagConstants.PN_TAGS, new String[]{});

        componentProperties.put("category",getTagsAsAdmin(getSlingScriptHelper(), tags, getRequest().getLocale()));

        //grab value for prefix
        componentProperties.put("honorificPrefix",
                getTagValueAsAdmin(componentProperties.get("honorificPrefix", ""),getSlingScriptHelper())
        );

        //format fields
        componentProperties.putAll(processComponentFields(componentProperties,_i18n,getSlingScriptHelper()), false);

        componentProperties.put(DETAILS_DESCRIPTION,componentProperties.get(FIELD_FORMATTED_DESCRIPTION,""));

        processCommonFields();

        //set something if title formatted is empty
        if (isEmpty(componentProperties.get(FIELD_FORMATTED_TITLE,""))) {
            componentProperties.put(FIELD_FORMATTED_TITLE,componentProperties.get("title",""));
            componentProperties.put(FIELD_FORMATTED_TITLE_TEXT,componentProperties.get("title",""));
        }
    }

    /***
     * substitute formatted field template with fields from component
     * @param componentProperties source map with fields
     * @param i18n
     * @param sling
     * @return returns map with new values
     */
    @SuppressWarnings("Duplicates")
    public Map<String, Object> processComponentFields(ComponentProperties componentProperties, com.day.cq.i18n.I18n i18n, SlingScriptHelper sling){
        Map<String, Object> newFields = new HashMap();

        try {

            String formattedTitle = compileComponentMessage(FIELD_FORMAT_TITLE, DEFAULT_FORMAT_TITLE, componentProperties, sling);
            Document fragment = Jsoup.parse(formattedTitle);
            String formattedTitleText = fragment.text();

            newFields.put(FIELD_FORMATTED_TITLE,
                    formattedTitle.trim()
            );
            newFields.put(FIELD_FORMATTED_TITLE_TEXT,
                    formattedTitleText.trim()
            );
            newFields.put(FIELD_FORMATTED_DESCRIPTION,
                    compileComponentMessage(FIELD_FORMAT_DESCRIPTION, DEFAULT_FORMAT_DESCRIPTION, componentProperties, sling).trim()
            );

        } catch (Exception ex) {
            LOGGER.error("Could not process component fields in " + COMPONENT_DETAILS_NAME);
        }
        return newFields;
    }
}
