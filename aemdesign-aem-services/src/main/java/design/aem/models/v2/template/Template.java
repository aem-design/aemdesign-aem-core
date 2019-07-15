package design.aem.models.v2.template;

import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Template extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Template.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() {

        componentProperties = ComponentsUtil.getNewComponentProperties(this);

        String[] listLookForDetailComponent = DEFAULT_LIST_DETAILS_SUFFIX;
        String detailsPath = findComponentInPage(getCurrentPage(),listLookForDetailComponent);

        if (isNotEmpty(detailsPath)) {
            String componentPath = detailsPath + ".badge.metadata";

            componentProperties.put("detailsPath", detailsPath);
            componentProperties.put("detailsMetadataBadgePath", componentPath);
            componentProperties.put("detailsMetadataBadgeSelectors", "badge.metadata");
            componentProperties.put("detailsMetadataBadgeUrl", componentPath.concat(DEFAULT_EXTENTION));
        }
    }
}