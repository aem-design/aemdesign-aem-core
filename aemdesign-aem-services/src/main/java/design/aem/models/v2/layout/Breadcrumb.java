package design.aem.models.v2.layout;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageNavTitle;
import static design.aem.utils.components.CommonUtil.tryParseInt;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Breadcrumb extends WCMUsePojo {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Breadcrumb.class);

    protected ComponentProperties componentProperties = null;

    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String DEFAULT_DELIMITER = "";
        final String DEFAULT_TRAIL = "";
        final String DEFAULT_ARIA_ROLE = "navigation";
        final String DEFAULT_ARIA_LABEL = "breadcrumb";
        final boolean DEFAULT_SHOW_HIDDEN = false;
        final boolean DEFAULT_HIDE_CURRENT = false;
        final int DEFAULT_LEVEL_START = 1;
        final int DEFAULT_LEVEL_END = 1;

        Object[][] componentFields = {
                {"delimiter", DEFAULT_DELIMITER},
                {"trail", DEFAULT_TRAIL},
                {"startLevel", ""},
                {"endLevel", ""},
                {"showHidden", DEFAULT_SHOW_HIDDEN},
                {"hideCurrent", DEFAULT_HIDE_CURRENT},
                {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE},
                {FIELD_ARIA_LABEL, DEFAULT_ARIA_LABEL},
                {FIELD_VARIANT, DEFAULT_VARIANT},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);

        List<Map> values = new ArrayList<Map>();

        int startLevel = tryParseInt(componentProperties.get("startLevel", ""), DEFAULT_LEVEL_START);
        int endLevel = tryParseInt(componentProperties.get("endLevel", ""), DEFAULT_LEVEL_END);
        int currentLevel = getResourcePage().getDepth();

        if (isBlank(componentProperties.get("endLevel", ""))) {
            endLevel = currentLevel;
        }

        boolean showHidden = componentProperties.get("showHidden", DEFAULT_SHOW_HIDDEN);
        boolean hideCurrent = componentProperties.get("hideCurrent", DEFAULT_SHOW_HIDDEN);

        for (int i = startLevel; i <= endLevel; i++) {
            Page pagetrail = getCurrentPage().getAbsoluteParent(i);
            if (pagetrail == null) {
                continue;
            }
            if (hideCurrent) {
                if (i == currentLevel - 1) {
                    continue;
                }
            }

            if (pagetrail != null && (!pagetrail.isHideInNav() || showHidden)) {

                HashMap<String, String> pagetrailvalues = new HashMap<String, String>();

                pagetrailvalues.put("path", pagetrail.getPath());
                pagetrailvalues.put("url", pagetrail.getPath().concat(DEFAULT_EXTENTION));
                pagetrailvalues.put("name", pagetrail.getName());
                pagetrailvalues.put("title", getPageNavTitle(pagetrail));

                Boolean currentPage = pagetrail.getPath().equals(getResourcePage().getPath());
                pagetrailvalues.put("current", BooleanUtils.toStringTrueFalse(currentPage));

                values.add(pagetrailvalues);
            }
        }

        componentProperties.put("values", values);

    }


}