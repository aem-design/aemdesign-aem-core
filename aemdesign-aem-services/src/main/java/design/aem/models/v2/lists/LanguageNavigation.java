package design.aem.models.v2.lists;

import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.Page;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import design.aem.utils.components.ConstantsUtil;
import design.aem.utils.components.ResolverUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.TagUtil.getTagsAsAdmin;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LanguageNavigation extends BaseComponent {
    private static final String SEARCH_LOGIC = "searchlogic";
    private static final String SEARCH_LOGIC_DEFAULT = StringUtils.EMPTY;
    private static final String FIELD_DESCRIPTION = "description";

    @SuppressWarnings({"Duplicates", "squid:S3776"})
    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        Map<String, Map<String, String>> languageToggleMap = new LinkedHashMap<>();

        String appearanceOption = componentProperties.get(SEARCH_LOGIC, String.class);
        String[] tagsFilterList = componentProperties.get("languageSet", new String[]{});

        boolean isShowRoot = ("showRoot").equals(appearanceOption);
        boolean isShowNothing = ("showNothing").equals(appearanceOption);

        LinkedHashMap<String, Map> languageMap = getTagsAsAdmin(
            getSlingScriptHelper(),
            tagsFilterList,
            getRequest().getLocale());

        if (!isShowNothing || !languageMap.isEmpty()) {
            Page currentPage = getResourcePage();
            String languageRoot = StringUtils.EMPTY;
            String pagePath = StringUtils.EMPTY;

            if (currentPage != null) {
                pagePath = currentPage.getPath();
                languageRoot = LanguageUtil.getLanguageRoot(getResourcePage().getPath());
                pagePath = pagePath.substring(languageRoot.length());
            }

            String languageSiteParentPath = StringUtils.EMPTY;
            Resource languageSiteRootPage;

            for (String key : languageMap.keySet()) {
                Map<String, String> langTag = languageMap.get(key);

                String tagValue = langTag.get("value");
                String tagTitle = langTag.get("title");
                String tagDescription = langTag.get(FIELD_DESCRIPTION);

                if (isEmpty(languageSiteParentPath)) {
                    languageRoot = languageRoot.substring(0, languageRoot.indexOf(tagValue));
                    languageSiteRootPage = getResourceResolver().resolve(languageRoot);

                    if (!ResourceUtil.isNonExistingResource(languageSiteRootPage)) {
                        languageSiteParentPath = languageSiteRootPage.getPath();
                    }
                }

                String langPageRootPath = MessageFormat.format(
                    "{0}/{1}",
                    languageSiteParentPath,
                    tagValue);

                String langPagePath = MessageFormat.format(
                    "{0}/{1}{2}",
                    languageSiteParentPath,
                    tagValue,
                    pagePath);

                Resource langPageRoot = getResourceResolver().resolve(langPageRootPath);
                Resource langPage = getResourceResolver().resolve(langPagePath);

                if (ResourceUtil.isNonExistingResource(langPageRoot)) {
                    langPageRoot = null;
                }

                if (ResourceUtil.isNonExistingResource(langPage)) {
                    langPage = null;
                }

                if (langPage != null && langPageRoot != null) {
                    Map<String, String> pageInfo = new HashMap<>();

                    pageInfo.put("path", ResolverUtil.mappedUrl(
                        getResourceResolver(),
                        langPage.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));

                    pageInfo.put(FIELD_DESCRIPTION, tagDescription);
                    pageInfo.put("displayTitle", tagTitle);

                    if (langPagePath.startsWith(languageRoot)) {
                        pageInfo.put("current", "true");
                    }

                    Page langPageRootPage = langPageRoot.adaptTo(Page.class);

                    if (langPageRootPage != null) {
                        String hrefLang = langPageRootPage.getLanguage().toLanguageTag();
                        String language = langPageRootPage.getLanguage().toString();
                        pageInfo.put("hreflang", hrefLang);
                        pageInfo.put("language", language);
                    }

                    languageToggleMap.put(tagValue, pageInfo);
                } else if (isShowRoot && langPageRoot != null) {
                    Page langPageRootPage = langPageRoot.adaptTo(Page.class);

                    if (langPageRootPage != null) {
                        String hrefLang = langPageRootPage.getLanguage().toLanguageTag();
                        String language = langPageRootPage.getLanguage().toString();

                        Map<String, String> pageInfo = new HashMap<>();
                        pageInfo.put("path", ResolverUtil.mappedUrl(getResourceResolver(), langPageRootPage.getPath()).concat(ConstantsUtil.DEFAULT_EXTENTION));
                        pageInfo.put(FIELD_DESCRIPTION, langPageRootPage.getDescription());
                        pageInfo.put("displayTitle", langPageRootPage.getTitle());
                        pageInfo.put("hreflang", hrefLang);
                        pageInfo.put("language", language);

                        if (langPagePath.startsWith(languageRoot)) {
                            pageInfo.put("current", "true");
                        }

                        languageToggleMap.put(langPageRootPage.getName(), pageInfo);
                    }
                }
            }
        }

        componentProperties.put("languageMap", languageToggleMap);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"languageSet", new String[]{}},
            {SEARCH_LOGIC, SEARCH_LOGIC_DEFAULT}
        });
    }
}
