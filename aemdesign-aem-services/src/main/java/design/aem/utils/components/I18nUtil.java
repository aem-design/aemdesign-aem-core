package design.aem.utils.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class I18nUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtil.class);

    public static final String LANGUAGE_TAG_PATH = "/content/cq:tags/language";
    public static final String LANGUAGE_DEFAULT = Locale.ENGLISH.getLanguage();
    public static final String LANGUAGE_DEFAULT_LABEL = "Missing Label";

    public static final String DEFAULT_I18N_LIST_LINK_TITLE = "listLinkTitle";
    public static final String DEFAULT_I18N_LIST_LINK_TEXT = "listLinkText";

    public static final String DEFAULT_I18N_LIST_ITEM_LINK_TITLE = "listItemLinkTitle";
    public static final String DEFAULT_I18N_LIST_ITEM_LINK_TEXT = "listItemLinkText";

    public static final String DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX = "listItemTitleLengthMax";
    public static final String DEFAULT_I18N_LIST_ITEM_TITLE_LENGTH_MAX_SUFFIX = "listItemTitleLengthMaxSuffix";

    public static final String DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX = "listItemSummaryLengthMax";
    public static final String DEFAULT_I18N_LIST_ITEM_SUMMARY_LENGTH_MAX_SUFFIX = "listItemSummaryLengthMaxSuffix";

    public static final String DEFAULT_I18N_INHERIT_CATEGORY = "inherit";
    public static final String DEFAULT_I18N_INHERIT_LABEL_PARENTNOTFOUND = "parentnotfound";


    public static String getDefaultLabelIfEmpty(String currentLabel, String currentCategory, String defaultCode, String defaultCategory, com.day.cq.i18n.I18n i18n, String... params) {
        String label = "";
        if (isEmpty(currentLabel)) {
            label = i18n.get(defaultCode, defaultCategory, params);
        } else {
            label = i18n.get(currentLabel, currentCategory, params);
        }

        if (isEmpty(label)) {
            label = LANGUAGE_DEFAULT_LABEL;
        }

        return label;
    }

    public static String getDefaultLabelIfEmpty(String defaultCode, String defaultCategory, String defaultLabel, com.day.cq.i18n.I18n i18n, String... params) {
        String label = i18n.get(defaultCode, defaultCategory, params);

        if (isEmpty(label)) {
            label = defaultLabel;
        }

        return label;
    }


}
