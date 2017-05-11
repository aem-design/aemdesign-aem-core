package design.aem.utils;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.AssetVersionManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

import static com.day.cq.dam.api.DamConstants.DC_TITLE;
import static org.apache.jackrabbit.vault.util.JcrConstants.*;

public final class AssetUtil {

    private static final Logger log = LoggerFactory.getLogger(AssetUtil.class);

    private AssetUtil() { }

    public static final String ASSET_OWNER = "asset.tag.dtmOwner";
    private static final String ASSET_METADATA_CHILD_PATH = JCR_CONTENT + "/metadata";

    public static AssetManager getAssetManager(ResourceResolver resourceResolver) {
        return resourceResolver.adaptTo(AssetManager.class);
    }

    public static AssetVersionManager getAssetVersionManager(ResourceResolver resourceResolver) {
        return resourceResolver.adaptTo(AssetVersionManager.class);
    }

    public static Asset getAsset(ResourceResolver resourceResolver, String path) {
        return getAssetManager(resourceResolver).getAsset(path);
    }

    public static Asset getAsset(Resource resource) {
        if (resource instanceof Asset) {
            return (Asset)resource;
        }
        return resource.adaptTo(Asset.class);
    }

    public static String getAssetTitle(Asset asset) {
        Resource metadata = asset.getChild(ASSET_METADATA_CHILD_PATH);
        if (metadata != null) {
            String title = metadata.adaptTo(ValueMap.class).get(DC_TITLE, String.class);
            if (title != null) {
                return title;
            }
        }

        return asset.getName();
    }

    /**
     * Looks for active asset ownership
     * @param asset
     * @return Authorizable or null when active owner not found
     */
    public static Authorizable getAssetOwner(Resource asset) {
        Authorizable owner = getActiveAuthorizableFromProperty(asset.getChild(ASSET_METADATA_CHILD_PATH), ASSET_OWNER);
        if (owner == null) {
            owner = getActiveAuthorizableFromProperty(asset.getChild(JCR_CONTENT), JCR_LAST_MODIFIED_BY);
            if (owner == null) {
                owner = getActiveAuthorizableFromProperty(asset, JCR_CREATED_BY);
            }
        }

        return owner;
    }

    private static Authorizable getActiveAuthorizableFromProperty(Resource resource, String property) {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        String authorizableID = valueMap.get(property, String.class);

        try {
            Session session = resource.getResourceResolver().adaptTo(Session.class);
            Authorizable authorizable = UserManagementUtil.getAuthorizable(session, authorizableID);
            if (UserManagementUtil.isActive(session, authorizable)) {
                return authorizable;
            }
        } catch (Exception ex) {
            log.error("could not resolve authorizable from {}, {}", property, ex);
        }

        return null;
    }

}
