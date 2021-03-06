package design.aem.models.v2.layout;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.services.ContentAccess;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static design.aem.utils.components.CommonUtil.DEFAULT_PAR_NAME;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SuppressWarnings({"Duplicates", "squid:S3776"})
public class ContentBlock extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentBlock.class);

    protected static final String DEFAULT_I18N_CATEGORY = "contentblock";
    protected static final String DEFAULT_I18N_BACKTOTOP_LABEL = "backtotoplabel";
    protected static final String DEFAULT_I18N_BACKTOTOP_TITLE = "backtotoptitle";
    protected static final String DEFAULT_BACKGROUND_ATTRIBUTES_VIDEO = "videoBooleanAttrs";
    protected static final String DEFAULT_TITLE_TAG_TYPE = "h2";

    protected void ready() throws Exception {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);

        componentProperties.put("linksRightList", getPageListInfo(
            this,
            getPageManager(),
            getResourceResolver(),
            componentProperties.get("linksRight", new String[]{})
        ));

        componentProperties.put("linksLeftList", getPageListInfo(
            this,
            getPageManager(),
            getResourceResolver(),
            componentProperties.get("linksLeft", new String[]{})
        ));

        componentProperties.put("topLinkLabel", getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_CATEGORY,
            DEFAULT_I18N_BACKTOTOP_LABEL,
            DEFAULT_I18N_CATEGORY,
            i18n));

        componentProperties.put("topLinkTitle", getDefaultLabelIfEmpty(
            StringUtils.EMPTY,
            DEFAULT_I18N_CATEGORY,
            DEFAULT_I18N_BACKTOTOP_TITLE,
            DEFAULT_I18N_CATEGORY,
            i18n));

        Node resourceNode = getResource().adaptTo(Node.class);

        if (componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT).equals("advsection")) {
            String ariaLabelledBy = componentProperties.get(FIELD_ARIA_LABELLEDBY, StringUtils.EMPTY);

            if (isEmpty(ariaLabelledBy)) {
                String labelId = getComponentId(null);

                if (resourceNode != null) {
                    labelId = "heading-".concat(resourceNode.getName());
                }

                componentProperties.put(FIELD_ARIA_LABELLEDBY, labelId);
                componentProperties.attr.add("aria-labelledby", labelId);

                componentProperties.put(COMPONENT_ATTRIBUTES,
                    buildAttributesString(componentProperties.attr.getData(), null));
            }
        }

        componentProperties.put(DEFAULT_BACKGROUND_VIDEO_NODE_NAME,
            getBackgroundVideoRenditions(this));

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,
            getBackgroundImageRenditions(this));

        String variant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

        //skip output of table if noconfig selector is used, used in testing to speedup page load
        if (variant.equals("componentConfig")
            && StringUtils.contains(getRequest().getRequestPathInfo().getSelectorString(), "showconfig")) {
            Resource componentresource = getResource().getChild(DEFAULT_PAR_NAME);

            if (componentresource != null && componentresource.hasChildren()) {
                Resource firstComponent = componentresource.listChildren().next();

                if (firstComponent != null) {
                    ContentAccess contentAccess = getSlingScriptHelper().getService(ContentAccess.class);

                    if (contentAccess != null) {
                        try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
                            componentProperties.put("firstComponentConfig", getComponentFieldsAndDialogMap(
                                firstComponent,
                                adminResourceResolver,
                                getSlingScriptHelper()));
                        } catch (Exception ex) {
                            LOGGER.error("ContentBlock: error accessing component dialog component.path={}, ex={}",
                                firstComponent.getPath(),
                                ex);
                        }
                    }
                }
            }
        }

        componentProperties.put(DEFAULT_BACKGROUND_ATTRIBUTES_VIDEO,
            Stream.of(new String[][] {
                { "autoplay", "autoplay" },
                { "muted", "muted" },
                { "loop", "loop" },
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]))
        );
    }

    @Override
    protected void setFields() {
        final String[] DEFAULT_VIDEO_ATTRIBUTES = new String[]{
            getCurrentTenant() + ":component-style-modifier/video/playsinline",
            getCurrentTenant() + ":component-style-modifier/video/loop",
            getCurrentTenant() + ":component-style-modifier/video/muted",
        };

        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {"hideTitle", false},
            {"hideTopLink", false},
            {"linksLeftTitle", StringUtils.EMPTY},
            {"linksRightTitle", StringUtils.EMPTY},
            {"dataTitle", StringUtils.EMPTY},
            {"dataScroll", StringUtils.EMPTY},
            {"linksRight", new String[]{}},
            {"linksLeft", new String[]{}},
            {"titleType", DEFAULT_TITLE_TAG_TYPE},
            {"title", StringUtils.EMPTY},
            {"dataParent", StringUtils.EMPTY},
            {"dataToggle", StringUtils.EMPTY},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, DEFAULT_VIDEO_ATTRIBUTES, StringUtils.EMPTY, Tag.class.getCanonicalName()},
        });
    }
}
