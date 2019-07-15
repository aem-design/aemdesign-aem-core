package design.aem.models.v2.content;

import com.adobe.granite.security.user.UserPropertiesManager;
import design.aem.components.ComponentProperties;
import design.aem.models.ModelProxy;
import design.aem.utils.components.ComponentsUtil;
import org.apache.jackrabbit.api.security.user.UserManager;

import javax.jcr.Node;
import javax.jcr.Session;
import java.text.MessageFormat;

import static design.aem.utils.components.ComponentsUtil.*;
import static design.aem.utils.components.SecurityUtil.getUserEmail;
import static design.aem.utils.components.SecurityUtil.getUserFullName;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PageAuthor extends ModelProxy {

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
            UserPropertiesManager userPropertiesManager = userPropertiesService.createUserPropertiesManager(session, getResourceResolver());
            UserManager userManager = getResourceResolver().adaptTo(org.apache.jackrabbit.api.security.user.UserManager.class);

            String pageAuthorUser = getResourcePage().getLastModifiedBy();

            if (isNotBlank(pageAuthorUser)) {
                pageAuthorFullName = getUserFullName(userManager, userPropertiesManager, pageAuthorUser, "");
                pageAuthorEmail = getUserEmail(userManager, userPropertiesManager, pageAuthorUser, "");

                if (isNotBlank(pageAuthorEmail)) {
                    pageAuthorEmail = MessageFormat.format("mailto:{0}",pageAuthorEmail);
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