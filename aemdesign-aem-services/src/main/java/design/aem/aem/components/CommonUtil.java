package design.aem.aem.components;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import java.util.HashMap;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.day.cq.wcm.api.Page;
import javax.jcr.RepositoryException;
import java.util.Iterator;

/**
 * Create static class for common functions
 *
 * Created by yawly on 5/15/2016.
 */
public final class CommonUtil {

    public static String compileSubstituteMessage(String formatTagPath, String defaultFormat,
                                                  HashMap<String, Object> componentProperties, Resource resource)
    {
        if (componentProperties == null) {
            return StringUtils.EMPTY;
        }

        String formatTagFieldPath = formatTagPath;

        String titleFormat = defaultFormat;

        if (isNotEmpty(formatTagFieldPath)) {
            titleFormat = getTagValueAsAdmin(formatTagPath, resource);
        }

        return compileMapMessage(titleFormat,componentProperties);
    }

    private static String getTagValueAsAdmin(String tagPath, Resource resource) {
        String tagValue="";

        if (isEmpty(tagPath)) {
            return tagValue;
        }

        TagManager _adminTagManager = resource.getResourceResolver().adaptTo(TagManager.class);
        Tag jcrTag = _adminTagManager.resolve(tagPath);
        if (jcrTag != null) {
            tagValue = jcrTag.getName();
            ValueMap tagVM = jcrTag.adaptTo(Resource.class).getValueMap();

            if (tagVM != null) {
                if (tagVM.containsKey("value")) {
                    tagValue = tagVM.get("value", jcrTag.getName());
                }
            }
        }

        return tagValue;
    }

    private static String compileMapMessage(String template, Map<String, Object> map) {

        if (isEmpty(template) || map == null) {
            return "";
        }

        StrSubstitutor sub = new StrSubstitutor(map);
        return sub.replace(template);
    }


    public static Integer currentPageNumberByContentPath(Page page, String contentPath) throws RepositoryException {
        if(page == null) {
            return -1;
        }

        Page parentPage = page.getParent();
        if(parentPage == null) {
            return -1;
        }

        Integer pageCount = 0;

        Iterator<Page> pageIterator = parentPage.listChildren();

        while(pageIterator.hasNext()) {
            Page currentPage = pageIterator.next();

            Resource issueResource = currentPage.getContentResource(contentPath);
            if (issueResource != null) {
                pageCount++;
            }

            if(currentPage.equals(page)) {
                return pageCount;
            }
        }
        return pageCount;
    }
}