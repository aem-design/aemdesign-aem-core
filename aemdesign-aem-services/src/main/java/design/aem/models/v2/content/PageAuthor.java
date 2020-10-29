package design.aem.models.v2.content;

import com.adobe.granite.security.user.UserPropertiesManager;
import com.adobe.granite.security.user.UserPropertiesService;
import design.aem.models.BaseComponent;
import design.aem.utils.components.ComponentsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.text.MessageFormat;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.SecurityUtil.getUserEmail;
import static design.aem.utils.components.SecurityUtil.getUserFullName;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PageAuthor extends BaseComponent {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageAuthor.class);

    protected static final String AUTHOR_FULL_NAME = "authorFullName";
    protected static final String AUTHOR_EMAIL = "authorEmail";

    protected void ready() {
        componentProperties = ComponentsUtil.getComponentProperties(
            this,
            componentFields,
            DEFAULT_FIELDS_STYLE,
            DEFAULT_FIELDS_ACCESSIBILITY);
    }

    @Override
    protected void setFields() {
        setComponentFields(new Object[][]{
            {"author", componentDefaults.get(AUTHOR_FULL_NAME)},
            {"authorUrl", componentDefaults.get(AUTHOR_EMAIL)},
            {FIELD_VARIANT, DEFAULT_VARIANT}
        });
    }

    @Override
    protected void setFieldDefaults() {
        UserPropertiesService userPropertiesService = getSlingScriptHelper().getService(UserPropertiesService.class);

        String pageAuthorFullName = StringUtils.EMPTY;
        String pageAuthorEmail = StringUtils.EMPTY;

        Node currentNode = getResource().adaptTo(Node.class);

        if (currentNode != null && userPropertiesService != null) {
            try {
                Session session = currentNode.getSession();
                UserPropertiesManager userPropertiesManager = userPropertiesService.createUserPropertiesManager(session, getResourceResolver());
                UserManager userManager = getResourceResolver().adaptTo(org.apache.jackrabbit.api.security.user.UserManager.class);

                String pageAuthorUser = getResourcePage().getLastModifiedBy();

                if (isNotBlank(pageAuthorUser)) {
                    pageAuthorFullName = getUserFullName(userManager, userPropertiesManager, pageAuthorUser, StringUtils.EMPTY);
                    pageAuthorEmail = getUserEmail(userManager, userPropertiesManager, pageAuthorUser, StringUtils.EMPTY);

                    if (isNotBlank(pageAuthorEmail)) {
                        pageAuthorEmail = MessageFormat.format("mailto:{0}", pageAuthorEmail);
                    } else {
                        pageAuthorEmail = "#";
                    }
                } else {
                    pageAuthorFullName = session.getUserID();
                }
            } catch (Exception ex) {
                LOGGER.warn("Unexpected error occurred: {}", ex);
            }
        }

        componentDefaults.put(AUTHOR_FULL_NAME, pageAuthorFullName);
        componentDefaults.put(AUTHOR_EMAIL, pageAuthorEmail);
    }
}
