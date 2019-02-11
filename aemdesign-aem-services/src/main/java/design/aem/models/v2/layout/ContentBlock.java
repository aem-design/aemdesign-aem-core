package design.aem.models.v2.layout;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.getBackgroundImageRenditions;
import static design.aem.utils.components.ImagesUtil.getBackgroundVideoRenditions;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ContentBlock extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentBlock.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_I18N_CATEGORY = "contentblock";
        final String DEFAULT_I18N_BACKTOTOP_LABEL = "backtotoplabel";
        final String DEFAULT_I18N_BACKTOTOP_TITLE = "backtotoptitle";
        final String DEFAULT_TITLE_TAG_TYPE = "h2";
        final String[] DEFAULT_VIDEO_ATTRIBUTES = new String[]{
                "aemdesign:component-style-modifier/video/playsinline",
                "aemdesign:component-style-modifier/video/loop",
                "aemdesign:component-style-modifier/video/muted",
        };

        Object[][] componentFields = {
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"hideTitle", false},
                {"hideTopLink", false},
                {"linksLeftTitle", ""},
                {"linksRightTitle", ""},
                {"dataTitle", ""},
                {"dataScroll", ""},
                {"linksRight", new String[]{}},
                {"linksLeft", new String[]{}},
                {"titleType", DEFAULT_TITLE_TAG_TYPE},
                {"title", ""},
                {"dataParent", ""},
                {"dataToggle", ""},
                {FIELD_STYLE_COMPONENT_BOOLEANATTR, DEFAULT_VIDEO_ATTRIBUTES,"", Tag.class.getCanonicalName()},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put("linksRightList",getPageListInfo(this,getPageManager(), getResourceResolver(), componentProperties.get("linksRight", new String[]{})));
        componentProperties.put("linksLeftList",getPageListInfo(this,getPageManager(), getResourceResolver(), componentProperties.get("linksLeft", new String[]{})));

        componentProperties.put("topLinkLabel",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_LABEL,DEFAULT_I18N_CATEGORY,_i18n));
        componentProperties.put("topLinkTitle",getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_BACKTOTOP_TITLE,DEFAULT_I18N_CATEGORY,_i18n));

        if (componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT).equals("advsection")) {
            String ariaLabelledBy = componentProperties.get(FIELD_ARIA_LABELLEDBY, "");
            if (isEmpty(ariaLabelledBy)) {
                String labelId = "heading-".concat(getResource().adaptTo(Node.class).getName());
                componentProperties.put(FIELD_ARIA_LABELLEDBY, labelId);

                componentProperties.attr.add("aria-labelledby",labelId);
                componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));
            }

        }

        componentProperties.putAll(getBackgroundVideoRenditions(this));

        componentProperties.putAll(getBackgroundImageRenditions(this));


    }



}