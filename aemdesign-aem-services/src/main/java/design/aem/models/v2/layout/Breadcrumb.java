package design.aem.models.v2.layout;

import com.day.cq.wcm.api.Page;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static design.aem.utils.components.CommonUtil.getPageNavTitle;
import static design.aem.utils.components.CommonUtil.tryParseInt;
import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Breadcrumb extends BaseComponent {
    public void ready() {

        final String DEFAULT_DELIMITER = "";
        final String DEFAULT_TRAIL = "";
        final String DEFAULT_ARIA_ROLE = "navigation";
        final String DEFAULT_ARIA_LABEL = "breadcrumb";
        final boolean DEFAULT_SHOW_HIDDEN = false;
        final boolean DEFAULT_HIDE_CURRENT = false;
        final int DEFAULT_LEVEL_START = 1;
        final int DEFAULT_LEVEL_END = 1;
        final String FIELD_END_LEVEL = "endLevel";
        final String FIELD_START_LEVEL = "startLevel";

        setComponentFields(new Object[][]{
            {"delimiter", DEFAULT_DELIMITER},
            {"trail", DEFAULT_TRAIL},
            {FIELD_START_LEVEL, ""},
            {FIELD_END_LEVEL, ""},
            {"showHidden", DEFAULT_SHOW_HIDDEN},
            {"hideCurrent", DEFAULT_HIDE_CURRENT},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE},
            {FIELD_ARIA_LABEL, DEFAULT_ARIA_LABEL},
            {FIELD_VARIANT, DEFAULT_VARIANT},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        List<Map> values = new ArrayList<>();

        int startLevel = tryParseInt(componentProperties.get(FIELD_START_LEVEL, ""), DEFAULT_LEVEL_START);
        int endLevel = tryParseInt(componentProperties.get(FIELD_END_LEVEL, ""), DEFAULT_LEVEL_END);
        int currentLevel = getResourcePage().getDepth();

        if (isBlank(componentProperties.get(FIELD_END_LEVEL, ""))) {
            endLevel = currentLevel;
        }

        boolean showHidden = componentProperties.get("showHidden", DEFAULT_SHOW_HIDDEN);
        boolean hideCurrent = componentProperties.get("hideCurrent", DEFAULT_SHOW_HIDDEN);

        for (int i = startLevel; i <= endLevel; i++) {
            Page pagetrail = getCurrentPage().getAbsoluteParent(i);
            if (pagetrail == null || (hideCurrent && (i == currentLevel - 1))) {
                continue;
            }

            if (!pagetrail.isHideInNav() || showHidden) {

                HashMap<String, String> pagetrailvalues = new HashMap<>();

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
