package design.aem.reports;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static design.aem.reports.PageComponentsReportCellValue.getResourceChildrenComponentsTreeList;

/**
 * Model for rendering component paths in a page when exporting content to csv
 */
@Model(adaptables = Resource.class)
public class PageComponentTreeReportCellCSVExporter {

    private static final Logger log = LoggerFactory.getLogger(PageComponentTreeReportCellCSVExporter.class);

    @Inject
    @Optional
    @Default(values = "")
    private String componentattribute;

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
