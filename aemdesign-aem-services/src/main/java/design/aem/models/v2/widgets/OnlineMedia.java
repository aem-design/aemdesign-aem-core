package design.aem.models.v2.widgets;

import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.*;

public class OnlineMedia extends BaseComponent {
    @SuppressWarnings("Duplicates")
    protected void ready() {
        setComponentFields(new Object[][]{
            {FIELD_VARIANT, DEFAULT_VARIANT},
            {FIELD_MEDIA_PROVIDER, StringUtils.EMPTY, FIELD_DATA_MEDIA_PROVIDER},
            {FIELD_MEDIA_TITLE, StringUtils.EMPTY, FIELD_DATA_MEDIA_TITLE},
            {FIELD_MEDIA_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_ID},
            {FIELD_MEDIA_PARTNER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PARTNER_ID},
            {FIELD_MEDIA_PLAYER_ID, StringUtils.EMPTY, FIELD_DATA_MEDIA_PLAYER_ID},
            {FIELD_MEDIA_PROVIDER_URL, StringUtils.EMPTY, FIELD_SOURCE_ATTRIBUTE},
        });

        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }
}
