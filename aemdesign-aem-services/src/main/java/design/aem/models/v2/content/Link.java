package design.aem.models.v2.content;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import design.aem.components.ComponentProperties;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.ConstantsUtil.DEFAULT_EXTENTION;
import static design.aem.utils.components.I18nUtil.getDefaultLabelIfEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class Link extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Link.class);

    private ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    @Override
    public void activate() throws Exception {

        I18n _i18n = new I18n(getRequest());

        final String FIELD_LINKURL = "linkUrl";
        final String DEFAULT_LINKURL = "#";
        final String DEFAULT_LINK_ICON_DIRECTION = "left";
        final String DEFAULT_I18N_CATEGORY = "link";
        final String DEFAULT_I18N_LABEL = "linklabel";


        // {
        //   1 required - property name,
        //   2 required - default value,
        //   3 optional - name of component attribute to add value into
        //   4 optional - canonical name of class for handling multivalues, String or Tag
        // }
        Object[][] componentFields = {
                {"linkTarget", StringUtils.EMPTY, "target"},
                {FIELD_LINKURL, StringUtils.EMPTY},
                {FIELD_VARIANT, DEFAULT_VARIANT},
                {"linkId", getResource().getPath()},
                {"linkIcon", new String[]{}, "", Tag.class.getCanonicalName()},
                {"linkIconDirection", DEFAULT_LINK_ICON_DIRECTION},
                {"label", getDefaultLabelIfEmpty("",DEFAULT_I18N_CATEGORY,DEFAULT_I18N_LABEL,DEFAULT_I18N_CATEGORY,_i18n)},
                {COMPONENT_INPAGEPATH, getComponentInPagePath(getResource().adaptTo(Node.class)),"data-layer-componentpath"},
        };

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY,
                DEFAULT_FIELDS_ANALYTICS,
                DEFAULT_FIELDS_ATTRIBUTES);

        String linkUrl = componentProperties.get(FIELD_LINKURL, StringUtils.EMPTY);

        if (isNotEmpty(linkUrl)) {
            Resource linkResource = getResourceResolver().resolve(linkUrl);
            if (!ResourceUtil.isNonExistingResource(linkResource) && linkResource.isResourceType(NameConstants.NT_PAGE)) {
                if (!linkUrl.endsWith(DEFAULT_EXTENTION) && !linkUrl.contains(DEFAULT_LINKURL)) {
                    linkUrl = linkUrl.concat(DEFAULT_EXTENTION);
                }
            }

            componentProperties.attr.add("href", linkUrl);

            componentProperties.put(COMPONENT_ATTRIBUTES, buildAttributesString(componentProperties.attr.getData(), null));


        }


    }



}