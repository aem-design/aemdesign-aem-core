/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 AEM.Design
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package design.aem.utils.components;

import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class I18nUtil {

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
