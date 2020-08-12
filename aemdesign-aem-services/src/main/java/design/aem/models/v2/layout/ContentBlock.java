package design.aem.models.v2.layout;

import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import design.aem.models.BaseComponent;
import design.aem.services.ContentAccess;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

import static design.aem.utils.components.CommonUtil.DEFAULT_PAR_NAME;
import static design.aem.utils.components.ComponentDetailsUtil.getPageListInfo;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static design.aem.utils.components.ImagesUtil.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@SuppressWarnings({"Duplicates", "squid:S3776"})
public class ContentBlock extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentBlock.class);

    protected final String PAR_NAME = DEFAULT_PAR_NAME;

    protected final String DEFAULT_I18N_CATEGORY = "contentblock";
    protected final String DEFAULT_I18N_BACKTOTOP_LABEL = "backtotoplabel";
    protected final String DEFAULT_I18N_BACKTOTOP_TITLE = "backtotoptitle";

    protected final Boolean DEFAULT_HIDE_TITLE = false;
    protected final Boolean DEFAULT_SHOW_TOP_LINK = false;
    protected final Boolean DEFAULT_SHOW_ICON = false;
    protected final String DEFAULT_TITLE_TAG_TYPE = "h2";

    protected final String FIELD_SHOW_TOP_LINK = "showTopLink";
    protected final String FIELD_SHOW_ICON = "showIcon";
    protected final String FIELD_TITLE_TYPE = "titleType";
    protected final String FIELD_LINKS_LEFT_TITLE = "linksLeftTitle";
    protected final String FIELD_LINKS_LEFT = "linkPagesLeft";
    protected final String FIELD_LINKS_RIGHT_TITLE = "linksRightTitle";
    protected final String FIELD_LINKS_RIGHT = "linkPagesRight";
    protected final String FIELD_DATA_PARENT = "dataParent";
    protected final String FIELD_DATA_TOGGLE = "dataToggle";

    @SuppressWarnings({"Duplicates", "squid:S3776"})
    public void ready() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_TITLE, StringUtils.EMPTY},
            {FIELD_TITLE_TYPE, DEFAULT_TITLE_TAG_TYPE},
            {FIELD_HIDE_TITLE, DEFAULT_HIDE_TITLE},
            {FIELD_SHOW_TOP_LINK, DEFAULT_SHOW_TOP_LINK},
            {FIELD_SHOW_ICON, DEFAULT_SHOW_ICON},
            {FIELD_LINKS_LEFT_TITLE, StringUtils.EMPTY},
            {FIELD_LINKS_LEFT, new String[]{}},
            {FIELD_LINKS_RIGHT_TITLE, StringUtils.EMPTY},
            {FIELD_LINKS_RIGHT, new String[]{}},
            {FIELD_DATA_PARENT, StringUtils.EMPTY},
            {FIELD_DATA_TOGGLE, StringUtils.EMPTY},
            {FIELD_STYLE_COMPONENT_BOOLEANATTR, getDefaultVideoAttributes(), StringUtils.EMPTY, Tag.class.getCanonicalName()},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ANALYTICS,
            DEFAULT_FIELDS_ACCESSIBILITY);

        String currentVariant = componentProperties.get(FIELD_VARIANT, DEFAULT_VARIANT);

        generateAdditionalComponentProps();

        if (currentVariant.equals("advsection")) {
            try {
                generateAdvancedSectionComponentProps();
            } catch (Exception ex) {
                LOGGER.error("Unable generate advanced section component props: {}", ex.getLocalizedMessage());
            }
        }

        generateBackgroundAssetComponentProps();

        if (currentVariant.equals("componentConfig") &&
            StringUtils.contains(getRequest().getRequestPathInfo().getSelectorString(), "showconfig")) {
            generateComponentConfiguration();
        }

        generateHasAuthoredContentComponentProps();
    }

    protected String[] getDefaultVideoAttributes() {
        String currentTenant = getCurrentTenant();

        return new String[]{
            String.format("%s:component-style-modifier/video/playsinline", currentTenant),
            String.format("%s:component-style-modifier/video/loop", currentTenant),
            String.format("%s:component-style-modifier/video/muted", currentTenant),
        };
    }

    protected void generateAdditionalComponentProps() {
        I18n i18n = new I18n(getRequest());

        componentProperties.put("linksLeftList",
            getPageListInfo(
                this,
                getPageManager(),
                getResourceResolver(),
                componentProperties.get(FIELD_LINKS_LEFT, new String[]{})));

        componentProperties.put("linksRightList",
            getPageListInfo(
                this,
                getPageManager(),
                getResourceResolver(),
                componentProperties.get(FIELD_LINKS_RIGHT, new String[]{})));

        componentProperties.put("topLinkLabel",
            getDefaultLabelIfEmpty(
                StringUtils.EMPTY,
                DEFAULT_I18N_CATEGORY,
                DEFAULT_I18N_BACKTOTOP_LABEL,
                DEFAULT_I18N_CATEGORY,
                i18n));

        componentProperties.put("topLinkTitle",
            getDefaultLabelIfEmpty(
                StringUtils.EMPTY,
                DEFAULT_I18N_CATEGORY,
                DEFAULT_I18N_BACKTOTOP_TITLE,
                DEFAULT_I18N_CATEGORY,
                i18n));
    }

    protected void generateAdvancedSectionComponentProps() throws Exception {
        String ariaLabelledBy = componentProperties.get(FIELD_ARIA_LABELLEDBY, "");

        Node resourceNode = getResource().adaptTo(Node.class);

        if (isEmpty(ariaLabelledBy)) {
            String labelId = getComponentId(null);
            if (resourceNode != null) {
                labelId = "heading-" . concat(resourceNode.getName());
            }

            componentProperties.put(FIELD_ARIA_LABELLEDBY, labelId);
            componentProperties.attr.add("aria-labelledby", labelId);

            componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(
                componentProperties.attr.getAttributes(),
                getSlingScriptHelper().getService(XSSAPI.class)));
        }
    }

    protected void generateBackgroundAssetComponentProps() {
        componentProperties.put(DEFAULT_BACKGROUND_VIDEO_NODE_NAME,
            getBackgroundVideoRenditions(this));

        componentProperties.put(DEFAULT_BACKGROUND_IMAGE_NODE_NAME,
            getBackgroundImageRenditions(this));
    }

    private void generateComponentConfiguration() {
        Resource componentResource = getResource().getChild(PAR_NAME);

        if (componentResource != null && componentResource.hasChildren()) {
            Resource firstComponent = componentResource.listChildren().next();

            if (firstComponent != null) {
                ContentAccess contentAccess = getSlingScriptHelper().getService(ContentAccess.class);

                if (contentAccess != null) {
                    try (ResourceResolver adminResourceResolver = contentAccess.getAdminResourceResolver()) {
                        componentProperties.put("firstComponentConfig",
                            getComponentFieldsAndDialogMap(firstComponent, adminResourceResolver, getSlingScriptHelper()));
                    } catch (Exception ex) {
                        LOGGER.error("Error accessing component dialog component.path={}, ex={}",
                            firstComponent.getPath(),
                            ex);
                    }
                }
            }
        }
    }

    private void generateHasAuthoredContentComponentProps() {
        Resource componentResource = getResource().getChild(PAR_NAME);

        componentProperties.put("hasAuthoredContent",
            componentResource != null && componentResource.hasChildren());
    }
}
