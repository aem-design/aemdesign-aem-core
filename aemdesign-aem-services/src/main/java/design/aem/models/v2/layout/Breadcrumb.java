package design.aem.models.v2.layout;

import com.day.cq.wcm.api.Page;
import design.aem.components.ComponentProperties;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
    protected static final String FIELD_DELIMITER = "delimiter";
    protected static final String FIELD_END_LEVEL = "endLevel";
    protected static final String FIELD_HIDE_CURRENT = "hideCurrent";
    protected static final String FIELD_SHOW_HIDDEN = "showHidden";
    protected static final String FIELD_START_LEVEL = "startLevel";
    protected static final String FIELD_TRAIL = "trail";

    protected static final String DEFAULT_DELIMITER = StringUtils.EMPTY;
    protected static final String DEFAULT_TRAIL = StringUtils.EMPTY;
    protected static final String DEFAULT_ARIA_ROLE = "navigation";
    protected static final String DEFAULT_ARIA_LABEL = "breadcrumb";
    protected static final boolean DEFAULT_SHOW_HIDDEN = false;
    protected static final boolean DEFAULT_HIDE_CURRENT = false;
    protected static final int DEFAULT_LEVEL_START = 1;
    protected static final int DEFAULT_LEVEL_END = 1;

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);

        List<Map<String, String>> pageTrails = new ArrayList<>();

        int startLevel = tryParseInt(componentProperties.get(FIELD_START_LEVEL, StringUtils.EMPTY), DEFAULT_LEVEL_START);
        int endLevel = tryParseInt(componentProperties.get(FIELD_END_LEVEL, StringUtils.EMPTY), DEFAULT_LEVEL_END);
        int currentLevel = getResourcePage().getDepth();

        if (isBlank(componentProperties.get(FIELD_END_LEVEL, StringUtils.EMPTY))) {
            endLevel = currentLevel;
        }

        boolean showHidden = componentProperties.get(FIELD_SHOW_HIDDEN, DEFAULT_SHOW_HIDDEN);
        boolean hideCurrent = componentProperties.get(FIELD_HIDE_CURRENT, DEFAULT_SHOW_HIDDEN);

        for (int i = startLevel; i <= endLevel; i++) {
            Page page = getCurrentPage().getAbsoluteParent(i);

            if (page == null || (hideCurrent && (i == currentLevel - 1))) {
                continue;
            }

            if (!page.isHideInNav() || showHidden) {
                pageTrails.add(getPageTrail(page));
            }
        }

        componentProperties.put("values", pageTrails);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_DELIMITER, DEFAULT_DELIMITER},
            {FIELD_END_LEVEL, StringUtils.EMPTY},
            {FIELD_HIDE_CURRENT, DEFAULT_HIDE_CURRENT},
            {FIELD_SHOW_HIDDEN, DEFAULT_SHOW_HIDDEN},
            {FIELD_START_LEVEL, StringUtils.EMPTY},
            {FIELD_TRAIL, DEFAULT_TRAIL},
            {FIELD_ARIA_ROLE, DEFAULT_ARIA_ROLE},
            {FIELD_ARIA_LABEL, DEFAULT_ARIA_LABEL},
        });
    }

    protected Map<String, String> getPageTrail(Page page) {
        Map<String, String> values = new HashMap<>();

        values.put("path", page.getPath());
        values.put("url", page.getPath().concat(DEFAULT_EXTENTION));
        values.put("name", page.getName());
        values.put("title", getPageNavTitle(page));

        Boolean currentPage = page.getPath().equals(getResourcePage().getPath());
        values.put("current", BooleanUtils.toStringTrueFalse(currentPage));

        return values;
    }
}
