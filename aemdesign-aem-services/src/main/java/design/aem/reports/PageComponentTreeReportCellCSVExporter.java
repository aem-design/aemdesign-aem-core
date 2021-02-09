package design.aem.reports;

import com.adobe.acs.commons.reports.api.ReportCellCSVExporter;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static design.aem.reports.PageComponentsReportCellValue.getResourceChildrenComponentsTreeList;
import static design.aem.utils.components.CommonUtil.RESOURCE_TYPE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.apache.sling.models.annotations.Optional;

/**
 * Model for rendering component paths in a page when exporting content to csv
 */
@Model(adaptables = Resource.class)
public class PageComponentTreeReportCellCSVExporter implements ReportCellCSVExporter {

    private static final Logger log = LoggerFactory.getLogger(PageComponentTreeReportCellCSVExporter.class);

    @Inject
    @Optional
    @Default(values = "")
    private String componentattribute;

    @Override
    public String getValue(Object result) {

        Resource resource = (Resource)result;
        ComponentManager compMgr = resource.getResourceResolver().adaptTo(ComponentManager.class);
        Collection<Component> components = compMgr.getComponents();

        List<PageComponentsReportCellValue.TreeNode> children = new LinkedList();
        String returnValue = "";

        log.debug("Finding components in {} with componentattribute: {}", resource.getPath(), componentattribute);

        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {

            Page page = resource.adaptTo(Page.class);
            Resource pageContent = page.getContentResource();

            children.addAll(getResourceChildrenComponentsTreeList(pageContent, componentattribute, components));
            if (children.size() == 1) {
                returnValue = children.get(0).toString();
            } else if (children.size() > 1) {
                returnValue = StringUtils.join(children, "\r");
            }

        }

        log.debug("Found components: {}", children);

        return returnValue.trim();

    }

}
