package design.aem.models.v2.template;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;

import static design.aem.utils.components.CommonUtil.DEFAULT_LIST_DETAILS_SUFFIX;
import static design.aem.utils.components.CommonUtil.findComponentInPage;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Template extends BaseComponent {
    protected void ready() {
        componentProperties = ComponentsUtil.getNewComponentProperties(this);

        String detailsPath = findComponentInPage(getCurrentPage(), DEFAULT_LIST_DETAILS_SUFFIX);

        if (isNotEmpty(detailsPath)) {
            String componentPath = detailsPath + ".badge.metadata";

            componentProperties.put("detailsPath", detailsPath);
            componentProperties.put("detailsMetadataBadgePath", componentPath);
            componentProperties.put("detailsMetadataBadgeSelectors", "badge.metadata");
            componentProperties.put("detailsMetadataBadgeUrl", componentPath.concat(DEFAULT_EXTENTION));
        }
    }
}
