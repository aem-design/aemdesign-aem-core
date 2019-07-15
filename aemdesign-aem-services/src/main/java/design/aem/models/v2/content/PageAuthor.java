package design.aem.models.v2.content;

import com.adobe.granite.security.user.UserPropertiesManager;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
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

public class PageAuthor extends ModelProxy {
    protected static final Logger LOGGER = LoggerFactory.getLogger(PageAuthor.class);

    protected ComponentProperties componentProperties = null;
    public ComponentProperties getComponentProperties() {
        return this.componentProperties;
    }

    protected void ready() throws Exception {

        com.adobe.granite.security.user.UserPropertiesService userPropertiesService = getSlingScriptHelper().getService(com.adobe.granite.security.user.UserPropertiesService.class);

        String pageAuthorFullName = "";
        String pageAuthorEmail = "";

        Node currentNode = getResource().adaptTo(Node.class);
        if (currentNode != null && userPropertiesService != null) {

            Session session = currentNode.getSession();
            UserPropertiesManager _userPropertiesManager = userPropertiesService.createUserPropertiesManager(session, getResourceResolver());
            UserManager _userManager = getResourceResolver().adaptTo(org.apache.jackrabbit.api.security.user.UserManager.class);

            String pageAuthorUser = getResourcePage().getLastModifiedBy();

            if (isNotBlank(pageAuthorUser)) {
                pageAuthorFullName = getUserFullName(_userManager, _userPropertiesManager, pageAuthorUser, "");
                pageAuthorEmail = getUserEmail(_userManager, _userPropertiesManager, pageAuthorUser, "");

                if (isNotBlank(pageAuthorEmail)) {
                    pageAuthorEmail = MessageFormat.format("mailto:{}",pageAuthorEmail);
                } else {
                    pageAuthorEmail = "#";
                }
            } else {
                pageAuthorFullName = session.getUserID();
            }
        }

        setComponentFields(new Object[][]{
                {"author", pageAuthorFullName},
                {"authorUrl", pageAuthorEmail},
                {FIELD_VARIANT, DEFAULT_VARIANT}
        });

        componentProperties = ComponentsUtil.getComponentProperties(
                this,
                componentFields,
                DEFAULT_FIELDS_STYLE,
                DEFAULT_FIELDS_ACCESSIBILITY);
    }
}