package design.aem.models.v2.layout;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Arrays;
import java.util.List;

import static design.aem.utils.components.ComponentsUtil.FIELD_STYLE_COMPONENT_ID;
import static design.aem.utils.components.SecurityUtil.isUserMemberOf;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ContentBlockLock extends ContentBlock {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContentBlockLock.class);

    protected static final String FIELD_LOCKED = "islocked";

    @Override
    protected void ready() throws Exception {
        super.ready();

        Node resourceNode = getResource().adaptTo(Node.class);
        String instanceName = getComponent().getCellName();

        if (resourceNode != null) {
            instanceName = resourceNode.getName();
        }

        String componentId = componentProperties.get(FIELD_STYLE_COMPONENT_ID, StringUtils.EMPTY);

        if (isNotEmpty(componentId)) {
            instanceName = componentId;
        }

        componentProperties.put("instanceName", instanceName);

        final Authorizable authorizable = getResourceResolver().adaptTo(Authorizable.class);

        final List<String> groups = Arrays.asList(componentProperties.get("groups", new String[]{
            "administrators",
        }));

        if (isUserMemberOf(authorizable, groups)) {
            componentProperties.put(FIELD_LOCKED, false);
        }
    }

    @Override
    protected void setFields() {
        super.setFields();

        setComponentFields(new Object[][]{
            {FIELD_LOCKED, true},
        });
    }
}
